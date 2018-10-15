package com.example.administrator.app2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/10/15.
 */

public class gridView extends Activity implements AdapterView.OnItemClickListener{

    private ArrayList<String> data;
    private ArrayAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview);
        data=new ArrayList<String>();
       GridView listView = (GridView) findViewById(R.id.mylist);
       //init data
        for(int i=0;i<100;i++){
            data.add("item" + i);
        }
        adapter = new ArrayAdapter<String>(gridView.this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.nodata));
        listView.setOnItemClickListener(this);
    }
    public void onItemClick(AdapterView<?> parent,View view,int position,long id) {

    }
}
