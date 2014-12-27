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

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class SettingsActivity extends PreferenceActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkBaro()) {
            addPreferencesFromResource(R.xml.preferences_full);
        } else {
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    protected boolean checkBaro() {
        PackageManager pm = this.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
    }
}
