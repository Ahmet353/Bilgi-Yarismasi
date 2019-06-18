package com.example.ahmet.milyoner;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MoneytreeActivity extends Activity {
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> mdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moneytree_activity);
        recyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        getData();
        int stepID=getIntent().getExtras().getInt("stepID");

        mLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerAdapter= new RecyclerAdapter(mdata,stepID);
        recyclerView.setAdapter(recyclerAdapter);

    }

    public void getData(){
        mdata=getIntent().getExtras().getStringArrayList("moneyTreeValues");
    }
}
