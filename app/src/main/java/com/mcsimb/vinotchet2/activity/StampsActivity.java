package com.mcsimb.vinotchet2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.mcsimb.vinotchet2.Repository;
import com.mcsimb.vinotchet2.R;
import com.mcsimb.vinotchet2.StampsControl;
import com.mcsimb.vinotchet2.common.activity.BaseActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StampsActivity extends BaseActivity implements OnTabSelectedListener {

  private static final String VOL_5 = "0.5";
  private static final String VOL_7 = "0.7";
  private static final String LETTER_5 = "Ю";
  private static final String LETTER_7 = "Я";

  private StampsControl control;
  private Repository repository;
  private String curVol;
  private TabLayout tabLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_stamps);
    initToolbar();
    
    curVol = VOL_5;
    repository = Repository.getInstance();
    control = new StampsControl();

    tabLayout = findViewById(R.id.tab_layout_stamps);
    tabLayout.addOnTabSelectedListener(this);

    Intent intent = getIntent();
    List<String> days = intent.getStringArrayListExtra("days");

    if (days != null) {
      for (String day : days) {
        tabLayout.addTab(tabLayout.newTab().setTag(day).setText(day + "." + repository.month), true);
      }
      setList(days.get(0));
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_vol_stamps) {
      if (curVol.equals(VOL_5)) {
        item.setTitle(R.string.volume5);
        curVol = VOL_7;
      } else {
        item.setTitle(R.string.volume7);
        curVol = VOL_5;
      }
      Tab tab = tabLayout.getTabAt(tabLayout.getTabCount() - 1);
      tab.select();
      setList((String) tab.getTag());
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected boolean canBack() {
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_stamps, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public void onTabSelected(Tab tab) {
    setList((String) tab.getTag());
  }

  @Override
  public void onTabUnselected(Tab tab) {}

  @Override
  public void onTabReselected(Tab tab) {}

  private void setList(String day) {
    List<Integer[]> numbers = new ArrayList<>();
    String startStamp;
    int current = 0;
    String chr;
    numbers.add(new Integer[] {0, current});
    if (curVol.equals(VOL_5)) {
      chr = LETTER_5;
      startStamp = repository.stamps05.get(current);
    } else {
      chr = LETTER_7;
      startStamp = repository.stamps07.get(current);
    }
    int startRange = control.toInt(startStamp, 0, 3);
    int startNumber = control.toInt(startStamp, 9) % 500;
    int atBegin = (startRange / 30 * 30 + 30 - startRange) * 500 + 500 - (startNumber % 500) + 1;
    int sum = 0;

    for (String[] data : repository.dataBase) {
      if (data[2].equals(curVol)) {
        if (data[0].equals(day)) {
          sum += Integer.decode(data[4]);
          if (atBegin - Integer.decode(data[4]) > 0) {
            numbers.get(numbers.size() - 1)[0] += Integer.decode(data[4]);
            numbers.get(numbers.size() - 1)[1] = current;
          } else {
            numbers.get(numbers.size() - 1)[0] += atBegin;
            numbers.get(numbers.size() - 1)[1] = current;
            numbers.add(new Integer[] {Integer.decode(data[4]) - atBegin, current + 1});
          }
        } else if (Integer.decode(data[0]) > Integer.decode(day)) {
          break;
        }
        atBegin -= Integer.decode(data[4]);
        if (atBegin < 0) {
          atBegin += 15000;
          current++;
        }
      }
    }

    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    HashMap<String, String> map;
    map = new HashMap<>();
    map.put("sum", "");
    map.put("start", "");
    map.put("end", "");
    map.put("total", "Акцизная марка " + curVol);
    list.add(map);
    map = new HashMap<>();
    map.put("sum", "");
    map.put("start", "");
    map.put("end", "");
    map.put("total", "");
    list.add(map);

    if (sum > 0) {
      for (int i = 0; i < numbers.size(); i++) {
        String s;
        if (curVol.equals(VOL_5)) {
          s = repository.stamps05.get(numbers.get(i)[1]);
        } else {
          s = repository.stamps07.get(numbers.get(i)[1]);
        }
        String rangeStart;
        String numStart;
        String rangeEnd;
        String numEnd;
        int num1 = control.toInt(s, 3, 9);
        int num2start = 500 - (atBegin + sum) % 500 + 1;
        int num2end = 500 - atBegin % 500;
        if (num2start == 500 || num2end == 500) {
          num1++;
        }
        if (control.toInt(s, 9) > 500) {
          num2start += 500;
          num2end += 500;
        }
        String numEnd1 = String.format("%06d", num1) + String.format("%03d", num2end);
        String numStart1 = String.format("%06d", num1) + String.format("%03d", num2start);
        if (numbers.size() == 1) {
          rangeStart =
              String.format("%03d", control.toInt(s, 0, 3) / 30 * 30 + 30 - (atBegin + sum) / 500);
          rangeEnd = String.format("%03d", control.toInt(s, 0, 3) / 30 * 30 + 30 - atBegin / 500);
          numStart = numStart1;
          numEnd = numEnd1;
        } else if (i == 0) {
          rangeStart =
              String.format(
                  "%03d", control.toInt(s, 0, 3) / 30 * 30 + 30 - (numbers.get(i)[0] / 500));
          numStart = numStart1;
          rangeEnd = String.format("%03d", control.toInt(s, 0, 3) / 30 * 30 + 30);
          if (control.toInt(s, 9) < 500) {
            numEnd = s.substring(3, 9) + "500";
          } else {
            numEnd = String.format("%06d", control.toInt(s, 3, 9) + 1) + "000";
          }
        } else if (i < numbers.size() - 1) {
          rangeStart = s.substring(0, 3);
          numStart = s.substring(3);
          rangeEnd = String.format("%03d", control.toInt(s, 0, 3) / 30 * 30 + 30);
          if (control.toInt(s, 9) < 500) {
            numEnd = s.substring(3, 9) + "500";
          } else {
            numEnd = String.format("%06d", control.toInt(s, 3, 9) + 1) + "000";
          }
        } else {
          rangeStart = s.substring(0, 3);
          numStart = s.substring(3);
          rangeEnd = String.format("%03d", control.toInt(s, 0, 3) / 30 * 30 + 30 - (atBegin / 500));
          numEnd = numEnd1;
        }

        map = new HashMap<>();
        map.put("sum", String.valueOf(numbers.get(i)[0]));
        map.put("start", chr + rangeStart + " " + numStart);
        map.put("end", chr + rangeEnd + " " + numEnd);
        map.put("total", "");
        list.add(map);
      }

      map = new HashMap<>();
      map.put("sum", numbers.size() > 1 ? String.valueOf(sum) : "");
      map.put("start", "");
      map.put("end", "");
      map.put("total", String.valueOf(atBegin));
      list.add(map);
    }

    ListView listView = findViewById(R.id.list);
    SimpleAdapter adapter =
        new SimpleAdapter(
            this,
            list,
            R.layout.list_item_stamps,
            new String[] {"sum", "start", "end", "total"},
            new int[] {R.id.text_sum, R.id.text_start, R.id.text_end, R.id.text_rest});
    listView.setAdapter(adapter);
  }
}
