package com.mcsimb.vinotchet2.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mcsimb.vinotchet2.R;

public class ProductRemoveDialog extends MaterialAlertDialogBuilder {

  public ProductRemoveDialog(
      @NonNull Context context, String message, DialogInterface.OnClickListener listener) {
    super(context);
    setIcon(R.drawable.ic_delete);
    setTitle(context.getString(R.string.remove));
    setMessage(context.getString(R.string.remove_entry, message));
    setNegativeButton(R.string.cancel, (dialog, whichButton) -> dialog.dismiss());
    setPositiveButton(R.string.ok, listener);
    create();
    show();
  }
}
