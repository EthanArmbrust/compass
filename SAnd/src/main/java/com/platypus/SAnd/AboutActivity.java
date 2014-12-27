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

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;


public class AboutActivity extends Activity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }

    public void onStart(){
        super.onStart();

        Resources res = getResources();
        TextView tv;

        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            String v = res.getString(R.string.about_version, info.versionName, info.versionCode);
            tv = (TextView) findViewById(R.id.about_version);
            tv.setText(v);
        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        tv = (TextView) findViewById(R.id.about_license);
        tv.setText(R.string.about_license);


        String link = res.getString(R.string.about_link);
        String linktext = res.getString(R.string.about_more, link);
        tv = (TextView) findViewById(R.id.about_more);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(Html.fromHtml(linktext));
    }
}
