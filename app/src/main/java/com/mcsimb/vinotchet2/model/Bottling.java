package com.mcsimb.vinotchet2.model;

import com.mcsimb.vinotchet2.util.MathUtils;

public class Bottling {

  private final Product product;

  public Bottling(Product product) {
    this.product = product;
  }

  public float getLab() {
    return product.getFiltering().getIncome().getLab();
  }

  public float getLoss() {
    return MathUtils.round2(
        product.getFiltering().getIncome().getDal() - product.getDal2() - getLab());
  }
}
