package com.mcsimb.vinotchet2;

import static com.mcsimb.vinotchet2.util.Const.ARG_COUNTER1;
import static com.mcsimb.vinotchet2.util.Const.ARG_COUNTER2;
import static com.mcsimb.vinotchet2.util.Const.ARG_DATA;
import static com.mcsimb.vinotchet2.util.Const.ARG_DAY;
import static com.mcsimb.vinotchet2.util.Const.ARG_DAYS;
import static com.mcsimb.vinotchet2.util.Const.ARG_FOR_DAY;
import static com.mcsimb.vinotchet2.util.Const.ARG_LOG_NUMBER;
import static com.mcsimb.vinotchet2.util.Const.ARG_MONTH;
import static com.mcsimb.vinotchet2.util.Const.ARG_POS_IN_DAY;
import static com.mcsimb.vinotchet2.util.Const.ARG_SORT;
import static com.mcsimb.vinotchet2.util.Const.ARG_SORT_LIST;
import static com.mcsimb.vinotchet2.util.Const.ARG_TARE;
import static com.mcsimb.vinotchet2.util.Const.ARG_YEAR;
import static com.mcsimb.vinotchet2.util.Const.FALSE;
import static com.mcsimb.vinotchet2.util.Const.PREF_USED_SORTS;
import static com.mcsimb.vinotchet2.util.Const.TRUE;
import static com.mcsimb.vinotchet2.util.Utils.stringToTimestamp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mcsimb.vinotchet2.activity.MaterialsActivity;
import com.mcsimb.vinotchet2.activity.StampsActivity;
import com.mcsimb.vinotchet2.adapter.MainPagerAdapter;
import com.mcsimb.vinotchet2.adapter.RecyclerViewAdapter;
import com.mcsimb.vinotchet2.common.activity.BaseActivity;
import com.mcsimb.vinotchet2.dialog.BlendAddDialog;
import com.mcsimb.vinotchet2.dialog.MonthSelectDialog;
import com.mcsimb.vinotchet2.dialog.StampsAddDialog;
import com.mcsimb.vinotchet2.dialog.UsedSortsSelectDialog;
import com.mcsimb.vinotchet2.util.MathUtils;
import com.mcsimb.vinotchet2.viewmodel.MainViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements OnTabSelectedListener {

  public static String[] monthsNames;
  private MainViewModel viewModel;
  private Repository repository;
  private Menu mainMenu;
  private SharedPreferences prefs;
  private TabLayout tabLayout;
  private ViewPager2 viewPager;
  private MainPagerAdapter pagerAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initToolbar();

    viewModel =
        new ViewModelProvider(this, ViewModelProvider.Factory.from(MainViewModel.INITIALIZER))
            .get(MainViewModel.class);

    prefs = getPreferences(MODE_PRIVATE);
    monthsNames = getResources().getStringArray(R.array.months);
    repository = Repository.getInstance();

    tabLayout = findViewById(R.id.tabs);
    tabLayout.addOnTabSelectedListener(this);

    viewPager = findViewById(R.id.viewpager);
    pagerAdapter = new MainPagerAdapter(this);
    viewPager.setAdapter(pagerAdapter);
    new TabLayoutMediator(
            tabLayout,
            viewPager,
            (tab, position) -> {
              String day = viewModel.getDays().get(position);
              tab.setTag(day);
              tab.setText(
                  getString(R.string.day_month_year, day, repository.month, repository.year));
            })
        .attach();

    init();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == R.id.menu_add_main) {
      Intent intent = new Intent(this, AddActivity.class);
      if (!repository.dataBase.isEmpty()) {
        intent.putExtra(ARG_DAY, repository.dataBase.get(repository.dataBase.size() - 1)[0]);
      } else {
        intent.putExtra(ARG_DAY, "1");
      }
      intent.putExtra(ARG_MONTH, repository.month);
      intent.putExtra(ARG_YEAR, repository.year);
      intent.putExtra(ARG_SORT_LIST, repository.getSorts(false));
      startActivityForResult(intent, 1);
      return true;

    } else if (itemId == R.id.menu_other_month_main) {
      selectMonth();
      return true;

    } else if (itemId == R.id.menu_new_month_main) {
      String nMonth = viewModel.newMonth();
      if (nMonth.equals("00")) {
        Toast.makeText(this, getString(R.string.month_exist), Toast.LENGTH_LONG).show();
      } else {
        createPages(nMonth);
        String m = getResources().getStringArray(R.array.months)[Integer.parseInt(nMonth) - 1];
        Toast.makeText(this, getString(R.string.create, m), Toast.LENGTH_LONG).show();
      }
      return true;

    } else if (itemId == R.id.menu_month_summary_main) {
      Intent intent2 = new Intent(MainActivity.this, LogActivity.class);
      intent2.putExtra(ARG_LOG_NUMBER, 4);
      startActivity(intent2);
      return true;

    } else if (itemId == R.id.menu_rework_acts_main) {
      Intent intent3 = new Intent(MainActivity.this, LogActivity.class);
      intent3.putExtra(ARG_LOG_NUMBER, 5);
      intent3.putExtra(ARG_DAYS, (ArrayList<String>) viewModel.getDays());
      startActivity(intent3);
      return true;

    } else if (itemId == R.id.menu_doc_stamps_main) {
      Intent intent4 = new Intent(MainActivity.this, StampsActivity.class);
      intent4.putExtra(ARG_DAYS, (ArrayList<String>) viewModel.getDays());
      startActivity(intent4);
      return true;

    } else if (itemId == R.id.menu_settings_main) {
      settings();
      return true;

    } else if (itemId == R.id.menu_exit_main) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);
    mainMenu = menu;
    if (viewModel.getDays().isEmpty()) {
      mainMenu.findItem(R.id.menu_add_main).setVisible(true);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (repository.dataBase != null) {
      boolean visibility = !repository.dataBase.isEmpty();
      menu.findItem(R.id.menu_month_summary_main).setVisible(visibility);
      menu.findItem(R.id.menu_new_month_main).setVisible(visibility);
      menu.findItem(R.id.menu_rework_acts_main).setVisible(visibility);
      menu.findItem(R.id.menu_doc_stamps_main).setVisible(visibility);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @SuppressLint("InflateParams")
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      boolean addNewStamps = false;
      var day = data.getStringExtra(ARG_DAY);
      var sort = data.getStringExtra(ARG_SORT);
      var tare = data.getStringExtra(ARG_TARE);
      var counter1 = data.getStringExtra(ARG_COUNTER1);
      var counter2 = data.getStringExtra(ARG_COUNTER2);

      if (viewModel.saveProduct(day, sort, tare, counter1, counter2)) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_input_blend, null);
        EditText editText = dialogView.findViewById(R.id.edit_text_volume_dialog_blend);
        TextView textView = dialogView.findViewById(R.id.text_view_sort_dialog_blend);
        textView.setText(sort);
        new BlendAddDialog(
            this,
            dialogView,
            (dialog, whichButton) -> viewModel.addBlend(sort, editText.getText().toString()));
      }

      if (tare != null && viewModel.checkStamps(tare)) {
        addNewStamps = true;
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_stamps, null);
        TextView letterTextView = dialogView.findViewById(R.id.text_view_letter_stamps);
        EditText rangeEditText = dialogView.findViewById(R.id.edit_text_range_stamps);
        EditText numberEditText = dialogView.findViewById(R.id.edit_text_number_stamps);
        letterTextView.setText(tare.equals("0.5") ? "Ю" : "Я");
        String stamp =
            tare.equals("0.5")
                ? repository.stamps05.get(repository.stamps05.size() - 1)
                : repository.stamps07.get(repository.stamps07.size() - 1);
        int range = viewModel.toInt(stamp, 0, 3) / 30 * 30 + 30;
        rangeEditText.setText(
            range == 120 ? "001" : String.format(Locale.getDefault(), "%03d", range + 1));
        int number = viewModel.toInt(stamp, 9);
        numberEditText.setText(
            number > 500 ? stamp.substring(3, 9) + "501" : stamp.substring(3, 9) + "001");
        new StampsAddDialog(
            this,
            dialogView,
            (dialog, whichButton) -> {
              viewModel.addStampsRange(
                  rangeEditText.getText().toString(), numberEditText.getText().toString(), tare);
              updateView(day);
            });
      }

      if (!addNewStamps) {
        updateView(day);
      }
    }
  }

  private void init() {
    initBottomNavigation();
    if (repository.pathExists(this)) {
      repository.year = String.format("%ty", Calendar.getInstance());
      Integer[] existMonths = repository.existingMonths();
      if (existMonths.length != 0) {
        Integer month = existMonths[existMonths.length - 1];
        createPages(String.format(Locale.getDefault(), "%02d", month));
      } else {
        Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
      }
    } else {
      Toast.makeText(this, getString(R.string.no_dir), Toast.LENGTH_LONG).show();
    }
  }

  private void initBottomNavigation() {
    BottomNavigationView navigation = findViewById(R.id.bottom_nav);
    navigation.setOnItemSelectedListener(
        item -> {
          int id = item.getItemId();

          if (id == R.id.menu_statistic) {
            View dialogStatView = getLayoutInflater().inflate(R.layout.dialog_statistic, null);
            TextView made = dialogStatView.findViewById(R.id.tv_dialog_made);
            TextView rest = dialogStatView.findViewById(R.id.tv_dialog_rest);
            String[] stat = viewModel.getStatistic();
            made.setText(stat[0]);
            rest.setText(stat[1]);
            AlertDialog dialogStat =
                new MaterialAlertDialogBuilder(MainActivity.this)
                    .setIcon(R.drawable.ic_info)
                    .setTitle(R.string.statistic)
                    .setView(dialogStatView)
                    .setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss())
                    .create();
            dialogStat.show();
            return true;

          } else if (id == R.id.menu_production) {
            if (repository.dataBase.size() == 0) {
              Toast.makeText(this, R.string.no_production, Toast.LENGTH_SHORT).show();
            } else {
              View dialogProdView = getLayoutInflater().inflate(R.layout.dialog_production, null);
              ListView prodListView = dialogProdView.findViewById(R.id.lv_dialog_production);
              TextView sumTextView = dialogProdView.findViewById(R.id.tv_sum_dialog_production);
              String[] list = viewModel.getProduction();
              float sum = 0f;
              for (int i = 0; i < list.length; i++) {
                sum += Float.parseFloat(list[i]);
                list[i] = viewModel.getDays().get(i) + ".    --    " + list[i];
              }
              sumTextView.setText(String.valueOf(MathUtils.round2(sum)));
              ArrayAdapter<String> adapter =
                  new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, list);
              prodListView.setAdapter(adapter);
              AlertDialog dialogProd =
                  new MaterialAlertDialogBuilder(MainActivity.this)
                      .setIcon(R.drawable.ic_assesment)
                      .setTitle(R.string.days_production)
                      .setView(dialogProdView)
                      .setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss())
                      .create();
              dialogProd.show();
            }
            return true;

          } else if (id == R.id.menu_materials) {
            if (repository.dataBase.size() == 0) {
              Toast.makeText(this, R.string.no_production, Toast.LENGTH_SHORT).show();
            } else {
              View dialogMatView = getLayoutInflater().inflate(R.layout.dialog_materials, null);
              NumberPicker numPickStart =
                  dialogMatView.findViewById(R.id.np_dialog_materials_start);
              NumberPicker numPickEnd = dialogMatView.findViewById(R.id.np_dialog_materials_end);
              if (!viewModel.getDays().isEmpty()) {
                int min = Integer.parseInt(viewModel.getDays().get(0));
                int max = Integer.parseInt(viewModel.getDays().get(viewModel.getDays().size() - 1));
                numPickStart.setMinValue(min);
                numPickStart.setMaxValue(max);
                numPickEnd.setMinValue(min);
                numPickEnd.setMaxValue(max);
                Tab selectedTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
                if (selectedTab != null) {
                  //noinspection ConstantConditions
                  numPickStart.setValue(Integer.parseInt((String) selectedTab.getTag()));
                  numPickEnd.setValue(Integer.parseInt((String) selectedTab.getTag()));
                }
                AlertDialog materialsDialog =
                    new MaterialAlertDialogBuilder(MainActivity.this)
                        .setIcon(R.drawable.ic_calendar_range)
                        .setTitle(R.string.select_period)
                        .setView(dialogMatView)
                        .setNegativeButton(
                            R.string.cancel, (dialog, whichButton) -> dialog.dismiss())
                        .setPositiveButton(
                            R.string.ok,
                            (dialog, whichButton) -> {
                              int start = numPickStart.getValue();
                              int end = numPickEnd.getValue();
                              if (start <= end) {
                                String[] data = viewModel.material(start, end);
                                Intent intent =
                                    new Intent(MainActivity.this, MaterialsActivity.class);
                                intent.putExtra(ARG_DATA, data);
                                startActivity(intent);
                              } else {
                                Toast.makeText(
                                        MainActivity.this, R.string.bad_data, Toast.LENGTH_SHORT)
                                    .show();
                              }
                            })
                        .create();
                materialsDialog.show();
              }
            }
            return true;
          }
          return false;
        });
  }

  private void updateView(String day) {
    long date = stringToTimestamp(day + "." + repository.month + "." + repository.year);
    if (repository.dataBase.size() == 1) {
      pagerAdapter.add(date);
    } else {
      Tab selectedTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
      if (selectedTab != null) {
        if (day.equals(selectedTab.getTag())) {
          RecyclerView recyclerView = findViewById(R.id.recycler);
          RecyclerViewAdapter adapter = (RecyclerViewAdapter) recyclerView.getAdapter();
          List<ProductViewItem> items = viewModel.getDayProducts(day);
          if (adapter != null) {
            adapter.setItems(items);
          }
        } else {
          pagerAdapter.add(date);
          viewPager.post(() -> viewPager.setCurrentItem(pagerAdapter.getItemCount() - 1));
          mainMenu.findItem(R.id.menu_add_main).setVisible(true);
        }
      }
    }
  }

  private void createPages(String month) {
    this.setTitle(R.string.production_log);
    viewModel.init(month);
    repository.setUsedSortsFlags(getUsedSortsFromPrefs());
    pagerAdapter.removeAllPages();

    for (String day : viewModel.getDays()) {
      long date = stringToTimestamp(day + "." + repository.month + "." + repository.year);
      pagerAdapter.add(date);
    }

    viewPager.post(() -> viewPager.setCurrentItem(pagerAdapter.getItemCount() - 1));
  }

  private void selectMonth() {
    Integer[] existMonths = repository.existingMonths();
    String[] existMonthsNames = new String[existMonths.length];
    for (int i = 0; i < existMonths.length; i++) {
      existMonthsNames[i] = monthsNames[existMonths[i] - 1];
    }
    new MonthSelectDialog(
        this,
        existMonthsNames,
        (dialog, whichButton) -> {
          int monthNumber = Arrays.asList(monthsNames).indexOf(existMonthsNames[whichButton]) + 1;
          String month = String.format(Locale.getDefault(), "%02d", monthNumber);
          if (!month.equals(repository.month)) {
            createPages(month);
          }
        });
  }

  private void settings() {
    String[] sortList = repository.getSorts(true);
    int length = sortList.length;
    boolean[] checked = new boolean[length];

    CharSequence usedSorts = getUsedSortsFromPrefs();

    for (int i = 0; i < length; i++) {
      checked[i] = usedSorts.charAt(i) == TRUE;
    }
    new UsedSortsSelectDialog(
        this,
        sortList,
        checked,
        (dialog, whichButton) -> {
          StringBuilder result = new StringBuilder();
          for (int i = 0; i < length; i++) {
            result.append(checked[i] ? TRUE : FALSE);
          }
          prefs.edit().putString(PREF_USED_SORTS, result.toString()).apply();
          repository.setUsedSortsFlags(result.toString());
        });
  }

  private CharSequence getUsedSortsFromPrefs() {
    String[] sortList = repository.getSorts(true);
    StringBuilder defaultString = new StringBuilder();
    for (int i = 0; i < sortList.length; i++) {
      defaultString.append(TRUE);
    }
    return prefs.getString(PREF_USED_SORTS, defaultString.toString());
  }

  public Menu getMainMenu() {
    return mainMenu;
  }

  public MainPagerAdapter getPagerAdapter() {
    return pagerAdapter;
  }

  @Override
  public void onTabSelected(Tab tab) {
    String day = (String) tab.getTag();
    String lastDay = viewModel.getDays().get(viewModel.getDays().size() - 1);
    if (mainMenu != null && day != null) {
      mainMenu
          .findItem(R.id.menu_add_main)
          .setVisible((repository.dataBase.isEmpty()) || (day.equals(lastDay)));
    }
  }

  @Override
  public void onTabUnselected(Tab tab) {}

  @Override
  public void onTabReselected(Tab tab) {
    PopupMenu popup = new PopupMenu(MainActivity.this, tab.view);
    getMenuInflater().inflate(R.menu.popup_menu_tab_main, popup.getMenu());
    popup.setOnMenuItemClickListener(
        item -> {
          int itemId = item.getItemId();
          if (itemId == R.id.popup_menu_main_info) {
            Intent intent = new Intent(MainActivity.this, LogActivity.class);
            intent.putExtra(ARG_LOG_NUMBER, 3);
            intent.putExtra(ARG_POS_IN_DAY, 0);
            intent.putExtra(ARG_DAY, (String) tab.getTag());
            intent.putExtra(ARG_FOR_DAY, true);
            startActivity(intent);
          } else if (itemId == R.id.popup_menu_main_materials) {
            //noinspection ConstantConditions
            int day = Integer.parseInt((String) tab.getTag());
            String[] data = viewModel.material(day, day);
            Intent intent2 = new Intent(MainActivity.this, MaterialsActivity.class);
            intent2.putExtra(ARG_DATA, data);
            startActivity(intent2);
          }
          return false;
        });
    popup.show();
  }
}
