package com.example.fatihdemirel.milyoner;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BestGamerRecyclerAdapter extends RecyclerView.Adapter<BestGamerRecyclerAdapter.viewHolder> {

    List<Gamer> list;
    Context context;
    Typeface ty;


    public class viewHolder extends RecyclerView.ViewHolder {
        TextView siraText, usernameText, pointText;
        ImageView gamerPhoto;

        public viewHolder(View itemView) {
            super(itemView);
            siraText = (TextView) itemView.findViewById(R.id.siraTxt);
            usernameText = (TextView) itemView.findViewById(R.id.userNameText);
            pointText = (TextView) itemView.findViewById(R.id.userPointText);
            gamerPhoto=(ImageView)itemView.findViewById(R.id.recyclerviewGamerPhoto);

        }
    }

    public BestGamerRecyclerAdapter(List<Gamer> list, Context context,Typeface ty) {
        this.list = list;
        this.context=context;
        this.ty=ty;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bestgamerrecycler_view, parent, false);
        viewHolder vh = new viewHolder(v);
        context=parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {
        holder.siraText.setText(String.valueOf(position + 1));
        holder.usernameText.setText(list.get(position).username);
        holder.pointText.setText(String.valueOf(list.get(position).getPoint()+" ₺"));
        holder.pointText.setTypeface(ty);
        holder.siraText.setTypeface(ty);
        holder.usernameText.setTypeface(ty);
        Picasso.with(context).load(list.get(position).getImage()).into(holder.gamerPhoto);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
