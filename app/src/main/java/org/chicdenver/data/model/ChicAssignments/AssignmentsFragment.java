package org.chicdenver.data.model.ChicAssignments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;
import org.chicdenver.data.model.Grade;
import org.chicdenver.data.model.Messages.ConnectionChannelFragmentHolder;
import org.chicdenver.data.model.Messages.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class AssignmentsFragment extends Fragment {

    private List<ChicCourse.ChicAssignment> assignments;

    public static AssignmentsFragment newInstance(List<ChicCourse.ChicAssignment> assignments) {

        AssignmentsFragment myFragment = new AssignmentsFragment();

        Bundle args = new Bundle();
        ArrayList<String> titleTexts = new ArrayList<>();
        ArrayList<String> descTexts = new ArrayList<>();
        ArrayList<String> gradeTexts = new ArrayList<>();
        ArrayList<String> remTimeTexts = new ArrayList<>();
        ArrayList<String> dueDateTexts = new ArrayList<>();

        for (ChicCourse.ChicAssignment assignment: assignments
        ) {
            titleTexts.add(assignment.getTitle());
            descTexts.add(assignment.getDescription());
            String dateText = assignment.getPrettyDate();
            if(!assignment.isGraded()) {
                gradeTexts.add("INCOMPLETE");
                remTimeTexts.add(assignment.getRemainingTime());
            }
            else {
                gradeTexts.add(new Grade((float) assignment.getGrade()).getLetter());
                remTimeTexts.add(null);
            }
            dueDateTexts.add(dateText);
        }

        args.putStringArrayList("TitleTexts", titleTexts);
        args.putStringArrayList("DescTexts", descTexts);
        args.putStringArrayList("GradeTexts", gradeTexts);
        args.putStringArrayList("RemTimeTexts", remTimeTexts);
        args.putStringArrayList("DueDateTexts", dueDateTexts);
        myFragment.setArguments(args);
        myFragment.setList(assignments);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_assignments, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AssignmentsAdapter adapter = new AssignmentsAdapter(getArguments(), assignments);
        RecyclerView assignmentRecycler = view.findViewById(R.id.listAssignmentsRecycler);
        assignmentRecycler.setAdapter(adapter);
    }

    public void setList(List<ChicCourse.ChicAssignment> assignments) {
        this.assignments = assignments;

    }

}
