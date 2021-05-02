package org.chicdenver.ui.connect;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListHolder> {

    ArrayList<HashMap> userList;
    Activity activity;

    public UserListAdapter(ArrayList<HashMap> users, Activity activity)
    {

        this.userList = users;
        this.activity = activity;

    }

    @NonNull
    @Override
    public UserListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserListHolder(parent, activity);
    }

    @Override
    public void onBindViewHolder(UserListHolder holder, int position) {
        holder.setUsernameText((String) userList.get(position).get("Name"));
        Long id = (Long) userList.get(position).get("ID");
        Log.d("ULA Bind User ID", String.valueOf(id));
        holder.setID(id);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public CharSequence[] getItems() {

        String[] ret = new String[getItemCount()];

        for(int index = 0; index < getItemCount(); index++)
        {

            ret[index] = (String) userList.get(index).get("Name");

        }

        return ret;

    }

    public static class UserListHolder extends RecyclerView.ViewHolder {

        TextView username;

        public UserListHolder(View itemView, Activity activity) {

            super(itemView);
            username = new TextView(activity.getApplicationContext());

        }

        public void setUsernameText(String name)
        {

            username.setText(name);

        }

        public void setID(Long id)
        {

            username.setOnClickListener(

                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Log.d("ID: ", String.valueOf(id));

                        }
                    }

            );

        }

    }

}
