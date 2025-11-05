package com.ualberta.eventlottery.admin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.ualberta.eventlottery.ui.adminHome.AdminHomeFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityEventLotteryMainBinding;


public class AdminMainActivity extends AppCompatActivity{

    private ActivityEventLotteryMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //binding = ActivityEventLotteryMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_event_lottery_main);
        //NavigationUI.setupWithNavController(binding.navView, navController);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminHomeFragment())
                    .commit();
        }
    }

}
