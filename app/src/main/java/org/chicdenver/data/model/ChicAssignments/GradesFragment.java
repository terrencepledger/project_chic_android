package org.chicdenver.data.model.ChicAssignments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;
import org.chicdenver.data.model.Grade;

import java.util.ArrayList;
import java.util.List;

public class GradesFragment extends Fragment {

    public static GradesFragment newInstance(List<ChicCourse.ChicAssignment> assignments,
                                             String overallGrade) {

        GradesFragment myFragment = new GradesFragment();

        Bundle args = new Bundle();
        ArrayList<String> titleTexts = new ArrayList<>();
        ArrayList<String> gradeTexts = new ArrayList<>();



        for (ChicCourse.ChicAssignment assignment: assignments
        ) {
            if(assignment.isGraded()) {
                gradeTexts.add(new Grade((float) assignment.getGrade()).getLetter());
                titleTexts.add(assignment.getTitle());
            }
        }

        args.putStringArrayList("TitleTexts", titleTexts);
        args.putStringArrayList("GradeTexts", gradeTexts);
        args.putString("OverallGrade", overallGrade);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grade, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle args) {

        RecyclerView gradedAssignmentRecycler = view.findViewById(R.id.gradedAssignmentRecycler);
        ((TextView) view.findViewById(R.id.overallGradeText))
                .setText(getArguments().getString("OverallGrade"));

        gradedAssignmentRecycler.setAdapter(new GradedAssignmentAdapter(
                getArguments().getStringArrayList("TitleTexts"),
                getArguments().getStringArrayList("GradeTexts"))
        );

    }

}
