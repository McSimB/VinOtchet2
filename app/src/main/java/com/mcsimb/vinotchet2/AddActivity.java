package com.mcsimb.vinotchet2;

import static com.mcsimb.vinotchet2.util.Const.ARG_COUNTER1;
import static com.mcsimb.vinotchet2.util.Const.ARG_COUNTER2;
import static com.mcsimb.vinotchet2.util.Const.ARG_DAY;
import static com.mcsimb.vinotchet2.util.Const.ARG_MONTH;
import static com.mcsimb.vinotchet2.util.Const.ARG_SORT;
import static com.mcsimb.vinotchet2.util.Const.ARG_SORT_LIST;
import static com.mcsimb.vinotchet2.util.Const.ARG_TARE;
import static com.mcsimb.vinotchet2.util.Const.ARG_YEAR;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.mcsimb.vinotchet2.common.activity.BaseActivity;

public class AddActivity extends BaseActivity implements View.OnClickListener {

  private EditText editedDay;
  private String receivedDay;
  private String sort;
  private String tare;
  private EditText counter1;
  private EditText counter2;

  @Override
  protected void onCreate(Bundle savedInstantState) {
    super.onCreate(savedInstantState);
    setContentView(R.layout.activity_add);
    initToolbar();

    counter1 = findViewById(R.id.add_counter1);
    counter2 = findViewById(R.id.add_counter2);
    editedDay = findViewById(R.id.set_date);
    TextView monthYear = findViewById(R.id.month_year);

    String[] tareList = {"0,5", "0,7"};

    Intent intent = getIntent();
    receivedDay = intent.getStringExtra(ARG_DAY);
    String[] sortList = intent.getStringArrayExtra(ARG_SORT_LIST);

    monthYear.setText(
        getString(
            R.string.month_year,
            intent.getStringExtra(ARG_MONTH),
            intent.getStringExtra(ARG_YEAR)));

    editedDay.setText(receivedDay);

    ArrayAdapter<String> sortAdapter =
        new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sortList);
    Spinner sortSpinner = findViewById(R.id.set_sort);
    sortSpinner.setAdapter(sortAdapter);
    sortSpinner.setOnItemSelectedListener(
        new OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            sort = sortList[position];
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });

    ArrayAdapter<String> tareAdapter =
        new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tareList);
    Spinner tareSpinner = findViewById(R.id.set_tare);
    tareSpinner.setAdapter(tareAdapter);
    tareSpinner.setOnItemSelectedListener(
        new OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            tare = tareList[position].replace(",", ".");
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });
  }

  @Override
  protected boolean canBack() {
    return true;
  }

  @Override
  public void onClick(View v) {
    Intent intent = new Intent();
    if (v.getId() == R.id.add_ok) {
      if (Integer.parseInt(editedDay.getText().toString()) < Integer.parseInt(receivedDay)) {
        Toast.makeText(AddActivity.this, R.string.bad_day, Toast.LENGTH_SHORT).show();
        return;
      }
      if ((counter1.getText().toString().equals(""))
          || (counter2.getText().toString().equals(""))) {
        Toast.makeText(AddActivity.this, R.string.bad_counter, Toast.LENGTH_SHORT).show();
        return;
      }
      intent.putExtra(ARG_DAY, editedDay.getText().toString());
      intent.putExtra(ARG_SORT, sort);
      intent.putExtra(ARG_TARE, tare);
      intent.putExtra(ARG_COUNTER1, counter1.getText().toString());
      intent.putExtra(ARG_COUNTER2, counter2.getText().toString());
      setResult(RESULT_OK, intent);
      finish();
    }
  }
}
