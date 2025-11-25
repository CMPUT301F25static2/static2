package com.ualberta.eventlottery.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ualberta.eventlottery.ui.adminHome.AdminHomeFragment;
import com.ualberta.eventlottery.ui.notifications.NotificationsFragment;
import com.ualberta.eventlottery.ui.profile.ProfileFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Show home fragment initially
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminHomeFragment())
                    .commit();
        }

        // ✅ Handle bottom nav clicks with manual fragment transactions
        binding.navViewAdmin.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;

            if (itemId == R.id.navigation_admin_home) {
                selectedFragment = new AdminHomeFragment();
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.navigation_notifications) {
                selectedFragment = new NotificationsFragment();
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container))
                        .add(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            return false;
        });
    };
}