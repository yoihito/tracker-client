package com.main.tracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

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
	
	public synchronized void sendCoordOnServer(String nurl) {
		try {
			URL url = new URL(nurl);
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader cin = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String s=cin.readLine();
			if (s.equals("success"))
				return;
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
