package com.mcsimb.vinotchet2;

import static android.view.Gravity.CENTER_VERTICAL;
import static com.mcsimb.vinotchet2.util.Const.ARG_DAY;
import static com.mcsimb.vinotchet2.util.Const.ARG_DAYS;
import static com.mcsimb.vinotchet2.util.Const.ARG_FOR_DAY;
import static com.mcsimb.vinotchet2.util.Const.ARG_LOG_NUMBER;
import static com.mcsimb.vinotchet2.util.Const.ARG_POS_IN_DAY;
import static com.mcsimb.vinotchet2.util.Const.PREF_ACTS_ORDER;
import static com.mcsimb.vinotchet2.util.Const.PREF_REQ_ORDER;
import static com.mcsimb.vinotchet2.util.Const.REGEX_COMMA;
import static com.mcsimb.vinotchet2.util.MathUtils.dp2px;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.mcsimb.vinotchet2.common.activity.BaseActivity;
import java.util.ArrayList;
import java.util.Arrays;

public class LogActivity extends BaseActivity implements View.OnClickListener {

  private static final int INFO_ABOUT_WORKSHOP = 0;
  private static final int REPORT_MOVEMENT_PRODUCTS = 1;
  private static final int ALCOHOL_ACCOUNTING = 2;
  private static final int JOB_BOTTLING_ACCOUNTING = 3;
  private static final int MONTH_SUMMARY = 4;
  private static final int REWORK_ACTS = 5;
  private static final int REQUIREMENTS = 6;
  private Repository repository;
  private boolean firstInitSpinner = true;
  private int logNumber;
  private int curInd;
  private ArrayList<String> days;
  private String[] sortsList;
  private String[] titleList;
  private String[] actsOrder;
  private String[] reqOrder;
  private LogControl control;
  private Button btnPrev;
  private Button btnNext;
  private Spinner sortSpinner;
  private LinearLayout linLay1;
  private LinearLayout linLay2;
  private TableLayout tableLayout;
  private TextView sort;
  private TextView date;
  private TextView tv1;
  private TextView tv2;
  private TextView tv3;
  private TextView tv4;
  private TextView blendNum;
  private Resources res;
  private SharedPreferences prefs;

  @Override
  public void onClick(View v) {
    int next = 1;
    int prev = -1;
    int id = v.getId();
    if (id == R.id.btn_log_prev) {
      control.move(prev);
      showView();
    } else if (id == R.id.btn_log_next) {
      control.move(next);
      showView();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_log);
    initToolbar();

    repository = Repository.getInstance();
    res = getResources();

    tableLayout = findViewById(R.id.table_layout_log);
    btnPrev = findViewById(R.id.btn_log_prev);
    btnNext = findViewById(R.id.btn_log_next);
    sortSpinner = findViewById(R.id.spin_log_sort_select);
    linLay1 = findViewById(R.id.linear_layout_log_1);
    linLay2 = findViewById(R.id.linear_layout_log_2);
    sort = findViewById(R.id.text_log_sort);
    date = findViewById(R.id.text_log_date);
    tv1 = findViewById(R.id.text_log_1);
    tv2 = findViewById(R.id.text_log_2);
    tv3 = findViewById(R.id.text_log_3);
    tv4 = findViewById(R.id.text_log_4);
    blendNum = findViewById(R.id.text_log_blend);

    Intent intent = getIntent();
    logNumber = intent.getIntExtra(ARG_LOG_NUMBER, -1);
    int posInDay = intent.getIntExtra(ARG_POS_IN_DAY, -1);
    String startDay = intent.getStringExtra(ARG_DAY);
    days = intent.getStringArrayListExtra(ARG_DAYS);
    boolean forDay = intent.getBooleanExtra(ARG_FOR_DAY, false);

    for (int i = 0; i < repository.dataBase.size(); i++) {
      if (repository.dataBase.get(i)[0].equals(startDay)) {
        curInd = i + posInDay;
        break;
      }
    }

    StringBuilder defString = new StringBuilder();
    for (int i = 0; i < repository.getSorts(true).length; i++) {
      defString.append(i).append(i == repository.getSorts(true).length - 1 ? "" : REGEX_COMMA);
    }
    prefs = getPreferences(MODE_PRIVATE);
    actsOrder = prefs.getString(PREF_ACTS_ORDER, defString.toString()).split(REGEX_COMMA);
    reqOrder = prefs.getString(PREF_REQ_ORDER, defString.toString()).split(REGEX_COMMA);

    switch (logNumber) {
      case INFO_ABOUT_WORKSHOP:
        info();
        break;
      case REPORT_MOVEMENT_PRODUCTS:
        report();
        break;
      case ALCOHOL_ACCOUNTING:
        alcohol();
        break;
      case JOB_BOTTLING_ACCOUNTING:
        job(forDay);
        break;
      case MONTH_SUMMARY:
        monthSummary();
        break;
      case REWORK_ACTS:
        reworkActs();
        break;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == R.id.menu_require_log) {
      if (logNumber == REWORK_ACTS) {
        item.setTitle(R.string.acts);
        logNumber = REQUIREMENTS;
        requirements();
      } else if (logNumber == REQUIREMENTS) {
        item.setTitle(R.string.require);
        logNumber = REWORK_ACTS;
        reworkActs();
      }
    } else if (itemId == R.id.menu_export_log) {
      if (logNumber == MONTH_SUMMARY) {
        control.export();
        Toast.makeText(this, getString(R.string.export_complete), Toast.LENGTH_LONG).show();
      }
    } else if (itemId == R.id.menu_settings_log) {
      settings();
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
    inflater.inflate(R.menu.menu_log, menu);
    if (logNumber == REWORK_ACTS) {
      menu.findItem(R.id.menu_require_log).setVisible(true);
      menu.findItem(R.id.menu_settings_log).setVisible(true);
    } else if (logNumber == MONTH_SUMMARY) {
      menu.findItem(R.id.menu_export_log).setVisible(true);
    }
    return super.onCreateOptionsMenu(menu);
  }

  private void info() {
    this.setTitle(res.getStringArray(R.array.logs_titles)[0]);
    control = new InfoLogControl(curInd);
    titleList = res.getStringArray(R.array.info_title_list);
    initSpinner();
  }

  private void report() {
    this.setTitle(res.getStringArray(R.array.logs_titles)[1]);
    control = new ReportLogControl(curInd);
    titleList = res.getStringArray(R.array.report_title_list);
    initSpinner();
  }

  private void alcohol() {
    this.setTitle(res.getStringArray(R.array.logs_titles)[2]);
    control = new AlcoholLogControl(curInd);
    titleList = res.getStringArray(R.array.alcohol_title_list);
    sortSpinner.setVisibility(View.INVISIBLE);
    blendNum.setVisibility(View.INVISIBLE);
    date.setVisibility(View.VISIBLE);
    sort.setVisibility(View.VISIBLE);
    showView();
  }

  private void job(boolean forDay) {
    this.setTitle(res.getStringArray(R.array.logs_titles)[3]);
    control = new JobLogControl(curInd, forDay);
    titleList = res.getStringArray(R.array.job_title_list);
    sortSpinner.setVisibility(View.INVISIBLE);
    blendNum.setVisibility(View.INVISIBLE);
    date.setVisibility(View.VISIBLE);
    sort.setVisibility(View.VISIBLE);
    showView();
  }

  private void monthSummary() {
    this.setTitle(R.string.month_summary);
    control = new MonthSummaryControl(repository.getSorts(false)[0]);
    String[] iList = res.getStringArray(R.array.info_title_list);
    String[] rList = res.getStringArray(R.array.report_title_list);
    titleList = new String[8];
    titleList[0] = iList[1];
    titleList[1] = iList[3];
    titleList[2] = iList[5];
    titleList[3] = iList[8];
    titleList[5] = rList[2];
    titleList[6] = iList[9];
    titleList[7] = rList[7];
    linLay1.setVisibility(View.VISIBLE);
    linLay2.setVisibility(View.VISIBLE);
    initSpinner();
  }

  private void reworkActs() {
    this.setTitle(R.string.rework_acts_title);
    control = new ReworkActsControl(days.toArray(new String[0]));
    sortSpinner.setVisibility(View.INVISIBLE);
    blendNum.setVisibility(View.INVISIBLE);
    sort.setVisibility(View.VISIBLE);
    titleList = new String[repository.getSorts(true).length + 1];
    reorderTitle(titleList, actsOrder);
    showView();
  }

  private void requirements() {
    this.setTitle(R.string.require_title);
    control = new RequirementsControl(days.toArray(new String[0]));
    titleList = new String[repository.getSorts(true).length + 1];
    reorderTitle(titleList, reqOrder);
    titleList[repository.getSorts(true).length] = res.getString(R.string.sum);
    showView();
  }

  private void reorderTitle(String[] data, String[] order) {
    for (int i = 0; i < repository.getSorts(true).length; i++) {
      data[i] = repository.getSorts(true)[Integer.parseInt(order[i])];
    }
  }

  private String[] reorderData(String[] data, String[] order) {
    String[] result = new String[repository.getSorts(true).length + 1];
    for (int i = 0; i < repository.getSorts(true).length; i++) {
      result[i] = data[Integer.parseInt(order[i])];
    }
    result[repository.getSorts(true).length] = data[repository.getSorts(true).length];
    return result;
  }

  private void settings() {
    LayoutInflater factory = LayoutInflater.from(this);
    View dialogView = factory.inflate(R.layout.dialog_settings, null);
    EditText value = dialogView.findViewById(R.id.edit_dialog_settings);
    StringBuilder str = new StringBuilder();
    if (logNumber == REWORK_ACTS) {
      for (int i = 0; i < actsOrder.length; i++) {
        str.append(actsOrder[i]).append(i == actsOrder.length - 1 ? "" : REGEX_COMMA);
      }
    } else if (logNumber == REQUIREMENTS) {
      for (int i = 0; i < reqOrder.length; i++) {
        str.append(reqOrder[i]).append(i == reqOrder.length - 1 ? "" : REGEX_COMMA);
      }
    }

    value.setText(str.toString());
    ListView list = dialogView.findViewById(R.id.list_dialog_settings);
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, repository.getSorts(true));
    list.setAdapter(adapter);
    AlertDialog dialog =
        new AlertDialog.Builder(this)
            .setView(dialogView)
            .setNegativeButton(R.string.cancel, (di, whichButton) -> di.dismiss())
            .setPositiveButton(
                R.string.ok,
                (di, whichButton) -> {
                  if (logNumber == REWORK_ACTS) {
                    actsOrder = value.getText().toString().split(REGEX_COMMA);
                    prefs.edit().putString(PREF_ACTS_ORDER, value.getText().toString()).apply();
                    reworkActs();
                  } else if (logNumber == REQUIREMENTS) {
                    reqOrder = value.getText().toString().split(REGEX_COMMA);
                    prefs.edit().putString(PREF_REQ_ORDER, value.getText().toString()).apply();
                    requirements();
                  }
                })
            .create();
    dialog.show();
  }

  private void initSpinner() {
    sortsList = repository.getSorts(false);
    ArrayAdapter<String> adapterSort =
        new ArrayAdapter<>(this, R.layout.spinner_item_layout, sortsList);
    sortSpinner.setAdapter(adapterSort);
    if (logNumber != MONTH_SUMMARY) {
      sortSpinner.setSelection(
          Arrays.binarySearch(sortsList, repository.dataBase.get(control.curIndex)[1]));
    }
    sortSpinner.setOnItemSelectedListener(
        new OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (firstInitSpinner) {
              firstInitSpinner = false;
            } else {
              control.initSnap(sortsList[position]);
              if (logNumber < MONTH_SUMMARY && !control.indexList.isEmpty()) {
                control.setSnapIndex(control.indexList.size() - 1);
              }
              showView();
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });
    showView();
  }

  private void showView() {
    String[] dataList = control.getDataList();
    btnPrev.setVisibility(control.btnPrevVisibility() ? View.VISIBLE : View.INVISIBLE);
    btnNext.setVisibility(control.btnNextVisibility() ? View.VISIBLE : View.INVISIBLE);
    if (logNumber == REWORK_ACTS) {
      dataList = reorderData(dataList, actsOrder);
      sort.setText(
          getString(
              R.string.day_month_year, control.days[control.curDay], repository.month, repository.year));
    } else if (logNumber == REQUIREMENTS) {
      dataList = reorderData(dataList, reqOrder);
      sort.setText(
          control.days[control.curDay] + "." + repository.month + "     № " + (control.curDay + 1));
    } else if (logNumber == MONTH_SUMMARY) {
      blendNum.setText(control.curBlend + 1 + " купаж");
      tv1.setText("0,5 - " + control.count05);
      tv2.setText("0,7 - " + control.count07);
      tv3.setText("0,5 - " + control.noZero2(control.count05 * 0.05));
      tv4.setText("0,7 - " + control.noZero2(control.count07 * 0.07));
    } else if (logNumber == ALCOHOL_ACCOUNTING) {
      if (control.total) {
        date.setText("Всего");
      } else {
        date.setText(
            getString(
                R.string.day_month_year,
                repository.dataBase.get(control.curIndex)[0],
                repository.month,
                repository.year));
      }
      sort.setText(
          repository.dataBase.get(control.curIndex)[1]
              + " "
              + repository.dataBase.get(control.curIndex)[2].replace(".", ","));
    } else if (logNumber == JOB_BOTTLING_ACCOUNTING) {
      date.setText(
          getString(
              R.string.day_month_year,
              repository.dataBase.get(control.curIndex)[0],
              repository.month,
              repository.year));
      sort.setText(repository.dataBase.get(control.curIndex)[1]);
    } else {
      if (!control.divBlendFlag) {
        blendNum.setText(control.blend + 1 + " купаж");
      } else {
        blendNum.setText(control.blend + "/" + (control.blend + 1) + " купаж");
      }
    }

    tableLayout.removeAllViews();
    TableLayout.LayoutParams params =
        new TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
    for (int i = 0; i < dataList.length; i++) {
      TableRow tableRow = new TableRow(this);
      tableRow.setGravity(CENTER_VERTICAL);
      TextView titleTextView = new TextView(this);
      TextView dataTextView = new TextView(this);
      tableRow.setLayoutParams(params);
      titleTextView.setTextSize(16);
      titleTextView.setTextColor(0xFF666666);
      titleTextView.setPadding(dp2px(8), 0, 0, 0);
      titleTextView.setText(titleList[i]);
      dataTextView.setTextSize(30);
      if (i == 11) {
        dataTextView.setTextSize(20);
      }
      dataTextView.setText(dataList[i]);
      tableRow.addView(titleTextView);
      tableRow.addView(dataTextView);
      if (logNumber < MONTH_SUMMARY) {
        if (i % 2 == 0) {
          tableRow.setBackgroundResource(R.drawable.rect_frame);
        } else {
          tableRow.setBackgroundResource(R.drawable.rect_solid);
        }
      } else if (logNumber == MONTH_SUMMARY) {
        if (i != 4) {
          tableRow.setBackgroundResource(R.drawable.rect_solid);
        }
      } else {
        tableRow.setBackgroundResource(R.drawable.rect_frame);
        if (logNumber == REQUIREMENTS
            && !(titleList[i] == null)
            && titleList[i].equals(res.getString(R.string.sum))) {
          tableRow.setBackgroundResource(R.drawable.rect_solid);
        }
      }
      tableLayout.addView(tableRow);
    }
  }
}
