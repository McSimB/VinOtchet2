package com.mcsimb.vinotchet2;

public class JobLogControl extends AlcoholLogControl {

  private final boolean forDayOnly;

  public JobLogControl(int index, boolean forDayOnly) {
    super(index);
    this.forDayOnly = forDayOnly;
    if (forDayOnly) {
      initSnap("");
    }
  }

  @Override
  void initSnap(String sort) {
    indexList.clear();
    for (String[] d : repository.dataBase) {
      if (d[0].equals(repository.dataBase.get(curIndex)[0])) {
        indexList.add(repository.dataBase.indexOf(d));
      }
    }
  }

  @Override
  public boolean btnPrevVisibility() {
    if (forDayOnly) {
      return snapIndex != 0 && !indexList.isEmpty();
    } else {
      return super.btnPrevVisibility();
    }
  }

  @Override
  public boolean btnNextVisibility() {
    if (forDayOnly) {
      return snapIndex != indexList.size() - 1 && !indexList.isEmpty();
    } else {
      return super.btnNextVisibility();
    }
  }

  @Override
  protected String[] getDataList() {
    String[] dataList = new String[10];
    if (!repository.dataBase.isEmpty()) {
      double dal1 = toDal(curIndex, toInt(repository.dataBase.get(curIndex)[4]));
      float[] in2 =
          income2(
              curIndex,
              toInt(repository.dataBase.get(curIndex)[3]),
              toInt(repository.dataBase.get(curIndex)[4]));

      dataList[0] = "-";
      dataList[1] = noZero2(in2[0]);
      dataList[2] = noZero2(toDouble(repository.dataBase.get(curIndex)[2]));
      dataList[3] = repository.dataBase.get(curIndex)[4];
      dataList[4] = noZero2(dal1);
      dataList[5] = in2[3] == 0 ? noZero2(in2[1]) : noZero2(in2[3]) + " / " + noZero2(in2[1]);
      dataList[6] = noZero2(in2[2]);
      dataList[7] = noZero2(Math.floor(in2[2] * 10000.0 / dal1) / 100.0);
      dataList[8] = "0,34";
      dataList[9] = "-";
    }
    return dataList;
  }

  @Override
  public void move(int dir) {
    if (forDayOnly) {
      setSnapIndex(Math.max(Math.min(snapIndex + dir, indexList.size() - 1), 0));
      sort = repository.dataBase.get(curIndex)[1];
      tare = repository.dataBase.get(curIndex)[2];
    } else {
      super.move(dir);
    }
  }
}
