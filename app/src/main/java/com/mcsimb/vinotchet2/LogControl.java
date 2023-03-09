package com.mcsimb.vinotchet2;

import android.annotation.SuppressLint;
import java.util.ArrayList;

@SuppressLint("DefaultLocale")
public abstract class LogControl {

  public final ArrayList<Integer> indexList = new ArrayList<>();
  final Repository repository = Repository.getInstance();

  int count05;
  int count07;
  int snapIndex;
  int curBlend;
  int blend = 0;
  public int curIndex = 0;
  int newBlendFlag = 0;
  public boolean divBlendFlag = false;
  public String sort;
  public String tare;
  String[] days;
  int curDay = 0;
  boolean total;

  protected abstract String[] getDataList();

  protected void export() {}

  void initSnap(String sort) {
    boolean ini = false;
    indexList.clear();
    for (String[] s : repository.dataBase) {
      if (s[1].equals(sort)) {
        indexList.add(repository.dataBase.indexOf(s));
        if (curIndex == repository.dataBase.indexOf(s)) {
          snapIndex = indexList.indexOf(curIndex);
          ini = true;
        }
      }
    }
    if (!indexList.isEmpty() && !ini) {
      setSnapIndex(0);
    }
  }

  public void setSnapIndex(int index) {
    snapIndex = index;
    curIndex = indexList.get(snapIndex);
  }

  void move(int direct) {
    curIndex = Math.max(Math.min(curIndex + direct, repository.dataBase.size() - 1), 0);
    sort = repository.dataBase.get(curIndex)[1];
    tare = repository.dataBase.get(curIndex)[2];
  }

  boolean btnPrevVisibility() {
    return curIndex != 0;
  }

  boolean btnNextVisibility() {
    return curIndex != repository.dataBase.size() - 1 && !repository.dataBase.isEmpty();
  }

  double toDal(int ind, int count) {
    double dal;
    if (repository.dataBase.get(ind)[2].equals("0.5")) {
      dal = count * 0.05;
    } else {
      dal = count * 0.07;
    }
    return dal;
  }

  float toDal2(int ind, int count) {
    float dal;
    if (repository.dataBase.get(ind)[2].equals("0.5")) {
      dal = count * 0.05f;
    } else {
      dal = count * 0.07f;
    }
    return dal;
  }

  // Поступило, лаборатория, потери 0.34%, испр.брак
  float[] income2(int ind, int count1, int count2) {
    float dal1 = toDal2(ind, count1);
    float dal2 = toDal2(ind, count2);
    float in, lab, loss034;
    float rew = 0;

    if (repository.dataBase.get(ind)[2].equals("0.5")) {
      in = (float) Math.floor(dal1 * 1.0017f * 10f) / 10f;
      lab = 0.05f;
    } else {
      in = (float) Math.floor(dal1 * 1.0014f * 10f) / 10f;
      lab = 0.07f;
    }

    if (in < dal1) {
      in = dal1;
      lab = 0f;
    }

    if (in - dal1 < lab) {
      lab = 0f;
    }

    loss034 = in - dal2 - lab;
    if (loss034 > dal2 * 0.0034f) {
      loss034 = (int) (dal2 * 0.34f) / 100f;
      rew = in - dal2 - lab - loss034;
    }

    return new float[] {in, lab, loss034, rew};
  }

  // Потери
  private double wineLoss076(double dal) {
    return round(dal * 0.0065, 100.0);
  }

  // Расход:на розлив + потери 0.76
  // и потери 0.42%
  double[] debit(int ind, int count1, int count2) {
    double deb;
    double r100;
    double loss042;
    double dal2 = toDal(ind, count2);
    // double rew = rework(ind, count1, count2);
    float rew = income2(ind, count1, count2)[3];
    float lab = income2(ind, count1, count2)[1];
    float loss034 = income2(ind, count1, count2)[2];
    deb = dal2 + loss034 + (int) (dal2 * 0.042) / 10.0 + lab + rew;
    r100 = deb - (int) (deb * 10) / 10.0; // Сотая часть дроби deb
    double roundFactor = 0.05;
    if (r100 > roundFactor) {
      deb = deb - r100 + 0.1; // Отброшена сотая часть дроби deb
      loss042 = wineLoss076(dal2) - loss034 - r100 + 0.1;
    } else {
      deb = deb - r100;
      loss042 = wineLoss076(dal2) - loss034 - r100;
    }
    return new double[] {deb, loss042};
  }

  // Имеется ли разделение купажей. Возвращает остаток
  // закрываемого купажа, остаток кон., расход
  public double[] isDivBlend() {
    blend = 0;
    double deb = 0.0;
    double divRest = 0.0;
    double rest = toDouble(repository.sorts.get(repository.dataBase.get(curIndex)[1]).get(blend));
    for (int i = 0; i < snapIndex + 1; i++) {
      String[] d = repository.dataBase.get(indexList.get(i));
      divRest = 0.0;
      deb = debit(indexList.get(i), toInt(d[3]), toInt(d[4]))[0];
      rest = rest - deb;
      divBlendFlag = false;
      if (rest < 1) {
        divBlendFlag = true;
        divRest = rest + deb;
        try {
          String b = repository.sorts.get(repository.dataBase.get(curIndex)[1]).get(++blend);
          if (rest > -1) {
            rest = 0;
          }
          rest += toDouble(b);

          int[] c = blendDivision(divRest, indexList.get(i));
          double[] in = new double[2];
          double[] rew = new double[2];
          double rest0 =
              toDouble(repository.sorts.get(repository.dataBase.get(curIndex)[1]).get(blend));
          for (int j = 0; j < 2; j++) {
            newBlendFlag = j;
            in[j] = income2(indexList.get(i), c[j * 2], c[j * 2 + 1])[0];
            rew[j] = income2(indexList.get(i), c[j * 2], c[j * 2 + 1])[3];
          }
          newBlendFlag = 0;
          double rew2 = rew[1];
          double loss0422;
          if (c[2] > 0) {
            loss0422 = rest0 + rew2 - in[1] - rest;
            if (loss0422 < 0) {
              rest -= 0.1;
              deb += 0.1;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return new double[] {divRest, rest, deb};
  }

  int[] blendDivision(double rest, int ind) {
    int count1_1;
    int count2_1;
    int count1_2;
    int count2_2;
    double dal1 = (rest - 0.05) / 1.0074;

    String[] d = repository.dataBase.get(ind);
    if (d[2].equals("0.5")) {
      count2_1 = (int) (dal1 / 0.05);
    } else {
      count2_1 = (int) (dal1 / 0.07);
    }
    if (toInt(d[4]) - count2_1 < 15) {
      count2_1 = toInt(d[4]);
    }

    count1_1 = (int) (count2_1 * toInt(d[3]) / toDouble(d[4]));
    count1_2 = toInt(d[3]) - count1_1;
    count2_2 = toInt(d[4]) - count2_1;

    return new int[] {count1_1, count2_1, count1_2, count2_2};
  }

  double round(double value, double precis) {
    return Math.round(value * precis) / precis;
  }

  double toDouble(String s) {
    return Double.parseDouble(s);
  }

  double toDouble2(String s) {
    double d;
    if (s.equals("-")) {
      d = 0;
    } else {
      d = toDouble(s.replace(",", "."));
    }
    return d;
  }

  int toInt(String i) {
    return Integer.parseInt(i);
  }

  String toStr(Object s) {
    return String.valueOf(s);
  }

  String noZero1(double d) {
    String s;
    double _d = round(d, 10.0);
    if (d == 0) {
      s = "-";
    } else {
      if (_d - Math.round(d) == 0) {
        s = String.format("%.0f", d);
      } else {
        s = String.format("%.1f", _d);
      }
    }
    return s;
  }

  String noZero2(double d) {
    String s;
    double _d = round(d, 100.0);
    if (d == 0) {
      s = "-";
    } else {
      if (_d - Math.round(d) == 0) {
        s = String.format("%.0f", d);
      } else if (_d - round(d, 10.0) == 0) {
        s = String.format("%.1f", d);
      } else {
        s = String.format("%.2f", _d);
      }
    }
    return s;
  }
}
