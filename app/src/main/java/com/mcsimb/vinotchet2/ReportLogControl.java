package com.mcsimb.vinotchet2;

public class ReportLogControl extends InfoLogControl {

  public ReportLogControl(String wine) {
    super(wine);
  }

  ReportLogControl(int curIndex) {
    super(curIndex);
  }

  @Override
  public String[] getDataList() {
    String[] dataList = new String[10];
    if (!indexList.isEmpty()) {
      double[] rest = isDivBlend();
      int count1 = toInt(repository.dataBase.get(curIndex)[3]);
      int count2 = toInt(repository.dataBase.get(curIndex)[4]);
      if (!divBlendFlag) {
        float in = income2(curIndex, count1, count2)[0];
        double loss042 = round(debit(curIndex, count1, count2)[0] - in, 100.0);
        double loss033 = round(loss042 * 0.33 / 0.42, 100.0);
        dataList[8] = noZero2(in); // Поступило
        dataList[1] = noZero1(rest[1] + rest[2]); // Остаток нач.
        dataList[9] = noZero1(rest[1]); // Остаток кон.
        dataList[2] = "-"; // Брак
        dataList[4] = "-"; // Лабор-ия
        dataList[7] = noZero2(loss042); // Потери 0.42%
        dataList[6] = noZero2(loss033); // Потери 0.33%
        dataList[5] = noZero2(loss042 - loss033); // Потери 0.09%
      } else {
        int[] c = blendDivision(rest[0], curIndex);
        double[] in = new double[2];
        double[] rew = new double[2];
        double rest0 = toDouble(repository.sorts.get(repository.dataBase.get(curIndex)[1]).get(blend));
        for (int i = 0; i < 2; i++) {
          newBlendFlag = i;
          in[i] = income2(curIndex, c[i * 2], c[i * 2 + 1])[0];
          rew[i] = income2(curIndex, c[i * 2], c[i * 2 + 1])[3];
        }
        newBlendFlag = 0;
        dataList[8] = noZero2(in[0]) + " / " + noZero2(in[1]);
        dataList[1] = noZero1(rest[0]) + " / " + noZero1(rest0);
        double rew1 = rew[0];
        double rew2 = rew[1];
        dataList[2] = "- / -";
        dataList[4] = "- / -";
        double loss0421 = round(rest[0] + rew1 - in[0], 100.0);
        double loss0422;
        if (c[2] > 0) {
          loss0422 = round(rest0 + rew2 - in[1] - rest[1], 100.0);
        } else {
          loss0422 = 0;
        }
        double loss0331 = round(loss0421 * 0.33 / 0.42, 100.0);
        double loss0332 = round(loss0422 * 0.33 / 0.42, 100.0);
        dataList[7] = noZero2(loss0421) + " / " + noZero2(loss0422);
        dataList[6] = noZero2(loss0331) + " / " + noZero2(loss0332);
        dataList[5] = noZero2(loss0421 - loss0331) + " / " + noZero2(loss0422 - loss0332);

        dataList[9] = "- / " + noZero1(rest[1]);
      }
      dataList[0] = repository.dataBase.get(curIndex)[0] + "." + repository.month; // Дата
    }
    return dataList;
  }
}
