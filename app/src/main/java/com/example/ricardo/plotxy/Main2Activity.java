package com.example.ricardo.plotxy;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

public class Main2Activity extends AppCompatActivity implements SensorEventListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main2 Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.ricardo.plotxy/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main2 Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.ricardo.plotxy/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * A simple formatter to convert bar indexes into sensor names.
     */
    private class APRIndexFormat extends Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            Number num = (Number) obj;

            // using num.intValue() will floor the value, so we add 0.5 to round instead:
            int roundNum = (int) (num.floatValue() + 0.5f);
            switch (roundNum) {
                case 0:
                    toAppendTo.append("Azimuth");
                    break;
                case 1:
                    toAppendTo.append("Pitch");
                    break;
                case 2:
                    toAppendTo.append("Roll");
                    break;
                default:
                    toAppendTo.append("Unknown");
            }
            return toAppendTo;
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return null;  // We don't use this so just return null for now.
        }
    }


    private static final int HISTORY_SIZE = 30;            // number of points to plot in history
    private SensorManager sensorMgr = null;
    private Sensor orSensor = null;

    private XYPlot aprLevelsPlot = null;
    private XYPlot aprHistoryPlot = null;

    private CheckBox hwAcceleratedCb;
    private CheckBox showFpsCb;
    private SimpleXYSeries aprLevelsSeries = null;
    private SimpleXYSeries azimuthHistorySeries = null;
    private SimpleXYSeries pitchHistorySeries = null;
    private SimpleXYSeries rollHistorySeries = null;
    private SimpleXYSeries pressHistorySeries = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // setup the APR History plot:
        aprHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);

        azimuthHistorySeries = new SimpleXYSeries("Azimuth");
        azimuthHistorySeries.useImplicitXVals();
        pitchHistorySeries = new SimpleXYSeries("Pitch");
        pitchHistorySeries.useImplicitXVals();
        rollHistorySeries = new SimpleXYSeries("Roll");
        rollHistorySeries.useImplicitXVals();
        pressHistorySeries = new SimpleXYSeries("Pressão");
        pressHistorySeries.useImplicitXVals();

        aprHistoryPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(0, 5, BoundaryMode.FIXED);
        LineAndPointFormatter linAzi=new LineAndPointFormatter(Color.rgb(100,100,200),Color.BLACK,null,null);
        aprHistoryPlot.addSeries(azimuthHistorySeries, linAzi);
        LineAndPointFormatter linPitch=new LineAndPointFormatter(Color.rgb(100,200,100),null,null,null);
        aprHistoryPlot.addSeries(pitchHistorySeries, linPitch);
        LineAndPointFormatter linRoll=new LineAndPointFormatter(Color.rgb(100,100,200),null,null,null);
        aprHistoryPlot.addSeries(rollHistorySeries, linRoll);
        LineAndPointFormatter linPressao=new LineAndPointFormatter(Color.BLACK,null,null,null);
        aprHistoryPlot.addSeries(pressHistorySeries, linPressao);
        aprHistoryPlot.setDomainStepValue(5);
        aprHistoryPlot.setTicksPerRangeLabel(3);
        aprHistoryPlot.setDomainLabel("Sample Index");
        aprHistoryPlot.getDomainLabelWidget().pack();
        aprHistoryPlot.setRangeLabel("Angle (Degs)");
        aprHistoryPlot.getRangeLabelWidget().pack();

        // setup checkboxes:
        //final PlotStatistics levelStats = new PlotStatistics(1000, false);
        //final PlotStatistics histStats = new PlotStatistics(1000, false);

        //aprHistoryPlot.addListener(histStats);


        sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        for (Sensor sensor : sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION)) {
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                orSensor = sensor;
            }
        }

        // if we can't access the orientation sensor then exit:
        if (orSensor == null) {
            System.out.println("Failed to attach to orSensor.");
            cleanup();
        }

        sensorMgr.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_UI);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void cleanup() {
        // aunregister with the orientation sensor before exiting:
        sensorMgr.unregisterListener(this);
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // update instantaneous data:
        //Number[] series1Numbers = {event.values[0], event.values[1], event.values[2]};
        //aprLevelsSeries.setModel(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        Log.d("teste",String.valueOf(event.values[0]));
        // get rid the oldest sample in history:
        if (rollHistorySeries.size() > HISTORY_SIZE) {
            rollHistorySeries.removeFirst();
            pitchHistorySeries.removeFirst();
            azimuthHistorySeries.removeFirst();
            pressHistorySeries.removeFirst();
        }

        // add the latest history sample:
        azimuthHistorySeries.addLast(null, event.values[0]);
        pitchHistorySeries.addLast(null, event.values[1]);
        rollHistorySeries.addLast(null, event.values[2]);
        pressHistorySeries.addLast(null,0);
        // redraw the Plots:
        aprHistoryPlot.redraw();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
