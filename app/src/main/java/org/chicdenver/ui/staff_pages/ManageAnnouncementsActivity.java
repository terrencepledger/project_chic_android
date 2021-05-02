package org.chicdenver.ui.staff_pages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.chicdenver.R;
import org.chicdenver.data.model.Announcements.Announcement;
import org.chicdenver.data.model.Announcements.ManageAnnouncementsAdapter;
import org.chicdenver.ui.DrawerActivity;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ManageAnnouncementsActivity extends DrawerActivity {

    ManageAnnouncementsAdapter manageAnnouncementsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_announcements);

        activity = this;

        manageAnnouncementsAdapter = new ManageAnnouncementsAdapter();

        init();

    }

    public void connectButtons() {

        Button addBtn = findViewById(R.id.addAncBtn);

        addBtn.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        manageAnnouncementsAdapter.add();

                    }
                }

        );

    }

    public void populateAnnouncements() {

        RecyclerView listAllAncs = findViewById(R.id.manageAncsRecycler);

        ArrayList<Announcement> allAnnouncements = new ArrayList<>();

        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference().child("Announcements").getRef();

        dbRef.orderByKey().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("Hello", "hello");
                        snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                            @Override
                            public void accept(DataSnapshot dataSnapshot) {
                                Announcement current =
                                        dataSnapshot.getValue(Announcement.class);
                                allAnnouncements.add(current);
                            }
                        });

                        manageAnnouncementsAdapter.setAnnouncements(allAnnouncements);
                        listAllAncs.setAdapter(manageAnnouncementsAdapter);
                        listAllAncs.setLayoutManager(new LinearLayoutManager(activity,
                                LinearLayoutManager.VERTICAL,false));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    @Override
    public void updateUi() {

        connectButtons();
        populateAnnouncements();

    }

}
