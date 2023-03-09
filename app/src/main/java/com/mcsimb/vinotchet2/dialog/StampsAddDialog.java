package com.mcsimb.vinotchet2.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mcsimb.vinotchet2.R;

public class StampsAddDialog extends MaterialAlertDialogBuilder {

  public StampsAddDialog(
      @NonNull Context context, View view, DialogInterface.OnClickListener listener) {
    super(context);
    setIcon(R.drawable.ic_qrcode);
    setTitle(context.getString(R.string.add_stamps_title));
    setView(view);
    setCancelable(false);
    setPositiveButton(R.string.ok, listener);
    create();
    show();
  }
}
