package org.chicdenver.data.model.Messages;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.chicdenver.R;

import java.util.ArrayList;

class ChannelHeaderHolder extends RecyclerView.ViewHolder {

    TextView header;
    TextView lastMessage;
    TextView date;

    public ChannelHeaderHolder(View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.channelMemberName);
        lastMessage = itemView.findViewById(R.id.channelLastMessage);
        date = itemView.findViewById(R.id.channelLastMesasgeDate);
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent()
//                ((Activity) view.getContext()).startActivity();
//            }
//        });
    }

    public void setChannelHeader(String headerText, String lastMessageText, String dateText,
                                 String username, String dbKey) {
        header.setText(headerText);
        lastMessage.setText(lastMessageText);
        date.setText(dateText);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Channels").child(dbKey)
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ConnectionChannel userChannel =
                                                snapshot.getValue(ConnectionChannel.class);
                                        Fragment connectionFragment =
                                                ConnectionChannelFragment.newInstance(
                                                        (ArrayList<ConnectionChannel.ChannelMessage>) userChannel.getMessages(),
                                                        headerText, userChannel.getChannelId(), username, dbKey
                                                );

                                        FragmentManager manager = ((FragmentActivity) view.getContext())
                                                .getSupportFragmentManager();
                                        FragmentTransaction transaction = manager.beginTransaction();
                                        transaction.replace(R.id.root,
                                                connectionFragment,"Connection");
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                }
                        );
                return true;
            }
        });
    }

}
