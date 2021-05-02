package org.chicdenver.data.model.ChicAssignments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.classroom.model.Course;

import org.chicdenver.R;
import org.chicdenver.data.model.LoggedInUser;

import java.util.List;

public class ClassAssignmentsListFragment extends Fragment {

    private List<ChicCourse> courses;
    private LoggedInUser user;

    public static ClassAssignmentsListFragment newInstance(List<ChicCourse> courses, LoggedInUser user) {

        ClassAssignmentsListFragment myFragment = new ClassAssignmentsListFragment();

        myFragment.setCourses(courses, user);

        return myFragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View inflatedView = inflater.inflate(R.layout.fragment_class_assignments_list, container,
                false);

        inflatedView.findViewById(R.id.floatingAddCourseBtn).setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inflatedView.findViewById(R.id.newCourseCard).setVisibility(View.VISIBLE);
                    }
                }

        );

        inflatedView.findViewById(R.id.addCourseSubmitBtn).setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        user.mStaff.addCourse(
                                                ((EditText) inflatedView.findViewById(
                                                        R.id.addCourseNameInputText))
                                                        .getText().toString()
                                        );
                                    }
                                }
                        ).start();
                    }
                }

        );
        return inflatedView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ManageClassAssignmentsListAdapter adapter = new ManageClassAssignmentsListAdapter(courses,
                user);
        ((RecyclerView) view.findViewById(R.id.classAssignmentListRecycler))
                .setAdapter(adapter);
    }

    private void setCourses(List<ChicCourse> courses, LoggedInUser user) {

        this.courses = courses;
        this.user = user;

    }

}
