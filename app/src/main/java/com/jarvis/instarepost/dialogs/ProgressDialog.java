package com.jarvis.instarepost.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;
import com.jarvis.instarepost.R;
import java.util.Objects;

public class  ProgressDialog extends Dialog {
    private TextView txtMessage = ((TextView) findViewById(R.id.txtMessage));

    public ProgressDialog(Context context) {
        super(context);
        ((Window) Objects.requireNonNull(getWindow())).setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_progress);
        setCancelable(false);
    }

    public ProgressDialog setMessage(String str) {
        this.txtMessage.setText(str);
        return this;
    }
}
