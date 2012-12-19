package com.main.tracker;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Scanner;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.location.*;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.content.*;
import android.net.*;
import org.apache.*;

public class MainActivity extends Activity implements LocationListener {

	private CheckBox button;
	private EditText status;
	private LocationManager lm;
	String nl = System.getProperty("line.separator");
	
	public void enableGPS() {
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
	}
	
	public void disableGPS() {
		lm.removeUpdates(this);
	}
	
	public void sendCoordOnServer(String nurl) {
		try {
			URL url = new URL(nurl);
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader cin = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder str = null;
			String s=cin.readLine();
		} catch (Exception e) {
			status.setText(status.getText()+nl+"Can't send coords");
			e.printStackTrace();
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        button = (CheckBox) findViewById(R.id.checkBox1);
        status = (EditText) findViewById(R.id.editText1);
        button.setOnCheckedChangeListener( 
        		new OnCheckedChangeListener() {
					
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							enableGPS();
						}
						else {
							disableGPS();
						}	
					}
				} 
        );
       
         
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	public void onLocationChanged(Location location) {
		final String lat = String.valueOf(location.getLatitude());
		final String lng = String.valueOf(location.getLongitude());
		new Thread() {
			public void run(){
				sendCoordOnServer("http://tracker.yoihito.tk/save.php?x="+lat+"&y="+lng);
			}
		}.start();
		status.setText("Location update enabled."+Calendar.getInstance().getTime().toGMTString()+nl+"lat = "+lat+nl+"lng = "+lng);
	}

	public void onProviderDisabled(String provider) {
		status.setText("Location update disabled.");
	}

	public void onProviderEnabled(String provider) {
		status.setText("Location update enabled.");		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

    
}
