package com.example.testgps;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        toggleGPS(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void toggleGPS(boolean enable) {
        String provider = Settings.Secure.getString(getContentResolver(), 
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        
        if(provider.contains("gps") == enable) {
            return; // the GPS is already in the requested state
        }
        Toast.makeText(this, "hola", Toast.LENGTH_SHORT).show();
        final Intent poke = new Intent();
        poke.setClassName("com.android.settings", 
            "com.android.settings.widget.SettingsAppWidgetProvider");
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        poke.setData(Uri.parse("3"));
        this.getApplicationContext().sendBroadcast(poke);
    }

}
