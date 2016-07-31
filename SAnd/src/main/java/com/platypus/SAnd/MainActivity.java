/*
* Copyright (C) 2014 kas70
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.platypus.SAnd;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements SensorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREFS = "PrefsFile";
    TextView tv, altitude, baro;
    double height;
    private ImageView image;
    private float currentDegree = 0;
    private int heightdif = 0;
    private SensorManager sm;
    private boolean unitIsMetric = true;
    private boolean hasBarometer = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if device has barometer
        PackageManager pm = this.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER)) {
            hasBarometer = false;
        }
        //set layout
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.imageViewCompass);
        tv = (TextView) findViewById(R.id.tv);
        altitude = (TextView) findViewById(R.id.alt);
        baro = (TextView) findViewById(R.id.bar);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        //restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        heightdif = settings.getInt("heightdif", 0);
        applyHeightcorSetting();
        unitIsMetric = settings.getBoolean("unit", true);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //store preferences
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("heightdif", heightdif);
        editor.putBoolean("unit", unitIsMetric);
        editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater mf = getMenuInflater();
        if (hasBarometer) {
            mf.inflate(R.menu.main_full, menu);
        } else {
            mf.inflate(R.menu.main, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop the listener and save battery
        sm.unregisterListener(this);
    }

    //needs refactoring, bad code
    public void onSensorChanged(SensorEvent event) {
        float degree;
        float pressure;
        if (Sensor.TYPE_PRESSURE == event.sensor.getType()) {
            pressure = event.values[0];
            if (hasBarometer) {
                if (unitIsMetric) {
                    baro.setText(getString(R.string.pressure) + ": " + (int) pressure + "hPa");
                } else {
                    double h = (double) pressure;
                    h = h * 0.02952998751;
                    baro.setText(getString(R.string.pressure) + ": " + (int) h + "inHg");
                }

                if (unitIsMetric) {
                    height = (SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure)) + heightdif;
                } else {
                    height = ((SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure)) + heightdif) * 3.2808;
                }

                String text;
                if (heightdif != 0) {
                    if (heightdif > 0) {
                        if (unitIsMetric) {
                            text = getString(R.string.height) + ": " + Integer.toString((int) height) + "m" + " (+" + Integer.toString(heightdif) + ")";
                        } else {
                            text = getString(R.string.height) + ": " + Integer.toString((int) height) + "ft" + " (+" + Integer.toString(heightdif) + ")";
                        }
                    } else {
                        if (unitIsMetric) {
                            text = getString(R.string.height) + ": " + Integer.toString((int) height) + "m" + " (" + Integer.toString(heightdif) + ")";
                        } else {
                            text = getString(R.string.height) + ": " + Integer.toString((int) height) + "ft" + " (" + Integer.toString(heightdif) + ")";

                        }

                    }
                } else {
                    if (unitIsMetric) {
                        text = getString(R.string.height) + ": " + Integer.toString((int) height) + "m";
                    } else {
                        text = getString(R.string.height) + ": " + Integer.toString((int) height) + "ft";

                    }
                }

                altitude.setText(text);
            } else {
                baro.setText(getString(R.string.nobarometer));
                altitude.setText("");
            }
        } else {
            degree = Math.round(event.values[0]);
            tv.setText(getString(R.string.orientation) + ": " + Integer.toString((int) degree) + "°");

            //animation for rotating the image
            RotateAnimation animation = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(210);
            animation.setFillAfter(true);
            image.startAnimation(animation);
            currentDegree = -degree;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close:
                finish();
                return true;
            case R.id.changeheight:
                showHeightDialog();
                return true;
            case R.id.deleteHeightDiff:
                heightdif = 0;
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, getString(R.string.heightcor_deleted), Toast.LENGTH_LONG);
                toast.show();
                return true;
            case R.id.settings:
                openSettings(findViewById(R.id.settings));
                return true;
            /*
            case R.id.tracker:
                openTracker(findViewById(R.id.tracker));
                return true;
                */
        }
        return super.onOptionsItemSelected(item);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    /*
    public void openTracker(View view) {
        Intent intent = new Intent(MainActivity.this, TrackerActivity.class);
        startActivity(intent);
    }
    */

    public void showHeightDialog() {
        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle(getString(R.string.action_adjustheight));
        d.setContentView(R.layout.dialog);
        Button ok = (Button) d.findViewById(R.id.button1);
        Button cancel = (Button) d.findViewById(R.id.button2);
        Button decrease = (Button) d.findViewById(R.id.decreaseHeight);
        Button increase = (Button) d.findViewById(R.id.increaseHeight);
        final EditText et = (EditText) d.findViewById(R.id.editHeight);
        final int h = (int) height;
        et.setText(String.valueOf(h));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et.getText().toString();
                int value = Integer.parseInt(s);
                if (value > height) {
                    heightdif = value - ((int) height);
                } else if (height > value) {
                    heightdif = -(((int) height) - value);
                }
                d.dismiss();
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, getString(R.string.heightcor_set), Toast.LENGTH_LONG);
                toast.show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });


        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s = et.getText().toString();
                int value = Integer.parseInt(s);
                value--;
                s = Integer.toString(value);
                et.setText(s);
            }
        });

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = et.getText().toString();
                int value = Integer.parseInt(s);
                value++;
                s = Integer.toString(value);
                et.setText(s);
            }
        });

        d.show();
    }

    public void applyHeightcorSetting() {
        Boolean deleteHeightcor = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("PREF_HEIGHT", false);
        if (deleteHeightcor) heightdif = 0;
    }

    //set the measurement unit
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String string = sp.getString("PREF_UNIT", "none");
        unitIsMetric = string.equals("metric");
        if(!unitIsMetric) heightdif *= 3.2808;
                else heightdif /= 3.2808;
    }
}