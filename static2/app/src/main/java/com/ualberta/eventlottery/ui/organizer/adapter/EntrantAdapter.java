package com.ualberta.eventlottery.ui.organizer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.model.Entrant;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.static2.R;

import java.util.List;

public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.ViewHolder> {
    private Context context;
    private List<Entrant> entrants;
    private String eventId;
    private RegistrationRepository registrationRepository = RegistrationRepository.getInstance();

    public EntrantAdapter(Context context, List<Entrant> entrants, String eventId) {
        this.context = context;
        this.entrants = entrants;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.organizer_item_entrant, parent, false);
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
                holder.itemView.setOnClickListener(v -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(entrant);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

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

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Entrant entrant);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}