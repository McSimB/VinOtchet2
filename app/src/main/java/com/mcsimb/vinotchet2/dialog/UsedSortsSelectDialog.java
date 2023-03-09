package com.mcsimb.vinotchet2.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mcsimb.vinotchet2.R;

public class UsedSortsSelectDialog extends MaterialAlertDialogBuilder {

  public UsedSortsSelectDialog(
      @NonNull Context context,
      String[] items,
      boolean[] checkedItems,
      DialogInterface.OnClickListener listener) {
    super(context);
    setIcon(R.drawable.ic_settings);
    setTitle(context.getString(R.string.used_sorts));
    setMultiChoiceItems(
        items, checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked);
    setNegativeButton(R.string.cancel, (dialog, whichButton) -> dialog.dismiss());
    setPositiveButton(R.string.ok, listener);
    create();
  }
}
