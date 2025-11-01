package com.ualberta.eventlottery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityEventLotteryMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityEventLotteryMainBinding binding;

    private boolean isAdmin;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityEventLotteryMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_event_lottery_main);
//        NavigationUI.setupWithNavController(binding.navView, navController);
//
//        isAdmin = false; // Change to false to go to entrant screens
//        if (isAdmin){
//            Intent myIntent = new Intent(MainActivity.this, AdminMainActivity.class);
//            MainActivity.this.startActivity(myIntent);
//        }
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupClickListeners();
    }

    private void setupClickListeners() {

        findViewById(R.id.btn_entrant).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EntrantMainActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.btn_admin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminMainActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_organizer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrganizerMainActivity.class);
                startActivity(intent);
            }
        });


    }

}