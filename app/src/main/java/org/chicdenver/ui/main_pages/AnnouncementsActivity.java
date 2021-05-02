package org.chicdenver.ui.main_pages;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import org.chicdenver.data.model.Announcements.AnnouncementAdapter;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.ui.DrawerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AnnouncementsActivity extends DrawerActivity {

    List<Announcement> allAnnouncements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);
        
        activity = this;
        
        init();

    }

    protected void setUserListener() {

        loggedInUser.setListener(new LoggedInUser.UserDatabaseListener() {
            @Override
            public void onImageLoaded(Drawable profileImage) {
                menu.updateProfile(profileImage);
            }

            @Override
            public void onUserInfoLoaded(String name, Classification classification, String number) {
                menu.updateMenuItems(classification);
                populateAnnouncements(classification);
            }

            @Override
            public void onClassroomLoaded() {
            }

        });

    }

    @Override
    public void updateUi() {

    }

    private void populateAnnouncements(Classification classification) {

        ArrayList<Announcement> announcements = new ArrayList<>();

        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference().child("Announcements").getRef();

        RecyclerView announcementsRecycler = findViewById(R.id.allAncsRecycler);

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
                                if(classification.equals(Classification.Staff) ||
                                        current.getAudience().contains(classification))
                                    announcements.add(current);
                            }
                        });

                        announcementsRecycler.setAdapter(new AnnouncementAdapter(announcements));
                        announcementsRecycler.setLayoutManager(new LinearLayoutManager(activity,
                                LinearLayoutManager.VERTICAL,false));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

}