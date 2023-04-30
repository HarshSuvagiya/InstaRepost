package com.jarvis.instarepost.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.app.ShareCompat.IntentBuilder;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.jarvis.instarepost.R;
import com.jarvis.instarepost.adapters.MediaAdapter;
import com.jarvis.instarepost.classes.App;
import com.jarvis.instarepost.database.DBHelper;
import com.jarvis.instarepost.dialogs.MessageDialog.OnActionListener;
import com.jarvis.instarepost.models.MediaModel;
import com.jarvis.instarepost.models.PostModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PostDialog extends Dialog {

    public boolean captionExpanded = false;

    public SQLiteDatabase db;
    private ImageView imgCopy;
    private ImageView imgDelete;
    private ImageView imgSetWallpaper;
    private ImageView imgShare;
    private ImageView imgUserProfile;
    private DotsIndicator indicator;

    public OnPostDeleteListener onPostDeleteListener;

    public PostModel post;

    public TextView txtCaption;
    private TextView txtUserName;

    public ViewPager viewPager;

    public interface OnPostDeleteListener {
        void onPostDelete(ArrayList<MediaModel> arrayList);
    }

    public PostDialog(Context context, PostModel postModel) {
        super(context);
        requestWindowFeature(1);
        ((Window) Objects.requireNonNull(getWindow())).setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_post);
        setCancelable(true);
        this.post = postModel;
        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        this.indicator = (DotsIndicator) findViewById(R.id.indicator);
        this.imgUserProfile = (ImageView) findViewById(R.id.imgUserProfile);
        this.imgDelete = (ImageView) findViewById(R.id.imgDelete);
        this.imgCopy = (ImageView) findViewById(R.id.imgCopy);
        this.imgSetWallpaper = (ImageView) findViewById(R.id.imgSetWallpaper);
        this.imgShare = (ImageView) findViewById(R.id.imgShare);
        this.txtUserName = (TextView) findViewById(R.id.txtUserName);
        this.txtCaption = (TextView) findViewById(R.id.txtCaption);
        this.viewPager.setAdapter(new MediaAdapter(App.getContext(), this.post.getMedia()));
        this.indicator.setViewPager(this.viewPager);
        if (this.post.getMedia().size() == 1) {
            this.indicator.setVisibility(8);
        } else {
            this.indicator.setVisibility(0);
        }
        Glide.with((Activity) Objects.requireNonNull(App.getRunningActivity())).load(this.post.getProfile_pic_url()).apply((BaseRequestOptions<?>) RequestOptions.bitmapTransform(new RoundedCorners(1000))).into(this.imgUserProfile);
        this.txtUserName.setText(this.post.getUsername());
        this.txtCaption.setText(this.post.getCaption());
        this.txtCaption.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PostDialog.this.captionExpanded) {
                    PostDialog.this.txtCaption.setMaxLines(3);
                    PostDialog.this.txtCaption.setEllipsize(TruncateAt.END);
                    PostDialog.this.captionExpanded = false;
                    return;
                }
                PostDialog.this.txtCaption.setMaxLines(Integer.MAX_VALUE);
                PostDialog.this.txtCaption.setEllipsize(null);
                PostDialog.this.captionExpanded = true;
            }
        });
        this.imgDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new MessageDialog(App.getRunningActivity()).setTitle(App.getRunningActivity().getString(R.string.delete_post_title)).setMessage(App.getRunningActivity().getString(R.string.delete_post)).setConfirmText(App.getRunningActivity().getString(R.string.yes)).setCancelText(App.getRunningActivity().getString(R.string.no)).setOnActionListener(new OnActionListener() {
                    public void onCancel() {
                    }

                    public void onConfirm() {
                        PostDialog.this.db = new DBHelper(App.getContext()).getWritableDatabase();
                        SQLiteDatabase c = PostDialog.this.db;
                        StringBuilder sb = new StringBuilder();
                        sb.append("DELETE FROM posts WHERE id = '");
                        sb.append(PostDialog.this.post.getId());
                        sb.append("'");
                        c.execSQL(sb.toString());
                        for (int i = 0; i < PostDialog.this.post.getMedia().size(); i++) {
                            String str = "";
                            if (((MediaModel) PostDialog.this.post.getMedia().get(i)).getFile_path() != null && !((MediaModel) PostDialog.this.post.getMedia().get(i)).getFile_path().equals(str)) {
                                new File(((MediaModel) PostDialog.this.post.getMedia().get(i)).getFile_path()).delete();
                            }
                            if (((MediaModel) PostDialog.this.post.getMedia().get(i)).getPreview_file_path() != null && !((MediaModel) PostDialog.this.post.getMedia().get(i)).getPreview_file_path().equals(str)) {
                                new File(((MediaModel) PostDialog.this.post.getMedia().get(i)).getPreview_file_path()).delete();
                            }
                        }
                        App.SuccessToast(App.getContext().getString(R.string.files_deleted)).show();
                        PostDialog.this.onPostDeleteListener.onPostDelete(PostDialog.this.post.getMedia());
                        PostDialog.this.dismiss();
                    }
                }).show();
            }
        });
        this.imgCopy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) App.getContext().getSystemService("clipboard");
                ((ClipboardManager) Objects.requireNonNull(clipboardManager)).setPrimaryClip(ClipData.newPlainText("", PostDialog.this.post.getCaption()));
                App.SuccessToast(App.getContext().getString(R.string.caption_copied)).show();
            }
        });
        this.imgSetWallpaper.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (((MediaModel) PostDialog.this.post.getMedia().get(PostDialog.this.viewPager.getCurrentItem())).isVideo()) {
                    App.WarningToast(App.getContext().getString(R.string.set_video_wallpaper)).show();
                } else {
                    new MessageDialog(App.getRunningActivity()).setTitle(App.getRunningActivity().getString(R.string.set_wallpaper_title)).setMessage(App.getContext().getString(R.string.set_wallpaper)).setConfirmText(App.getContext().getString(R.string.ok)).setCancelText(App.getContext().getString(R.string.cancel)).setOnActionListener(new OnActionListener() {
                        public void onCancel() {
                        }

                        public void onConfirm() {
                            File file = new File(((MediaModel) PostDialog.this.post.getMedia().get(PostDialog.this.viewPager.getCurrentItem())).getFile_path());
                            Bitmap decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath());
                            WallpaperManager instance = WallpaperManager.getInstance(App.getContext());
                            if (file.exists()) {
                                try {
                                    instance.setBitmap(decodeFile);
                                    App.SuccessToast(App.getContext().getString(R.string.wallpaper_changed)).show();
                                } catch (IOException e) {
                                    App.FailToast(App.getContext().getString(R.string.wallpaper_change_failed)).show();
                                    e.printStackTrace();
                                }
                            } else {
                                App.FailToast(App.getContext().getString(R.string.file_not_exist)).show();
                            }
                        }
                    }).show();
                }
            }
        });
        this.imgShare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                File file = new File(((MediaModel) PostDialog.this.post.getMedia().get(PostDialog.this.viewPager.getCurrentItem())).getFile_path());
                if (file.exists()) {
                    Context context = App.getContext();
                    StringBuilder sb = new StringBuilder();
                    sb.append(App.getContext().getPackageName());
                    sb.append(".provider");
                    Uri uriForFile = FileProvider.getUriForFile(context, sb.toString(), file);
                    App.getRunningActivity().startActivity(IntentBuilder.from((Activity) Objects.requireNonNull(App.getRunningActivity())).setStream(uriForFile).getIntent().setAction("android.intent.action.SEND").setDataAndType(uriForFile, App.getRunningActivity().getContentResolver().getType(uriForFile)).addFlags(1));
                    return;
                }
                App.FailToast(App.getContext().getString(R.string.file_not_exist)).show();
            }
        });
        this.imgUserProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StringBuilder sb = new StringBuilder();
                sb.append("http://instagram.com/_u/");
                sb.append(PostDialog.this.post.getUsername());
                String str = "android.intent.action.VIEW";
                Intent intent = new Intent(str, Uri.parse(sb.toString()));
                intent.setPackage("com.instagram.android");
                try {
                    App.getRunningActivity().startActivity(intent);
                } catch (ActivityNotFoundException unused) {
                    Activity runningActivity = App.getRunningActivity();
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("http://instagram.com/");
                    sb2.append(PostDialog.this.post.getUsername());
                    runningActivity.startActivity(new Intent(str, Uri.parse(sb2.toString())));
                }
            }
        });
    }

    public PostDialog setOnPostDeleteListener(OnPostDeleteListener onPostDeleteListener2) {
        this.onPostDeleteListener = onPostDeleteListener2;
        return this;
    }
}
