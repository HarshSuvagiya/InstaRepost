package com.jarvis.instarepost.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.jarvis.instarepost.R;
import java.util.Objects;

public class DownloadProgressDialog extends Dialog {
    private ProgressBar progressBar;
    private TextView txtMessage;

    public DownloadProgressDialog(Context context) {
        super(context);
        ((Window) Objects.requireNonNull(getWindow())).setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_download_progress);
        progressBar = (findViewById(R.id.progressBar));
        txtMessage = (findViewById(R.id.txtMessage));
        setCancelable(false);
    }

    public DownloadProgressDialog setMessage(String str) {
        this.txtMessage.setText(str);
        return this;
    }

    public DownloadProgressDialog setProgress(int i) {
        this.progressBar.setProgress(i);
        return this;
    }
}
