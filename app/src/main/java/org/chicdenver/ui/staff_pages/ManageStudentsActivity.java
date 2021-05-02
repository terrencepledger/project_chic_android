package org.chicdenver.ui.staff_pages;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.chicdenver.R;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.data.model.ManageStudentsAdapter;
import org.chicdenver.ui.DrawerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class ManageStudentsActivity extends DrawerActivity {

    ManageStudentsAdapter adapter;
    RecyclerView studentsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        activity = this;

        init();

    }

    @Override
    public void updateUi() {

        studentsRecycler = findViewById(R.id.manageStudentsRecycler);
        collectUsers();

    }

    public void collectUsers() {

        ArrayList<HashMap> users = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot: task.getResult().getDocuments()
                             ) {
                            Log.d("Snapshot", snapshot.getData().toString());
                            HashMap studentMap = (HashMap) snapshot.getData();
                            if(Classification.valueOf(studentMap.get("Classification").toString())
                                    .equals(Classification.Staff))
                                continue;
                            studentMap.put("ID", snapshot.getId());
                            users.add(studentMap);
                            adapter = new ManageStudentsAdapter(users, activity);
                            studentsRecycler.setAdapter(adapter);
                        }
                    }
                }
        );

    }

}