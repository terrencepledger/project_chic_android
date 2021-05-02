package org.chicdenver.data.model;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.chicdenver.R;
import org.chicdenver.data.model.ChicAssignments.ChicCourse;
import org.chicdenver.data.model.Messages.ConnectActivityFragmentAdapter;
import org.chicdenver.data.model.Messages.ConnectionChannel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ManageStudentsHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView school;
    TextView number;
    Button approveBtn;
    Button removeBtn;

    public ManageStudentsHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.manageStudentNameText);
        school = itemView.findViewById(R.id.manageStudentSchoolText);
        number = itemView.findViewById(R.id.manageStudentNumberText);


        approveBtn = itemView.findViewById(R.id.approveStudentBtn);
        removeBtn = itemView.findViewById(R.id.removeStudentBtn);

    }

    public void bind(HashMap student, ManageStudentsAdapter manageStudentsAdapter) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String courseID = ((DocumentReference) student.get("Group")).getPath()
                .split("Groups/")[1];

        db.collection("Groups").document(courseID).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        name.setText(student.get("Name").toString());
                        school.setText(task.getResult().get("Name").toString());
                        number.setText(student.get("Number").toString());
                        if (Classification.valueOf(student.get("Classification").toString())
                                .equals(Classification.Unverified))
                                    approveBtn.setVisibility(View.VISIBLE);
                        else
                            removeBtn.setVisibility(View.VISIBLE);
                    }
                }
        );
        approveBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        db.collection("Users").document(student.get("ID").toString())
                                .update("Classification", Classification.Student).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ((DocumentReference) student.get("Group"))
                                                .get().addOnCompleteListener(
                                                new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                        ArrayList<Map> groupStudents = (ArrayList<Map>) task.getResult()
                                                                .get("Students");

                                                        if(groupStudents != null)
                                                        {
                                                            groupStudents.add(student);
                                                        }
                                                        else
                                                            groupStudents = new ArrayList<Map>();

                                                        ((DocumentReference) student.get("Group"))
                                                                .update("Students", groupStudents);

                                                    }
                                                }
                                        );
                                    }
                                }
                        );

                        approveBtn.setVisibility(View.INVISIBLE);
                        removeBtn.setVisibility(View.VISIBLE);

                        ChicCourse course = new ChicCourse(manageStudentsAdapter.getActivity(),
                                new LoggedInUser(FirebaseAuth.getInstance().getCurrentUser(),
                                        manageStudentsAdapter.getActivity(), false),
                                courseID
                        );

                        new Thread(new Runnable() {
                            public void run() {
                                course.sendInvite(student.get("Email").toString());
                            }
                        }).start();

                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Channels")
                                .getRef();

                        dbRef.orderByKey().addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                                            @Override
                                            public void accept(DataSnapshot dataSnapshot) {
                                                ConnectionChannel current =
                                                        dataSnapshot.getValue(ConnectionChannel.class);
                                                if (current.getChannelId().equals(courseID)) {
                                                    current.addMember(student.get("Name").toString(),
                                                            student.get("ID").toString());
                                                    snapshot.child(dataSnapshot.getKey()).getRef()
                                                            .setValue(current);
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }
        );
        removeBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        db.collection("Users").document(student.get("ID").toString())
                                .update("Classification", Classification.Unverified);

                        ((DocumentReference) student.get("Group"))
                                .get().addOnCompleteListener(
                                new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        ArrayList<Map> groupStudents = (ArrayList<Map>) task.getResult()
                                                .get("Students");

                                        final Map[] mStudent = new Map[1];

                                        groupStudents.removeIf(tempStudent -> {
                                            if(tempStudent.get("ID").equals(student.get("ID")))
                                            {
                                                mStudent[0] = tempStudent;
                                                return true;
                                            }
                                            return false;
                                        });

                                        ((DocumentReference) student.get("Group"))
                                                .update("Students", groupStudents);

                                    }
                                }
                        );

                        removeBtn.setVisibility(View.INVISIBLE);
                        approveBtn.setVisibility(View.VISIBLE);

                    }
                }
        );
    }


}
