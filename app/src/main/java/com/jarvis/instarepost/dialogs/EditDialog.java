package com.jarvis.instarepost.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.jarvis.instarepost.R;

public class EditDialog extends Dialog {
    public EditText edtValue;
    public OnActionListener onActionListener;
    private TextView txtCancel;
    private TextView txtConfirm;
    private TextView txtMessage;

    public interface OnActionListener {
        void onCancel();
        void onConfirm(String str);
    }

    public EditDialog(Context context) {
        super(context);
        init();
    }

    private void init() {
        requestWindowFeature(1);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_edit);
        setCancelable(false);
        this.txtMessage = (TextView) findViewById(R.id.txtMessage);
        this.txtConfirm = (TextView) findViewById(R.id.txtConfirm);
        this.txtCancel = (TextView) findViewById(R.id.txtCancel);
        this.edtValue = (EditText) findViewById(R.id.edtValue);
        this.txtConfirm.setVisibility(8);
        this.txtCancel.setVisibility(8);
        this.edtValue.setVisibility(8);
        this.txtConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (EditDialog.this.onActionListener != null) {
                    EditDialog.this.onActionListener.onConfirm(EditDialog.this.edtValue.getText().toString());
                }
                EditDialog.this.dismiss();
            }
        });
        this.txtCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (EditDialog.this.onActionListener != null) {
                    EditDialog.this.onActionListener.onCancel();
                }
                EditDialog.this.dismiss();
            }
        });
    }

    public EditDialog setCancelText(String str) {
        this.txtCancel.setText(str);
        this.txtCancel.setVisibility(0);
        return this;
    }

    public EditDialog setConfirmText(String str) {
        this.txtConfirm.setText(str);
        this.txtConfirm.setVisibility(0);
        return this;
    }

    public EditDialog setEditText(String str) {
        this.edtValue.setText(str);
        this.edtValue.setVisibility(0);
        return this;
    }

    public EditDialog setMessage(String str) {
        this.txtMessage.setText(str);
        return this;
    }

    public EditDialog setOnActionListener(OnActionListener onActionListener2) {
        this.onActionListener = onActionListener2;
        return this;
    }
}
