package com.mcsimb.vinotchet2.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mcsimb.vinotchet2.R;

public class LogSelectDialog extends MaterialAlertDialogBuilder {

  public LogSelectDialog(@NonNull Context context, DialogInterface.OnClickListener listener) {
    super(context);
    setIcon(R.drawable.ic_form_select);
    setTitle(context.getString(R.string.log_select_title));
    setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
    setItems(context.getResources().getStringArray(R.array.logs_titles), listener);
    create();
    show();
  }
}
