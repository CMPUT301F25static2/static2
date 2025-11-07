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

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.ui.adminEvents.AdminEventFragment;
import com.ualberta.eventlottery.ui.adminImages.AdminImagesFragment;
import com.ualberta.eventlottery.ui.adminLogs.AdminLogFragment;
import com.ualberta.eventlottery.ui.adminUsers.AdminUsersFragment;
import com.ualberta.eventlottery.ui.home.entrant.EventAdapter;
import com.ualberta.eventlottery.ui.home.entrant.HomeFragment;
import com.ualberta.eventlottery.ui.home.entrant.HomeViewModel;
import com.ualberta.eventlottery.ui.profile.ProfileFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentAdminHomeBinding;
import com.ualberta.static2.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lumbani
 * @version 1.0
 * This is a class that serves as the home screen for the admin.
 */
public class AdminHomeFragment extends Fragment {

    private ListView browseOptions;
    private ArrayAdapter<String> optionAdapter;
    private ArrayList<String> options;

    private FragmentAdminHomeBinding binding;

    /**
     * Called to have the fragment initiate the admin portal / home fragment
     * Sets up the browse menu for the admin
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        browseOptions = view.findViewById(R.id.admin_browse);

        options = new ArrayList<>(Arrays.asList("Browse Events", "Browse Users", "Browse Images", "Browse Logs"));
        optionAdapter = new ArrayAdapter<>(getContext(), R.layout.admin_menu_item, options);

        browseOptions.setAdapter(optionAdapter);

        browseOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * This method is invoked when an item in this browse menu is clicked.
             * Sends the user to the selected fragment.
             * Possible locations: Events, Users, Images, Logs
             *
             * @param adapterView The AdapterView where the click happened.
             * @param view The view within the AdapterView that was clicked
             * @param i The position of the view in the adapter.
             * @param l The row id of the item that was clicked.
             *
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment selectedFragment = null;

                Toast.makeText(getContext(), "Clicked: " + browseOptions.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                if (browseOptions.getItemAtPosition(i).equals("Browse Events")){
                    selectedFragment = new AdminEventFragment();
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
                    Bundle bundle = new Bundle();
                    bundle.putString("isAdmin", "true");
                    selectedFragment.setArguments(bundle);
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

    /**
     * Called when the fragment is no longer in use.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
