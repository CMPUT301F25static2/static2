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
import com.ualberta.static2.databinding.ActivityAdminMainBinding;
import com.ualberta.static2.databinding.ActivityEventLotteryMainBinding;

/**
 * @author Lumbani
 * @version 1.0
 * This is the main activity class for the admin.
 */

public class AdminMainActivity extends AppCompatActivity{

    private ActivityAdminMainBinding binding;

    /**
     * This method is called when the activity is created.
     * Will replace the initializer fragment with the admin home fragment.
     * @param savedInstanceState The saved instance state.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

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
