package com.mcsimb.vinotchet2.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mcsimb.vinotchet2.R;

public class MonthSelectDialog extends MaterialAlertDialogBuilder {

  public MonthSelectDialog(
      @NonNull Context context, String[] items, DialogInterface.OnClickListener listener) {
    super(context);
    setIcon(R.drawable.ic_calendar_month);
    setTitle(context.getString(R.string.month_select_title));
    setItems(items, listener);
    setPositiveButton(R.string.cancel, (dialog, whichButton) -> dialog.dismiss());
    create();
    show();
  }
}
