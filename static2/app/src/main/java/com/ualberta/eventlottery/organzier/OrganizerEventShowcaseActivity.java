package com.ualberta.eventlottery.organzier;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ualberta.static2.databinding.ActivityOrganizerEventInfoBinding;
import com.ualberta.static2.databinding.ActivityOrganizerEventShowcaseBinding;

public class OrganizerEventShowcaseActivity extends AppCompatActivity {
    private ActivityOrganizerEventShowcaseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerEventShowcaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }


}
