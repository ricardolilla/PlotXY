package com.example.ricardo.plotxy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class TabActivity extends AppCompatActivity {
    private boolean indIsActive;
    private boolean servIsActive;
    private boolean hidIsActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        Bundle bundle = getIntent().getExtras();
        indIsActive=bundle.getBoolean("IND");
        servIsActive=bundle.getBoolean("SERV");
        hidIsActive=bundle.getBoolean("HID");

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Principal");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Indústria 4.0");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Service");
        host.addTab(spec);

        //Tab 4
        spec = host.newTabSpec("Tab Four");
        spec.setContent(R.id.tab4);
        spec.setIndicator("Hidráulica");
        host.addTab(spec);

        host.getTabWidget().getChildTabViewAt(1).setEnabled(indIsActive);
        host.getTabWidget().getChildTabViewAt(2).setEnabled(servIsActive);
        host.getTabWidget().getChildTabViewAt(3).setEnabled(hidIsActive);

    }
}
