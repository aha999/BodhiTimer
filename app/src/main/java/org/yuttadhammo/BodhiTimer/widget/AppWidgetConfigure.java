/*
    This file is part of Bodhi Timer.

    Bodhi Timer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Bodhi Timer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Bodhi Timer.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.yuttadhammo.BodhiTimer.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import androidx.appcompat.app.AppCompatActivity;

import org.yuttadhammo.BodhiTimer.R;

import java.util.Arrays;

public class AppWidgetConfigure extends AppCompatActivity implements OnClickListener {

    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        setContentView(R.layout.widget_config);

        ImageButton ib1 = findViewById(R.id.wtheme1);
        ImageButton ib2 = findViewById(R.id.wtheme2);
        ImageButton ib3 = findViewById(R.id.wtheme3);
        ImageButton ib4 = findViewById(R.id.wtheme4);

        ib1.setOnClickListener(this);
        ib2.setOnClickListener(this);
        ib3.setOnClickListener(this);
        ib4.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int theme = R.drawable.widget_background_black_square;
        switch (v.getId()) {
            case R.id.wtheme2:
                theme = R.drawable.widget_background_black;
                break;
            case R.id.wtheme3:
                theme = R.drawable.widget_background_white_square;
                break;
            case R.id.wtheme4:
                theme = R.drawable.widget_background_white;
                break;
        }

        final Context context = AppWidgetConfigure.this;
        savePref(context, mAppWidgetId, theme);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.appwidget);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        this.sendBroadcast(new Intent(BodhiAppWidgetProvider.ACTION_CLOCK_UPDATE));
        finish();
    }

    // Write the prefix to the SharedPreferences object for this widget
    private void savePref(Context context, int appWidgetId, int theme) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String widgetIds = prefs.getString("widgetIds", null);
        if (widgetIds == null) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName appWidgets = new ComponentName(context.getPackageName(), "org.yuttadhammo.BodhiTimer.widget.BodhiAppWidgetProvider");
            int[] ids = appWidgetManager.getAppWidgetIds(appWidgets);
            widgetIds = ids.length > 0 ? Arrays.toString(ids).replace("[", ",").replace("]", ",").replaceAll(" ", "") : ",";
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("widget_theme_" + appWidgetId, theme);
        if (!widgetIds.contains("," + appWidgetId + ","))
            widgetIds += +appWidgetId + ",";
        editor.putString("widgetIds", widgetIds);
        editor.apply();
    }

    static void deletePref(Context context, int appWidgetId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String widgetIds = prefs.getString("widgetIds", null);
        if (widgetIds == null) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName appWidgets = new ComponentName(context.getPackageName(), "org.yuttadhammo.BodhiTimer.widget.BodhiAppWidgetProvider");
            int[] ids = appWidgetManager.getAppWidgetIds(appWidgets);
            widgetIds = ids.length > 0 ? Arrays.toString(ids).replace("[", ",").replace("]", ",").replaceAll(" ", "") : ",";
        }

        SharedPreferences.Editor editor = prefs.edit();
        widgetIds = widgetIds.replace("," + appWidgetId + ",", ",");
        editor.putString("widgetIds", widgetIds);
        editor.remove("widget_theme_" + appWidgetId);
        editor.apply();
    }
}
