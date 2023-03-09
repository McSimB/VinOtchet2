package com.mcsimb.vinotchet2;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcsimb.vinotchet2.common.Item;

public class ProductViewItem implements Item {

  public final long date;
  public final String sort;
  public final String counter1;
  public final String counter2;
  public final String stamps1;
  public final String stamps2;
  public final Bitmap label;

  public ProductViewItem(
      long date,
      String sort,
      String counter1,
      String counter2,
      String stamps1,
      String stamps2,
      Bitmap label) {
    this.date = date;
    this.sort = sort;
    this.counter1 = counter1;
    this.counter2 = counter2;
    this.stamps1 = stamps1;
    this.stamps2 = stamps2;
    this.label = label;
  }

  @Override
  public long getId() {
    return 0;
  }

  @Override
  public void bindView(View view) {
    TextView sortItem = view.findViewById(R.id.text_main_sort);
    TextView countItem1 = view.findViewById(R.id.text_main_counter1);
    TextView countItem2 = view.findViewById(R.id.text_main_counter2);
    TextView stampsItem1 = view.findViewById(R.id.text_main_stamps1);
    TextView stampsItem2 = view.findViewById(R.id.text_main_stamps2);
    ImageView imageView = view.findViewById(R.id.ic_main);
    if (sort.endsWith("0.5")) {
      view.setBackgroundColor(
          view.getContext().getResources().getColor(android.R.color.transparent, null));
      sortItem.setTypeface(null, Typeface.NORMAL);
    }
    sortItem.setText(sort.replace(".", ","));
    countItem1.setText(counter1);
    countItem2.setText(counter2);
    stampsItem1.setText(stamps1);
    stampsItem2.setText(stamps2);
    imageView.setImageBitmap(label);
  }
}
