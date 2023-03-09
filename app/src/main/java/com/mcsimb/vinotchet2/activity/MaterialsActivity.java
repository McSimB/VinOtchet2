package com.mcsimb.vinotchet2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.mcsimb.vinotchet2.Repository;
import com.mcsimb.vinotchet2.R;
import com.mcsimb.vinotchet2.common.activity.BaseActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialsActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_materials);
    initToolbar();
    Repository db = Repository.getInstance();
    Intent intent = getIntent();
    String[] dataList = intent.getStringArrayExtra("data");
    String[] tList = getResources().getStringArray(R.array.material_title_list);
    ArrayList<String> titleList = new ArrayList<>();
    Collections.addAll(titleList, tList);
    for (String s : db.sorts.keySet()) {
      titleList.add(s + " 0,5");
      titleList.add(s + " 0,7");
    }
    Map<String, String> map;
    List<Map<String, String>> data = new ArrayList<>();
    for (int i = 0; i < dataList.length; i++) {
      map = new HashMap<>();
      map.put("title", titleList.get(i));
      map.put("data", dataList[i]);
      data.add(map);
    }

    ListView listView = findViewById(R.id.list);
    ListAdapter adapter =
        new SimpleAdapter(
            this,
            data,
            R.layout.list_item_materials,
            new String[] {"title", "data"},
            new int[] {R.id.text_mat_title, R.id.text_mat_data});
    listView.setAdapter(adapter);
  }

  @Override
  protected boolean canBack() {
    return true;
  }
}
