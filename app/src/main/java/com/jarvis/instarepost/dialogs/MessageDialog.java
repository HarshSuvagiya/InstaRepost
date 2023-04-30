package com.jarvis.instarepost.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.jarvis.instarepost.R;

public class MessageDialog extends Dialog {
    public OnActionListener onActionListener;
    private TextView txtCancel;
    private TextView txtConfirm;
    private TextView txtMessage;
    private TextView txtTitle;

    public interface OnActionListener {
        void onCancel();

        void onConfirm();
    }

    public MessageDialog(Context context) {
        super(context);
        init();
    }

    private void init() {
        requestWindowFeature(1);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_message);
        setCancelable(false);
        this.txtTitle = (TextView) findViewById(R.id.txtTitle);
        this.txtMessage = (TextView) findViewById(R.id.txtMessage);
        this.txtConfirm = (TextView) findViewById(R.id.txtConfirm);
        this.txtCancel = (TextView) findViewById(R.id.txtCancel);
        this.txtConfirm.setVisibility(8);
        this.txtCancel.setVisibility(8);
        this.txtConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (MessageDialog.this.onActionListener != null) {
                    MessageDialog.this.onActionListener.onConfirm();
                }
                MessageDialog.this.dismiss();
            }
        });
        this.txtCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (MessageDialog.this.onActionListener != null) {
                    MessageDialog.this.onActionListener.onCancel();
                }
                MessageDialog.this.dismiss();
            }
        });
    }

    public MessageDialog setCancelText(String str) {
        this.txtCancel.setText(str);
        this.txtCancel.setVisibility(0);
        return this;
    }

    public MessageDialog setConfirmText(String str) {
        this.txtConfirm.setText(str);
        this.txtConfirm.setVisibility(0);
        return this;
    }

    public MessageDialog setMessage(String str) {
        this.txtMessage.setText(str);
        return this;
    }

    public MessageDialog setOnActionListener(OnActionListener onActionListener2) {
        this.onActionListener = onActionListener2;
        return this;
    }

    public MessageDialog setTitle(String str) {
        this.txtTitle.setText(str);
        return this;
    }
}
