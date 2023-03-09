package com.mcsimb.vinotchet2.model;

public class Tare {

  /* Объем тары для розлива продукции, дал */
  private final float dal;
  /* Процент расхождения продукции между первым счетчиком и кол-вом, что поступило по
  накладной */
  private final float divergencyPercent;

  public Tare(float dal, float divergencyPercent) {
    this.dal = dal;
    //this.divergencyPercent = divergencyPercent;

    if (dal == 0.05f) {
      this.divergencyPercent = 0.17f;
    } else {
      this.divergencyPercent = 0.14f;
    }
  }

  public float getDal() {
    return dal;
  }

  public float getDivPercent() {
    return divergencyPercent;
  }
}
