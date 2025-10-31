package com.ualberta.eventlottery.ui.adminHome;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.event.Event;
import com.ualberta.eventlottery.ui.adminImages.AdminImagesFragment;
import com.ualberta.eventlottery.ui.adminLogs.AdminLogFragment;
import com.ualberta.eventlottery.ui.adminUsers.AdminUsersFragment;
import com.ualberta.eventlottery.ui.home.entrant.EventAdapter;
import com.ualberta.eventlottery.ui.home.entrant.HomeFragment;
import com.ualberta.eventlottery.ui.home.entrant.HomeViewModel;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentAdminHomeBinding;
import com.ualberta.static2.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class AdminHomeFragment extends Fragment {

    private ListView browseOptions;
    private ArrayAdapter<String> optionAdapter;
    private ArrayList<String> options;

    private FragmentAdminHomeBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Reference the ListView from the layout
        browseOptions = view.findViewById(R.id.admin_browse);

        // Sample data
        options = new ArrayList<>(Arrays.asList("Browse Events", "Browse Users", "Browse Images", "Browse Logs"));
        optionAdapter = new ArrayAdapter<>(getContext(), R.layout.admin_menu_item, options)
        ;

        // Set the adapter on the ListView
        browseOptions.setAdapter(optionAdapter);

        browseOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment selectedFragment = null;

                Toast.makeText(getContext(), "Clicked: " + browseOptions.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                if (browseOptions.getItemAtPosition(i).equals("Browse Events")){
                    selectedFragment = new HomeFragment();
                }
                else if (browseOptions.getItemAtPosition(i).equals("Browse Logs")){
                    selectedFragment = new AdminLogFragment();
                }
                else if (browseOptions.getItemAtPosition(i).equals("Browse Users")){
                    selectedFragment = new AdminUsersFragment();
                }
                else if (browseOptions.getItemAtPosition(i).equals("Browse Images")){
                    selectedFragment = new AdminImagesFragment();
                }

                if (selectedFragment != null) {
                    getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit();
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
