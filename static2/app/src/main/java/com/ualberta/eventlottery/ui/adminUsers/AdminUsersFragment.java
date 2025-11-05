package com.ualberta.eventlottery.ui.adminUsers;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.ui.organizer.adapter.OrganizerEventAdapter;
import com.ualberta.eventlottery.ui.organizer.organizerEventInfo.OrganizerEventInfoFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentAdminUsersBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersFragment extends Fragment {

    private ArrayList<User> userArrayList;

    private ArrayList<User> filtered;
    private UserAdapter userArrayAdapter;

    private UserAdapter filteredUserAdapter;

    private FirebaseFirestore db;

    private CollectionReference usersRef;
    private com.ualberta.static2.databinding.FragmentAdminUsersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //AdminUserViewModel adminUserViewModel =
         //       new ViewModelProvider(this).get(AdminUserViewModel.class);

        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textUsers;
        //adminUserViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.adminBackButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        userArrayList = new ArrayList<>();
        userArrayAdapter = new UserAdapter(requireContext(), userArrayList);
        binding.userListView.setAdapter(userArrayAdapter);


        usersRef.addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", error.toString());
                    }
                    if (value != null && !value.isEmpty()) {
                        userArrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            String userId = snapshot.getId();
                            String name = snapshot.getString("name");
                            String email = snapshot.getString("email");
                            String phone = snapshot.getString("phone");
                            String favRecCenter = snapshot.getString("favRecCenter");

                            userArrayList.add(new User("ID: " + userId, name, email, phone, favRecCenter));
                        }

                        // dummy data
                        userArrayList.add(new User("ID: " + "1", "giannis antetokounmpo", "c", "d", "e"));
                        userArrayList.add(new User("ID: " + "12", "michael johnson", "c", "d", "e"));
                        userArrayList.add(new User("ID: " + "123", "max verstappen", "c", "d", "e"));
                        userArrayList.add(new User("ID: " + "1234", "novak djokovic", "c", "d", "e"));
                        userArrayList.add(new User("12345", "jerome iginla", "c", "d", "e"));
                        userArrayList.add(new User("123456", "lebron james", "c", "d", "e"));
                        userArrayList.add(new User("1234567", "Shohei ohtani", "c", "d", "e"));
                        userArrayList.add(new User("12345678", "Mesut ozil", "c", "d", "e"));
                        userArrayList.add(new User("1a234567a", "Mesut ozil", "c", "d", "e"));
                        userArrayList.add(new User("ASDFSADF1a2b34567", "CAPS", "c", "d", "e"));
                        userArrayList.add(new User("1aCZXC2bERAGAERv3c45678", "ALL CAPS", "c", "d", "e"));
                        userArrayList.add(new User("NVCNG1a2ascsb3c4d5678", "C", "c", "d", "e"));
                        userArrayList.add(new User("LKNK1a2b3bdfbazd45678", "CAPS2", "c", "d", "e"));
                        userArrayList.add(new User("1a2345ARSDIVND678", "michael jordan", "c", "d", "e"));
                        userArrayList.add(new User("1a2345DVSDV678", "Shaun White", "c", "d", "e"));
                        userArrayList.add(new User("1a23456AWREG9P4G78", "1234234", "c", "d", "e"));
                        // dummy data

                        userArrayAdapter.notifyDataSetChanged();
                    }
                });

        binding.searchUsers.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().toLowerCase();
                filtered = new ArrayList<>();
                filteredUserAdapter = new UserAdapter(requireContext(), filtered);

                for (User user : userArrayList) {
                    if (user.getName().toLowerCase().contains(searchText.toLowerCase())){
                        filtered.add(user);
                    }
                }
                binding.userListView.setAdapter(filteredUserAdapter);
                userArrayAdapter.notifyDataSetChanged();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}