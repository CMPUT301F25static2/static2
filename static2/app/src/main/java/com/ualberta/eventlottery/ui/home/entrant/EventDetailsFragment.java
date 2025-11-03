package com.ualberta.eventlottery.ui.home.entrant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentEventDetailsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private FragmentEventDetailsBinding binding;


    private String mEventId;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventId Parameter 1.
     * @return A new instance of fragment EventDetailsFragment.
     */
    public static EventDetailsFragment newInstance(String eventId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (mEventId != null) {
            TextView title = view.findViewById(R.id.event_details_title);
            if (title != null) {
                title.setText("Event Details Fragment: eventId = " + mEventId);
            }
        }

        return view;
    }
}
