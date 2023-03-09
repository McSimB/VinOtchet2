package com.mcsimb.vinotchet2.model;

import com.mcsimb.vinotchet2.util.MathUtils;

public class Filtering {

  private final Income income;
  private float loss = Float.NaN;

  public Filtering(Product product) {
    this.income = new Income(product);
  }

  public Income getIncome() {
    return income;
  }

  public float getLoss() {
    if (Float.isNaN(loss)) {
      return MathUtils.floor1(income.getDal() * Production.FILTERING_LOSS / 100f);
    } else {
      return MathUtils.round1(loss);
    }
  }

  public void setLoss(float loss) {
    this.loss = loss;
  }

  public float getIncomeDal() {
    return income.getDal();
  }

  public float getExpense() {
    return MathUtils.round1(income.getDal() + getLoss());
  }

  public void recalculateIncome() {
    income.init();
  }
}
