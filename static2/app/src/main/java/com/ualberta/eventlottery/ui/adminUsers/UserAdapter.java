package com.ualberta.eventlottery.ui.adminUsers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ualberta.eventlottery.model.User;
import com.ualberta.static2.R;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    private ArrayList<User> users;
    private Context context;
    private LayoutInflater inflater;
    private String searchText;


    public UserAdapter(Context context, ArrayList<User> users) {
        this.users = users;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.admin_user_item, parent, false);

            holder = new ViewHolder();
            holder.userName = convertView.findViewById(R.id.text_admin_user_name);
            holder.userID = convertView.findViewById(R.id.text_admin_user_id);

            convertView.setTag(holder); // cache holder in the view
        } else {
            holder = (ViewHolder) convertView.getTag(); // reuse cached holder
        }

        User user = users.get(position);
        holder.userName.setText(user.getName());
        holder.userID.setText("ID: "+user.getUserId());

        return convertView;
    }


    static class ViewHolder {
        TextView userName;
        TextView userID;
    }
}
