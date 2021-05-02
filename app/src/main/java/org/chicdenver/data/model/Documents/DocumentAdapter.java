package org.chicdenver.data.model.Documents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentHolder> {

    ArrayList<WfDoc> docs = new ArrayList<>();

    public DocumentAdapter() {
//
//        titles = args.getStringArrayList("Titles");
//        statuses = args.getStringArrayList("Statuses");
//        dates = args.getStringArrayList("Dates");
//        comments = args.getStringArrayList("Comments");

    }

    public void addDoc(WfDoc doc) {
        docs.add(doc);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DocumentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DocumentHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.wf_document, parent, false
        ));
    }


    @Override
    public void onBindViewHolder(@NonNull DocumentHolder holder, int position) {
        holder.setDocument(docs.get(position).title, docs.get(position).date,
                docs.get(position).status, docs.get(position).downloadLink,
                docs.get(position).userComments, docs.get(position).staffComments);
    }

    @Override
    public int getItemCount() {
        return docs.size();
    }

}
