package com.mcsimb.vinotchet2.viewmodel;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;
import static com.mcsimb.vinotchet2.util.Utils.roundStr;
import static com.mcsimb.vinotchet2.util.Utils.stringToTimestamp;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import com.mcsimb.vinotchet2.AlcoholLogControl;
import com.mcsimb.vinotchet2.App;
import com.mcsimb.vinotchet2.ProductViewItem;
import com.mcsimb.vinotchet2.ReportLogControl;
import com.mcsimb.vinotchet2.Repository;
import com.mcsimb.vinotchet2.StampsControl;
import com.mcsimb.vinotchet2.util.MathUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainViewModel extends ViewModel {

  public static final ViewModelInitializer<MainViewModel> INITIALIZER =
      new ViewModelInitializer<>(
          MainViewModel.class,
          creationExtras -> {
            App app = (App) creationExtras.get(APPLICATION_KEY);
            assert app != null;
            return new MainViewModel(app);
          });
  private final Repository repository;
  private final List<String> days;

  public MainViewModel(App app) {
    repository = Repository.getInstance();
    days = new ArrayList<>();
  }

  public void init(String month) {
    repository.month = month;
    repository.readSorts();
    repository.readIcons();
    repository.readData();
    repository.readCounters();
    repository.readStamps();

    initDays();
  }

  public List<ProductViewItem> getDayProducts(String day) {
    ArrayList<ProductViewItem> products = new ArrayList<>();
    StampsControl counter = new StampsControl();
    List<String[]> dayStampsList = counter.getDayStamps(day);
    int i = 0;
    for (String[] data : repository.dataBase) {
      if (data[0].equals(day)) {
        products.add(
            new ProductViewItem(
                stringToTimestamp(data[0] + "." + repository.month + "." + repository.year),
                data[1] + "  " + data[2],
                data[3],
                data[4],
                dayStampsList.get(i)[0],
                dayStampsList.get(i)[1],
                repository.sortsIcons.get(data[1])));
        i++;
      }
    }
    return products;
  }

  public boolean checkStamps(String vol) {
    int sum = 0;
    ArrayList<String> stamps = vol.equals("0.5") ? repository.stamps05 : repository.stamps07;
    String startStamp = stamps.get(0);
    int startRange = toInt(startStamp, 0, 3);
    int startNumber = toInt(startStamp, 9) % 500;
    if (startNumber == 0) startNumber = 500;
    int sumStamps;
    if (startRange % 30 == 0) {
      sumStamps = 500 - startNumber + 1;
    } else {
      sumStamps = (startRange / 30 * 30 + 30 - startRange) * 500 + 500 - startNumber + 1;
    }
    for (int i = 1; i < stamps.size(); i++) {
      sumStamps += 15000;
    }
    for (String[] data : repository.dataBase) {
      if (data[2].equals(vol)) {
        sum += Integer.parseInt(data[4]);
      }
    }
    return sum >= sumStamps;
  }

  public void addStampsRange(String range, String number, String tare) {
    repository.saveStampsRange(range, number, tare);
  }

  public boolean saveProduct(String day, String sort, String tare, String count1, String count2) {
    if (!days.contains(day)) {
      days.add(day);
    }
    repository.dataBase.add(new String[] {day, sort, tare, count1, count2});
    repository.writeData();
    ArrayList<String> blends = repository.sorts.get(sort);
    //noinspection ConstantConditions
    if (Float.parseFloat(blends.get(blends.size() - 1)) == 0) {
      return true;
    }
    ReportLogControl reportControl = new ReportLogControl(sort);
    reportControl.setSnapIndex(reportControl.indexList.size() - 1);
    reportControl.isDivBlend();
    return reportControl.divBlendFlag;
  }

  public void removeProduct(String day, int positionInDay) {
    repository.removeProduct(day, positionInDay);
    initDays();
  }

  public void removeDay(int position) {
    days.remove(position);
  }

  public void addBlend(String sort, String volume) {
    ArrayList<String> blends = repository.sorts.get(sort);
    //noinspection ConstantConditions
    if (Float.parseFloat(blends.get(blends.size() - 1)) == 0) {
      blends.set(blends.size() - 1, volume);
    } else {
      blends.add(volume);
    }
    repository.sorts.put(sort, blends);

    ArrayList<String[]> sorts = new ArrayList<>();
    for (String sortName : repository.sorts.keySet()) {
      ArrayList<String> b = new ArrayList<>();
      b.add(sortName);
      b.addAll(repository.sorts.get(sortName));
      sorts.add(b.toArray(new String[0]));
    }
    repository.writeSorts(sorts);
  }

  public String[] getStatistic() {
    int count05 = 0;
    int count07 = 0;
    ArrayList<String[]> restsList = getRest();
    for (String[] s : repository.dataBase) {
      if (s[2].equals("0.5")) {
        count05 += Integer.parseInt(s[4]);
      } else {
        count07 += Integer.parseInt(s[4]);
      }
    }
    StringBuilder rests = new StringBuilder();
    for (String[] rest : restsList) {
      String[] sorts = repository.getSorts(false);
      for (String sort : sorts) {
        if (rest[0].equals(sort)) {
          int countSymbols = rest[0].length() + rest[1].length();
          rests
              .append(rest[0])
              .append(" ".repeat(20 - countSymbols))
              .append(rest[1])
              .append(sort.equals(sorts[sorts.length - 1]) ? "" : "\n");
        }
      }
    }
    String total = String.valueOf(MathUtils.round2((count05 * 0.05f + count07 * 0.07f)));
    String stat =
        "Бутылок 0.5"
            + " ".repeat(20 - 11 - String.valueOf(count05).length())
            + count05
            + "\n"
            + "Бутылок 0.7"
            + " ".repeat(20 - 11 - String.valueOf(count07).length())
            + count07
            + "\n"
            + "Всего, дал"
            + " ".repeat(20 - 10 - total.length())
            + total;
    return new String[] {stat, rests.toString()};
  }

  public String[] getProduction() {
    String[] list = new String[days.size()];
    float dal;
    for (int i = 0; i < days.size(); i++) {
      dal = 0f;
      for (String[] data : repository.dataBase) {
        if (days.get(i).equals(data[0])) {
          if (data[2].equals("0.5")) {
            dal += Integer.parseInt(data[4]) * 0.05f;
          } else {
            dal += Integer.parseInt(data[4]) * 0.07f;
          }
        }
      }
      list[i] = roundStr(dal, 100f);
    }
    return list;
  }

  public String[] material(int start, int end) {
    String[] aList = new String[] {"0", "0", "0", "0", "0", "0", "0", "0", "0", "", "0", "0", ""};
    String v05 = " 0,5";
    String v07 = " 0,7";
    int c05 = 0;
    int c07 = 0;
    Map<String, Integer> labels = new LinkedHashMap<>();
    for (String s : repository.sorts.keySet()) {
      labels.put(s + v05, 0);
      labels.put(s + v07, 0);
    }
    for (String[] s : repository.dataBase) {
      if (Integer.parseInt(s[0]) >= start && Integer.parseInt(s[0]) <= end) {
        if (s[2].equals("0.5")) {
          int c = Integer.parseInt(s[4]);
          c05 += c;
          labels.put(s[1] + v05, labels.get(s[1] + v05) + c);
        } else {
          int c = Integer.parseInt(s[4]);
          c07 += c;
          labels.put(s[1] + v07, labels.get(s[1] + v07) + c);
        }
      }
    }

    aList[0] = toStrFloor(c05 * 1.025f); // alu
    aList[1] = toStrFloor(c07 * 1.015f); // пэт
    aList[2] = toStrFloor((c05 + c07) * 1.012f); // тук
    aList[3] = toStrFloor((c05 + c07) * 1.008f); // этикетки
    aList[4] = toStrFloor(c05 * 0.05f * 0.0081f + c07 * 0.07f * 0.0057f); // клей 5210
    aList[5] = toStrFloor(c05 * 0.05f * 0.004f + c07 * 0.07f * 0.003f); // клей 2400
    aList[6] = toStrFloor((c05 + c07) * 0.003f); // дегмос
    aList[7] = toStrFloor((c05 + c07) * 0.0007f); // тринатр
    aList[8] = toStrFloor((c05 * 0.05f + c07 * 0.07f) * 0.021f); // ФК
    aList[10] = toStrFloor(c05); // акц .5
    aList[11] = toStrFloor(c07); // акц .7

    String[] bList = new String[labels.size()];
    int i = 0;
    for (String s : labels.keySet()) {
      bList[i] = toStrFloor(labels.get(s) * 1.008f);
      ++i;
    }
    int aLen = aList.length;
    int bLen = bList.length;
    String[] dataList = new String[aLen + bLen];
    System.arraycopy(aList, 0, dataList, 0, aLen);
    System.arraycopy(bList, 0, dataList, aLen, bLen);
    return dataList;
  }

  public String newMonth() {
    AlcoholLogControl alcohol = new AlcoholLogControl(0);
    ArrayList<String[]> rest;
    ArrayList<String[]> counters = new ArrayList<>();
    ArrayList<String[]> stamps = new ArrayList<>();
    Integer[] existMonths = repository.existingMonths();
    int lastMonth = existMonths[existMonths.length - 1] + 1;
    String monthNumber;
    if (lastMonth > 12) {
      monthNumber = "01";
    } else {
      monthNumber = String.format(Locale.getDefault(), "%02d", lastMonth);
    }
    if (repository.fileExists("sorts" + monthNumber)) {
      return "00";
    }

    rest = getRest();

    for (String s : repository.counters.keySet()) {
      String[] _c = new String[4];
      boolean v05 = false;
      boolean v07 = false;
      for (int i = repository.dataBase.size() - 1; i > -1; i--) {
        if (repository.dataBase.get(i)[1].equals(s)) {
          if (repository.dataBase.get(i)[2].equals("0.5") && !v05) {
            alcohol.curIndex = i;
            alcohol.sort = repository.dataBase.get(i)[1];
            alcohol.tare = repository.dataBase.get(i)[2];
            alcohol.sumFlag = false;
            String[] data = alcohol.getEndCounters();
            _c[0] = data[0];
            _c[1] = data[1];
            v05 = true;
          } else if (repository.dataBase.get(i)[2].equals("0.7") && !v07) {
            alcohol.curIndex = i;
            alcohol.sort = repository.dataBase.get(i)[1];
            alcohol.tare = repository.dataBase.get(i)[2];
            alcohol.sumFlag = false;
            String[] data = alcohol.getEndCounters();
            _c[2] = data[0];
            _c[3] = data[1];
            v07 = true;
          }
        }
      }
      if (v05 && v07) {
        counters.add(new String[] {s, _c[0], _c[1], _c[2], _c[3]});
      } else if (v05) {
        counters.add(
            new String[] {
              s, _c[0], _c[1], repository.counters.get(s)[2], repository.counters.get(s)[3]
            });
      } else if (v07) {
        counters.add(
            new String[] {
              s, repository.counters.get(s)[0], repository.counters.get(s)[1], _c[2], _c[3]
            });
      } else {
        String[] notUsedSortCounters =
            new String[] {
              s,
              repository.counters.get(s)[0],
              repository.counters.get(s)[1],
              repository.counters.get(s)[2],
              repository.counters.get(s)[3]
            };
        if (repository.month.equals(repository.resetCounters[0].split(" ")[0])) {
          if (repository.resetCounters[1].equals("1")) {
            notUsedSortCounters[1] = "0";
          }
          if (repository.resetCounters[2].equals("1")) {
            notUsedSortCounters[3] = "0";
          }
          if (repository.resetCounters[3].equals("1")) {
            notUsedSortCounters[2] = "0";
          }
          if (repository.resetCounters[4].equals("1")) {
            notUsedSortCounters[4] = "0";
          }
        }
        counters.add(notUsedSortCounters);
      }
    }

    StampsControl control = new StampsControl();
    String day5 = null, day7 = null;
    int posInDay5 = -1, posInDay7 = -1;
    for (int i = repository.dataBase.size() - 1; i > -1; i--) {
      if (repository.dataBase.get(i)[2].equals("0.5")) {
        day5 = repository.dataBase.get(i)[0];
        for (int j = i; j > -1; j--) {
          if (j == 0) {
            posInDay5 = Integer.parseInt(day5);
            break;
          }
          if (!repository.dataBase.get(j)[0].equals(repository.dataBase.get(i)[0])) {
            posInDay5 = i - j - 1;
            break;
          }
        }
        break;
      }
    }
    for (int i = repository.dataBase.size() - 1; i > -1; i--) {
      if (repository.dataBase.get(i)[2].equals("0.7")) {
        day7 = repository.dataBase.get(i)[0];
        for (int j = i; j > -1; j--) {
          if (j == 0) {
            posInDay7 = Integer.parseInt(day7);
            break;
          }
          if (!repository.dataBase.get(j)[0].equals(repository.dataBase.get(i)[0])) {
            posInDay7 = i - j - 1;
            break;
          }
        }
        break;
      }
    }
    if (day5 != null & posInDay5 != -1) {
      List<String[]> last5 = control.getDayStamps(day5);
      String s = last5.get(posInDay5)[1];
      stamps.add(
          new String[] {
            "0",
            s.substring(1, 4)
                + s.substring(5, 10)
                + String.format(Locale.getDefault(), "%04d", Integer.parseInt(s.substring(10)) + 1)
          });
    }
    if (day7 != null & posInDay7 != -1) {
      List<String[]> last7 = control.getDayStamps(day7);
      String s = last7.get(posInDay7)[1];
      stamps.add(
          new String[] {
            "1",
            s.substring(1, 4)
                + s.substring(5, 10)
                + String.format(Locale.getDefault(), "%04d", Integer.parseInt(s.substring(10)) + 1)
          });
    }

    repository.sortsIcons.clear();
    repository.dataBase.clear();
    repository.sorts.clear();
    repository.counters.clear();
    repository.stamps05.clear();
    repository.stamps07.clear();
    days.clear();

    repository.writeFile("sorts" + monthNumber, rest);
    repository.writeFile("data" + monthNumber, repository.dataBase);
    repository.writeFile("counters" + monthNumber, counters);
    repository.writeFile("stamps" + monthNumber, stamps);
    return monthNumber;
  }

  public List<String> getDays() {
    return days;
  }

  public int toInt(String str, int start) {
    String s = str.substring(start).replaceAll("^0*", "");
    if (s.equals("")) {
      return 0;
    }
    return Integer.parseInt(s);
  }

  public int toInt(String str, int start, int end) {
    String s = str.substring(start, end).replaceAll("^0*", "");
    if (s.equals("")) {
      return 0;
    }
    return Integer.parseInt(s);
  }

  private String toStrFloor(float f) {
    return String.valueOf((int) Math.floor(f));
  }

  private void initDays() {
    days.clear();
    for (String[] data : repository.dataBase) {
      if (!days.contains(data[0])) {
        days.add(data[0]);
      }
    }
  }

  private ArrayList<String[]> getRest() {
    ArrayList<String[]> rest = new ArrayList<>();
    ReportLogControl rControl;
    for (String wine : repository.sorts.keySet()) {
      rControl = new ReportLogControl(wine);
      if (!rControl.indexList.isEmpty()) {
        rControl.setSnapIndex(rControl.indexList.size() - 1);
        String endRest = rControl.getDataList()[9].replace(",", ".");
        if (rControl.divBlendFlag) {
          endRest = endRest.split(" / ")[1].replace(",", ".");
        }
        rest.add(new String[] {wine, endRest});
      } else {
        rest.add(new String[] {wine, repository.sorts.get(wine).get(0)});
      }
    }
    return rest;
  }
}
