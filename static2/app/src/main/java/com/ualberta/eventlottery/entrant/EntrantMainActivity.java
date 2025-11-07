package com.ualberta.eventlottery.entrant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityEventLotteryMainBinding;

/**
 * Responsible for navigating to the event details page if app is opened via QR code
 */
public class EntrantMainActivity extends AppCompatActivity {
    private static final String TAG = "EventLottery";

    private ActivityEventLotteryMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEventLotteryMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_event_lottery_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Sources used for QR code scanning
        // https://stackoverflow.com/questions/36079523/launch-app-or-play-store-by-scanning-qr-code
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
                // Exact google search term: android navigation with parameters in java
                Bundle bundle = new Bundle();
                bundle.putString("eventId", eventId);

                navController.navigate(R.id.navigation_event_details, bundle);
            }
        }
        Log.d(TAG, "Launched with eventId=" + eventId);
    }
}