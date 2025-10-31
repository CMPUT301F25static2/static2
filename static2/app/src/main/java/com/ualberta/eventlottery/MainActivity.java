package com.ualberta.eventlottery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityEventLotteryMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EventLottery";

    private ActivityEventLotteryMainBinding binding;

    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEventLotteryMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_event_lottery_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        isAdmin = false; // Change to false to go to entrant screens
        if (isAdmin){
            Intent myIntent = new Intent(MainActivity.this, AdminMainActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        // https://stackoverflow.com/questions/36079523/launch-app-or-play-store-by-scanning-qr-code
        // https://www.google.com/search?q=deeplink+testing+android+emulator&oq=deeplink+testing+android+emulator&gs_lcrp=EgZjaHJvbWUyBggAEEUYOTIICAEQABgWGB4yDQgCEAAYhgMYgAQYigUyDQgDEAAYhgMYgAQYigUyDQgEEAAYhgMYgAQYigUyBwgFEAAY7wXSAQg2ODU3ajBqN6gCALACAA&sourceid=chrome&ie=UTF-8
        // https://stackoverflow.com/questions/9867410/barcode-scanning-in-android-emulator
        //
        // Make sure the device in the emulator is configured to use your web cam to test the bar code scanning
        //
        String action = getIntent().getAction();
        String eventId = null;
        if(Intent.ACTION_VIEW.equals(action)){
            Uri uri = getIntent().getData();
            if(uri != null){
                eventId = uri.getQueryParameter("eventId");
            }
        }
        Log.d(TAG, "Launched with eventId=" + eventId);

    }

}