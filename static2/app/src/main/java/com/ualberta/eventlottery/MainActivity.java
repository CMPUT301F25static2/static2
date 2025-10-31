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
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityEventLotteryMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityEventLotteryMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEventLotteryMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_event_lottery_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Class activity = EntrantMainActivity.class;

        String userRole = "entrant"; // Change to admin or organizer to go to the admin or organizer screens
        if (userRole.compareTo("admin") == 0){
            activity = AdminMainActivity.class;

        } else{
            activity = EntrantMainActivity.class;
        }
        Intent myIntent = new Intent(MainActivity.this, activity);
        MainActivity.this.startActivity(myIntent);
    }

}