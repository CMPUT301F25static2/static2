package com.ualberta.eventlottery.organzier;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityMainBinding;
import com.ualberta.static2.databinding.ActivityOrganizerEventCreateBinding;

public class OrganizerEventCreateActivity extends AppCompatActivity {

    private ActivityOrganizerEventCreateBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerEventCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}