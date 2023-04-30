package com.jarvis.instarepost.adapters;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.jarvis.instarepost.R;
import com.jarvis.instarepost.classes.App;
import com.jarvis.instarepost.classes.ClipBoardService;
import com.jarvis.instarepost.classes.NotificationHelper;
import com.jarvis.instarepost.dialogs.EditDialog;
import com.jarvis.instarepost.dialogs.EditDialog.OnActionListener;
import com.securepreferences.SecurePreferences;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class SettingsAdapter extends Adapter<ViewHolder> {
    private static final int TYPE_ADVANCED = 1;
    private static final int TYPE_GENERAL = 0;
    public Context context;
    public Editor editor;
    public SecurePreferences prefs;

    class VHAdvanced extends ViewHolder {
        LinearLayout E;
        LinearLayout F;
        SwitchCompat G;
        SwitchCompat H;

        VHAdvanced(View view) {
            super(view);
            this.E = (LinearLayout) view.findViewById(R.id.llNotificationBar);
            this.F = (LinearLayout) view.findViewById(R.id.llOpenInstagram);
            this.G = (SwitchCompat) view.findViewById(R.id.switchNotificationBar);
            this.H = (SwitchCompat) view.findViewById(R.id.switchOpenInstagram);
        }
    }

    class VHGeneral extends ViewHolder {
        LinearLayout E;
        LinearLayout F;
        TextView G;
        TextView H;

        VHGeneral(View view) {
            super(view);
            this.E = (LinearLayout) view.findViewById(R.id.llStorageFolder);
            this.F = (LinearLayout) view.findViewById(R.id.llLanguage);
            this.G = (TextView) view.findViewById(R.id.txtStorageFolder);
            this.H = (TextView) view.findViewById(R.id.txtLanguage);
        }
    }

    public SettingsAdapter(Context context2) {
        this.context = context2;
        this.prefs = new SecurePreferences(context2);
        editor = this.prefs.edit();
        this.editor.apply();
    }

    public boolean isServiceRunning(Class<?> cls, Context context2) {
        for (RunningServiceInfo runningServiceInfo : ((ActivityManager) Objects.requireNonNull((ActivityManager) context2.getSystemService("activity"))).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public int getItemCount() {
        return 2;
    }

    public int getItemViewType(int i) {
        return i == 0 ? 0 : 1;
    }

    public void onBindViewHolder(@NotNull final ViewHolder viewHolder, int i) {
        if (viewHolder instanceof VHGeneral) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.context.getString(R.string.internal_storage));
            sb.append(this.prefs.getString("app_folder_path", context.getResources().getString(R.string.app_name)));
            VHGeneral vHGeneral = (VHGeneral) viewHolder;
            vHGeneral.G.setText(sb.toString());
            vHGeneral.H.setText(this.prefs.getString("language", "English"));
            vHGeneral.E.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    new EditDialog(App.getRunningActivity()).setMessage(App.getRunningActivity().getString(R.string.storage_folder)).setEditText(SettingsAdapter.this.prefs.getString("app_folder_path", "InstaSaver")).setConfirmText(App.getRunningActivity().getString(R.string.save)).setCancelText(App.getRunningActivity().getString(R.string.cancel)).setOnActionListener(new OnActionListener() {
                        public void onCancel() {
                        }

                        public void onConfirm(String str) {
                            SettingsAdapter.this.editor.putString("app_folder_path", str).apply();
                            StringBuilder sb = new StringBuilder();
                            sb.append(SettingsAdapter.this.context.getString(R.string.internal_storage));
                            sb.append(str);
                            ((VHGeneral) viewHolder).G.setText(sb.toString());
                        }
                    }).show();
                }
            });
            vHGeneral.F.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    final String[] strArr = {"English", "Persian", "Finnish", "Turkish", "Arabic", "Russian"};
                    Builder builder = new Builder(SettingsAdapter.this.context);
                    builder.setTitle(App.getRunningActivity().getString(R.string.select_your_language));
                    builder.setItems(strArr, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SettingsAdapter.this.editor.putString("language", strArr[i]).apply();
                            String str = "en";
                            if (i != 0) {
                                if (i == 1) {
                                    str = "fa";
                                } else if (i == 2) {
                                    str = "fi";
                                } else if (i == 3) {
                                    str = "tr";
                                } else if (i == 4) {
                                    str = "ar";
                                } else if (i == 5) {
                                    str = "ru";
                                }
                            }
                            Resources resources = SettingsAdapter.this.context.getResources();
                            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                            Configuration configuration = resources.getConfiguration();
                            configuration.setLocale(new Locale(str.toLowerCase()));
                            resources.updateConfiguration(configuration, displayMetrics);
                            Intent intent = App.getRunningActivity().getIntent();
                            App.getRunningActivity().finish();
                            App.getRunningActivity().startActivity(intent);
                        }
                    });
                    builder.create().show();
                }
            });
        } else if (viewHolder instanceof VHAdvanced) {
            if (this.prefs.getBoolean("notification_bar", false)) {
                ((VHAdvanced) viewHolder).G.setChecked(true);
            } else {
                ((VHAdvanced) viewHolder).G.setChecked(false);
            }
            if (this.prefs.getBoolean("open_instagram", true)) {
                ((VHAdvanced) viewHolder).H.setChecked(true);
            } else {
                ((VHAdvanced) viewHolder).H.setChecked(false);
            }
            VHAdvanced vHAdvanced = (VHAdvanced) viewHolder;
            vHAdvanced.E.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    String str = "notification_bar";
                    if (SettingsAdapter.this.prefs.getBoolean(str, false)) {
                        ((VHAdvanced) viewHolder).G.setChecked(false);
                        SettingsAdapter.this.editor.putBoolean(str, false).apply();
                        ((NotificationManager) Objects.requireNonNull((NotificationManager) SettingsAdapter.this.context.getSystemService("notification"))).cancel(0);
                        return;
                    }
                    ((VHAdvanced) viewHolder).G.setChecked(true);
                    SettingsAdapter.this.editor.putBoolean(str, true).apply();
                    if (SettingsAdapter.this.prefs.getBoolean("auto_download", true)) {
                        SettingsAdapter settingsAdapter = SettingsAdapter.this;
                        if (settingsAdapter.isServiceRunning(ClipBoardService.class, settingsAdapter.context)) {
                            new NotificationHelper(SettingsAdapter.this.context).createNotification(App.getRunningActivity().getString(R.string.auto_download), App.getRunningActivity().getString(R.string.auto_download_enabled));
                        }
                    }
                }
            });
            vHAdvanced.G.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    SettingsAdapter.this.editor.putBoolean("notification_bar", z).apply();
                    if (SettingsAdapter.this.prefs.getBoolean("auto_download", true)) {
                        SettingsAdapter settingsAdapter = SettingsAdapter.this;
                        if (settingsAdapter.isServiceRunning(ClipBoardService.class, settingsAdapter.context)) {
                            new NotificationHelper(SettingsAdapter.this.context).createNotification(App.getRunningActivity().getString(R.string.auto_download), App.getRunningActivity().getString(R.string.auto_download_enabled));
                        }
                    }
                }
            });
            vHAdvanced.F.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    String str = "open_instagram";
                    if (SettingsAdapter.this.prefs.getBoolean(str, true)) {
                        ((VHAdvanced) viewHolder).H.setChecked(false);
                        SettingsAdapter.this.editor.putBoolean(str, false).apply();
                        ((NotificationManager) Objects.requireNonNull((NotificationManager) SettingsAdapter.this.context.getSystemService("notification"))).cancel(0);
                        return;
                    }
                    ((VHAdvanced) viewHolder).H.setChecked(true);
                    SettingsAdapter.this.editor.putBoolean(str, true).apply();
                }
            });
            vHAdvanced.H.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    SettingsAdapter.this.editor.putBoolean("open_instagram", z).apply();
                }
            });
        }
    }

    @NotNull
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        if (i == 0) {
            return new VHGeneral(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_settings_general, viewGroup, false));
        }
        if (i == 1) {
            return new VHAdvanced(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_settings_advanced, viewGroup, false));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("There is no type that matches the type ");
        sb.append(i);
        sb.append("!");
        throw new RuntimeException(sb.toString());
    }
}
