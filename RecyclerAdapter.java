package com.example.ahmet.milyoner;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    ArrayList<String> mdata;
    int i=1;
    int stepID;
    TextView text;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.recyclerview_moneytree_text);
        }
    }

    public RecyclerAdapter(ArrayList<String> data, int stepID) {
        mdata = data;
        this.stepID = stepID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_moneytree_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        text.setText(mdata.get(mdata.size()-position-1));
        if(stepID==getItemCount()-position-1)
            text.setBackgroundResource(R.drawable.trueoption);
        if(stepID>getItemCount()-position-1)
            text.setBackgroundResource(R.drawable.moneytreeaffect);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }


}
