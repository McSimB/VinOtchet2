package com.mcsimb.vinotchet2;

public class AlcoholLogControl extends LogControl {

  public boolean sumFlag;

  public AlcoholLogControl(int index) {
    curIndex = index;
    sort = repository.dataBase.get(curIndex)[1];
    tare = repository.dataBase.get(curIndex)[2];
  }

  public String[] getEndCounters() {
    String[] dataList = new String[11];
    if (!repository.dataBase.isEmpty()) {
      dataList = checkDataList(false);
      total = false;
    }
    return new String[] {dataList[2], dataList[8]};
  }

  @Override
  protected String[] getDataList() {
    String[] dataList = new String[11];
    if (!repository.dataBase.isEmpty()) {
      dataList = checkDataList(true);
      total = false;
    }
    return dataList;
  }

  @Override
  public void move(int dir) {
    curIndex = Math.max(Math.min(curIndex + dir, repository.dataBase.size() - 1), 0);
    sort = repository.dataBase.get(curIndex)[1];
    tare = repository.dataBase.get(curIndex)[2];
  }

  private String[] checkDataList(boolean isTotal) {
    String[] dList = new String[11];
    float[] in =
        income2(
            curIndex,
            toInt(repository.dataBase.get(curIndex)[3]),
            toInt(repository.dataBase.get(curIndex)[4]));
    String[] counters = getCounters(repository.dataBase.get(curIndex)[2], sort);
    dList[0] = noZero2(in[0]);
    String[] tot = getTotal();
    if (tare.equals("0.5")) {
      dList[1] =
          String.valueOf(
              Integer.parseInt(tot[0]) - Integer.parseInt(repository.dataBase.get(curIndex)[3]));
      dList[2] = (isTotal ? tot[0] : counters[1]);
    } else {
      dList[1] =
          String.valueOf(
              Integer.parseInt(tot[1]) - Integer.parseInt(repository.dataBase.get(curIndex)[3]));
      dList[2] = (isTotal ? tot[1] : counters[1]);
    }
    dList[3] = repository.dataBase.get(curIndex)[3];
    double dal1 = toDal(curIndex, toInt(dList[3]));
    dList[4] = noZero2(dal1);
    dList[5] = tare.equals("0.5") ? "0,17" : "0,14";
    dList[6] = noZero2((in[0] - dal1) / in[0] * 100);
    dList[7] =
        String.valueOf(
            Integer.parseInt(tot[2]) - Integer.parseInt(repository.dataBase.get(curIndex)[4]));
    dList[8] = (isTotal ? tot[2] : counters[3]);
    dList[9] = repository.dataBase.get(curIndex)[4];
    dList[10] = noZero2(toDal(curIndex, toInt(dList[9])));

    if (dList[0].equals("-") && dList[10].equals("-")) {
      sumFlag = true;
    }
    return dList;
  }

  private String[] getCounters(String tare, String sort) {
    int c1, c2;
    if (tare.equals("0.5")) {
      c1 = toInt(repository.counters.get(sort)[0]);
      c2 = toInt(repository.counters.get(sort)[1]);
      if ((repository.month.equals(repository.resetCounters[0].split(" ")[0]))
          && (curIndex >= toInt(repository.resetCounters[0].split(" ")[1]))) {
        if (repository.resetCounters[1].equals("1")) {
          c1 = 0;
        }
        if (repository.resetCounters[3].equals("1")) {
          c2 = 0;
        }
      }
    } else {
      c1 = toInt(repository.counters.get(sort)[2]);
      c2 = toInt(repository.counters.get(sort)[3]);
      if ((repository.month.equals(repository.resetCounters[0].split(" ")[0]))
          && (curIndex >= toInt(repository.resetCounters[0].split(" ")[1]))) {
        if (repository.resetCounters[2].equals("1")) {
          c1 = 0;
        }
        if (repository.resetCounters[4].equals("1")) {
          c2 = 0;
        }
      }
    }
    if (c1 != 0) {
      for (int i = 0; i < curIndex; i++) {
        if (repository.dataBase.get(i)[1].equals(sort)
            && repository.dataBase.get(i)[2].equals(tare)) {
          c1 += toInt(repository.dataBase.get(i)[3]);
        }
      }
    }
    if (c2 != 0) {
      for (int i = 0; i < curIndex; i++) {
        if (repository.dataBase.get(i)[1].equals(sort)
            && repository.dataBase.get(i)[2].equals(tare)) {
          c2 += toInt(repository.dataBase.get(i)[4]);
        }
      }
    }
    if (c1 == 0) {
      for (int i = toInt(repository.resetCounters[0].split(" ")[1]); i < curIndex; i++) {
        if (repository.dataBase.get(i)[1].equals(sort)
            && repository.dataBase.get(i)[2].equals(tare)) {
          c1 += toInt(repository.dataBase.get(i)[3]);
        }
      }
    }
    if (c2 == 0) {
      for (int i = toInt(repository.resetCounters[0].split(" ")[1]); i < curIndex; i++) {
        if (repository.dataBase.get(i)[1].equals(sort)
            && repository.dataBase.get(i)[2].equals(tare)) {
          c2 += toInt(repository.dataBase.get(i)[4]);
        }
      }
    }
    return new String[] {
      toStr(c1),
      toStr(c1 + toInt(repository.dataBase.get(curIndex)[3])),
      toStr(c2),
      toStr(c2 + toInt(repository.dataBase.get(curIndex)[4]))
    };
  }

  private String[] getTotal() {
    int t5 = 0, t7 = 0, t = 0;
    for (String w : repository.sorts.keySet()) {
      for (String v : new String[] {"0.5", "0.7"}) {
        String[] c = getCounters(v, w);
        t += Integer.parseInt(c[2]);
        if (v.equals("0.5")) {
          t5 += Integer.parseInt(c[0]);
        } else {
          t7 += Integer.parseInt(c[0]);
        }
      }
    }
    t5 += toInt(repository.dataBase.get(curIndex)[3]);
    t7 += toInt(repository.dataBase.get(curIndex)[3]);
    t += toInt(repository.dataBase.get(curIndex)[4]);
    return new String[] {toStr(t5), toStr(t7), toStr(t)};
  }
}
