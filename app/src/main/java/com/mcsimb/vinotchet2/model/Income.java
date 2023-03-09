package com.mcsimb.vinotchet2.model;

import com.mcsimb.vinotchet2.util.MathUtils;

public class Income {

  private final Product product;
  private float lab;
  private float incomeDal;

  public Income(Product product) {
    this.product = product;
    init();
  }

  public float getLab() {
    return lab;
  }

  public float getDal() {
    return incomeDal;
  }

  public void init() {
    lab = product.getTare().getDal();

    if (!product.isValidCounters()) {
      lab = 0f;
      incomeDal = product.getDal2();
    }

    float div = product.getTare().getDivPercent();
    float dal1 = product.getDal1();
    float dal2 = product.getDal2();
    incomeDal = MathUtils.floor1(dal1 * 100.0f / (100.0f - div));
    incomeDal = Math.max(incomeDal, dal1);

    // Разница между фактическими и нормативными потерями по цеху розлива
    float diff = (incomeDal - dal2 - lab) - (dal2 * Production.BOTTLING_LOSS / 100.0f);
    if (diff > 0) {
      if (incomeDal - dal1 > diff) {
        incomeDal = Math.max(MathUtils.round1(incomeDal - diff), product.getDal1());
      }
    }

    lab = incomeDal - dal1 >= product.getTare().getDal() ? product.getTare().getDal() : 0f;
  }
}
