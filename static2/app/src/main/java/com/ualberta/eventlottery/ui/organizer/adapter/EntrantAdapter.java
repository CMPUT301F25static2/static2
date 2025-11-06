package com.ualberta.eventlottery.ui.organizer.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.model.Entrant;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.static2.R;

import java.util.List;

public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.ViewHolder> {
    private Context context;
    private List<Entrant> entrants;
    private String eventId;
    private RegistrationRepository registrationRepository = RegistrationRepository.getInstance();

    // Listener for status changes
    public interface OnEntrantStatusChangeListener {
        void onEntrantStatusChanged();
    }

    private OnEntrantStatusChangeListener statusChangeListener;

    public EntrantAdapter(Context context, List<Entrant> entrants, String eventId) {
        this.context = context;
        this.entrants = entrants;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_item_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entrant entrant = entrants.get(position);
        holder.tvName.setText(entrant.getName());

        registrationRepository.findRegistrationByEventAndUser(eventId, entrant.getUserId(), new RegistrationRepository.RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                if (registration != null) {
                    holder.tvStatus.setText(registration.getStatus().toString());
                } else {
                    holder.tvStatus.setText("Unknown");
                }

                // Set click listener to show options menu
                holder.itemView.setOnClickListener(v -> {
                    showOptionsMenu(v, entrant, registration);
                });
            }

            @Override
            public void onFailure(Exception e) {
                holder.tvStatus.setText("Error");
                // Allow click even if status fetch fails
                holder.itemView.setOnClickListener(v -> {
                    showOptionsMenu(v, entrant, null);
                });
            }
        });
    }

    private void showOptionsMenu(View view, Entrant entrant, Registration registration) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.entrant_options_menu, popupMenu.getMenu());

        MenuItem deleteItem = popupMenu.getMenu().findItem(R.id.menu_delete_from_waiting_list);
        if (deleteItem != null) {
            SpannableString deleteText = new SpannableString(deleteItem.getTitle());
            int redColor = ContextCompat.getColor(context, R.color.red_delete);
            deleteText.setSpan(new ForegroundColorSpan(redColor), 0, deleteText.length(), 0);
            deleteItem.setTitle(deleteText);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_delete_from_waiting_list) {
                deleteFromWaitingList(entrant, registration);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void deleteFromWaitingList(Entrant entrant, Registration registration) {
        if (registration == null) {
            Toast.makeText(context, "Cannot find registration for this entrant", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update status to CANCELLED
        registration.setStatus(EntrantRegistrationStatus.CANCELLED);

        registrationRepository.updateRegistration(registration, new RegistrationRepository.BooleanCallback() {
            @Override
            public void onSuccess(boolean result) {
                if (result) {
                    Toast.makeText(context, "Removed from waiting list: " + entrant.getName(), Toast.LENGTH_SHORT).show();

                    // Notify fragment to refresh data
                    if (statusChangeListener != null) {
                        statusChangeListener.onEntrantStatusChanged();
                    }

                    // Remove from current list
                    int position = entrants.indexOf(entrant);
                    if (position != -1) {
                        entrants.remove(position);
                        notifyItemRemoved(position);
                    }
                } else {
                    Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Set status change listener
    public void setOnEntrantStatusChangeListener(OnEntrantStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }

    // Update data method
    public void updateData(List<Entrant> newEntrants) {
        this.entrants = newEntrants;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return entrants != null ? entrants.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_entrant_name);
            tvStatus = itemView.findViewById(R.id.tv_entrant_status);
        }
    }
}