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



public class MainActivity extends ActionBarActivity implements SensorEventListener {

    public static final String PREF_1 = "HeightStore";
    TextView tv, altitude, baro;
    float height;
    private ImageView image;
    private float currentDegree = 0;
    private int heightdif = 0;
    private SensorManager sm;


    private boolean hasBarometer = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PackageManager pm = this.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER)) {
            hasBarometer = false;
        }
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.imageViewCompass);
        //image.setOnTouchListener((View.OnTouchListener)this);
        tv = (TextView) findViewById(R.id.tv);
        altitude = (TextView) findViewById(R.id.alt);
        baro = (TextView) findViewById(R.id.bar);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        SharedPreferences settings = getSharedPreferences(PREF_1, 0);
        heightdif = settings.getInt("heightdif", 0);
    }


    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREF_1, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("heightdif", heightdif);
        editor.apply();
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
        /*Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Resume",Toast.LENGTH_LONG);
                    toast.show();*/

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

    public void onSensorChanged(SensorEvent event) {
        float degree;
        float pressure;
        if (Sensor.TYPE_PRESSURE == event.sensor.getType()) {
            pressure = event.values[0];

            if (hasBarometer) {
                baro.setText(getString(R.string.pressure) + ": " + (int) pressure + "hPa");

                height = (SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure)) + heightdif;

                String text;
                if (heightdif != 0) {
                    if (heightdif > 0) {
                        text = getString(R.string.height) + ": " + Integer.toString((int) height) + "m" + " (+" + Integer.toString(heightdif) + ")";
                    } else {
                        text = getString(R.string.height) + ": " + Integer.toString((int) height) + "m" + " (" + Integer.toString(heightdif) + ")";

                    }
                } else {
                    text = getString(R.string.height) + ": " + Integer.toString((int) height) + "m";
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
                if (value == h) {

                } else {
                    if (value > height) {
                        heightdif = value - ((int) height);
                    } else {
                        heightdif = -(((int) height) - value);
                    }
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
}

