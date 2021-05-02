package org.chicdenver.ui.main_pages;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
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
import org.chicdenver.data.model.Announcements.AnnouncementAdapter;
import org.chicdenver.data.model.ChicAssignments.ManageAssignmentAdapter;
import org.chicdenver.data.model.ChicAssignments.UpcomingAssignmentHolder;
import org.chicdenver.data.model.ChicAssignments.ChicCourse;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.data.model.Messages.ChannelHeaderAdapter;
import org.chicdenver.data.model.Messages.ConnectionChannel;
import org.chicdenver.data.model.Announcements.Announcement;
import org.chicdenver.ui.DrawerActivity;
import org.chicdenver.ui.connect.ConnectActivity;
import org.chicdenver.ui.menu.ClassificationMenu;
import org.chicdenver.ui.staff_pages.ManageClassesActivity;
import org.chicdenver.ui.student_pages.ClassPortalActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.function.Consumer;

public class HomeActivity extends DrawerActivity {

    Classification mClassification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        activity = this;

        init();

    }

    @Override
    protected void setUserListener() {

        loggedInUser.setListener(new LoggedInUser.UserDatabaseListener() {
            @Override
            public void onImageLoaded(Drawable profileImage) {
                menu.updateProfile(profileImage);
            }

            @Override
            public void onUserInfoLoaded(String name, Classification classification, String number) {
                mClassification = classification;
                menu.updateMenuItems(classification);
                populateRecentAnnouncements();
                updateClassificationText(classification);
                if(classification.equals(Classification.Staff))
                    loggedInUser.startStaffLoad();
                else if(!classification.equals(Classification.Unverified))
                    loggedInUser.startStudentLoad();
            }

            @Override
            public void onClassroomLoaded() {
                populateUpcomingAssignment();
                populateRecentMessages();
            }

        });

    }

    private void updateClassificationText(Classification classification) {

        ((TextView) findViewById(R.id.homeClassificationHeaderText)).setText(

                String.format("%s\nView", classification.toString())

        );

    }

    private void populateUpcomingAssignment() {

        ChicCourse.ChicAssignment assignment;
        if(mClassification.equals(Classification.Staff))
            assignment = loggedInUser.mStaff.getNextAssignment();
        else
            assignment = loggedInUser.getStudentClassroom().getNextAssignment();

        if(assignment != null) {

            UpcomingAssignmentHolder holder = new UpcomingAssignmentHolder(
                    findViewById(R.id.homeUpcomingView)
            );

            holder.setAssignment(assignment);
        }

    }

    @Override
    public void updateUi() {

        updateWelcome();

    }

    private void populateRecentMessages() {

        RecyclerView listRecentMsgs = findViewById(R.id.homeRecentMsgsView)
                .findViewById(R.id.listRecentMsgsRecycler);

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> lastMessages = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();

        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference().child("Channels").getRef();

        Bundle args = new Bundle();

        dbRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                            @Override
                            public void accept(DataSnapshot dataSnapshot) {
                                ConnectionChannel current =
                                        dataSnapshot.getValue(ConnectionChannel.class);
                                String uid;
                                if(mClassification.equals(Classification.Staff))
                                    uid = getString(R.string.connection_staff_id);
                                else
                                    uid = loggedInUser.getUid();
                                if (current.getMemberIDs().contains(uid)) {
                                    if(current.getMessages().size() == 0)
                                        return;
                                    if(names.size() < 5)
                                    {
                                        names.add(current.getChannelHeader(
                                                loggedInUser.getDisplayName(0)));
                                        lastMessages.add(current.getLastMessage(loggedInUser.getDisplayName(1)).getMessage());
                                        dates.add(current.getLastMessage(loggedInUser.getDisplayName(1)).getPrettyPostTime());
                                        keys.add(dataSnapshot.getKey());
                                    }
                                }
                            }
                        });

                        args.putStringArrayList("Names", names);
                        args.putStringArrayList("LastMessages", lastMessages);
                        args.putStringArrayList("Dates", dates);
                        args.putStringArrayList("Keys", keys);
                        args.putString("UserName", loggedInUser.getDisplayName(0));

                        ChannelHeaderAdapter adapter = new ChannelHeaderAdapter(args, keys);

                        listRecentMsgs.setAdapter(adapter);
                        listRecentMsgs.setLayoutManager(new LinearLayoutManager(activity,
                                LinearLayoutManager.HORIZONTAL,false));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                }
        );



    }

    private void populateRecentAnnouncements() {

        RecyclerView listRecentAncs = findViewById(R.id.homeRecentAncsView)
                .findViewById(R.id.listRecentMsgsRecycler);

        ArrayList<Announcement> announcements = new ArrayList<>();

        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference().child("Announcements").getRef();

        dbRef.orderByKey().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                            @Override
                            public void accept(DataSnapshot dataSnapshot) {
                                Announcement current =
                                        dataSnapshot.getValue(Announcement.class);
                                if (mClassification == Classification.Staff ||
                                        current.getAudience().contains(mClassification)) {

                                    if(announcements.size() < 3)
                                        announcements.add(current);

                                }
                            }
                        });

//                        Collections.reverse(announcements);
                        AnnouncementAdapter adapter = new AnnouncementAdapter(announcements);

                        listRecentAncs.setAdapter(adapter);
                        listRecentAncs.setLayoutManager(new LinearLayoutManager(activity,
                                LinearLayoutManager.VERTICAL,false));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                }
        );



    }

    public void seeAllOnClick(View v) {

        if(v.equals(findViewById(R.id.upcomingAllLinkText))) {
            if(mClassification.equals(Classification.Staff))
                ClassificationMenu.navigate(activity, ManageClassesActivity.class);
            else
                ClassificationMenu.navigate(activity, ClassPortalActivity.class);
        }
        else if(v.equals(findViewById(R.id.listRecentAncsLinkText)))
            ClassificationMenu.navigate(activity, AnnouncementsActivity.class);
        else if(v.equals(findViewById(R.id.listRecentMsgsLinkText)))
            ClassificationMenu.navigate(activity, ConnectActivity.class);

    }

    private void updateWelcome() {

        ((TextView) findViewById(R.id.homeWelcomeText)).setText(
                String.format("Welcome Back,\n%s", loggedInUser.getDisplayName(0))
        );

    }

}