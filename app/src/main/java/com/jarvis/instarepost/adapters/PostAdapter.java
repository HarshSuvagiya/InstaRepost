package com.jarvis.instarepost.adapters;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.app.ShareCompat.IntentBuilder;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.jarvis.instarepost.R;
import com.jarvis.instarepost.classes.App;
import com.jarvis.instarepost.database.DBHelper;
import com.jarvis.instarepost.dialogs.MessageDialog;
import com.jarvis.instarepost.dialogs.MessageDialog.OnActionListener;
import com.jarvis.instarepost.models.MediaModel;
import com.jarvis.instarepost.models.PostModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class PostAdapter extends Adapter<ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    public boolean captionExpanded = false;
    public SQLiteDatabase db;
    public OnModeChangeListener onModeChangeListener;
    public ArrayList<PostModel> posts;

    public interface OnModeChangeListener {
        void onModeChange();
    }

    class VHHeader extends ViewHolder {
        LinearLayout E;
        LinearLayout F;
        ImageView G;
        ImageView H;

        VHHeader(View view) {
            super(view);
            this.E = (LinearLayout) view.findViewById(R.id.llGrid);
            this.F = (LinearLayout) view.findViewById(R.id.llList);
            this.G = (ImageView) view.findViewById(R.id.imgGrid);
            this.H = (ImageView) view.findViewById(R.id.imgList);
        }
    }

    class VHItem extends ViewHolder {
        ViewPager E;
        DotsIndicator F;
        ImageView G;
        ImageView H;
        ImageView I;
        ImageView J;
        ImageView K;
        TextView L;
        TextView M;

        VHItem(View view) {
            super(view);
            this.E = (ViewPager) view.findViewById(R.id.viewPager);
            this.F = (DotsIndicator) view.findViewById(R.id.indicator);
            this.G = (ImageView) view.findViewById(R.id.imgUserProfile);
            this.H = (ImageView) view.findViewById(R.id.imgDelete);
            this.I = (ImageView) view.findViewById(R.id.imgCopy);
            this.J = (ImageView) view.findViewById(R.id.imgSetWallpaper);
            this.K = (ImageView) view.findViewById(R.id.imgShare);
            this.L = (TextView) view.findViewById(R.id.txtUserName);
            this.M = (TextView) view.findViewById(R.id.txtCaption);
        }
    }

    public PostAdapter(ArrayList<PostModel> arrayList) {
        this.posts = arrayList;
    }

    private boolean isPositionHeader(int i) {
        return i == 0;
    }

    public int getItemCount() {
        return this.posts.size() + 1;
    }

    public int getItemViewType(int i) {
        return isPositionHeader(i) ? 0 : 1;
    }

    public void onBindViewHolder(@NotNull final ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof VHItem) {
            final PostModel postModel = (PostModel) this.posts.get(i - 1);
            VHItem vHItem = (VHItem) viewHolder;
            vHItem.E.setAdapter(new MediaAdapter(App.getContext(), postModel.getMedia()));
            vHItem.F.setViewPager(vHItem.E);
            if (postModel.getMedia().size() == 1) {
                vHItem.F.setVisibility(8);
            } else {
                vHItem.F.setVisibility(0);
            }
            Glide.with((Activity) Objects.requireNonNull(App.getRunningActivity())).load(postModel.getProfile_pic_url()).apply((BaseRequestOptions<?>) RequestOptions.bitmapTransform(new RoundedCorners(1000))).into(vHItem.G);
            vHItem.L.setText(postModel.getUsername());
            vHItem.M.setText(postModel.getCaption());
            vHItem.M.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PostAdapter.this.captionExpanded) {
                        ((VHItem) viewHolder).M.setMaxLines(3);
                        ((VHItem) viewHolder).M.setEllipsize(TruncateAt.END);
                        PostAdapter.this.captionExpanded = false;
                        return;
                    }
                    ((VHItem) viewHolder).M.setMaxLines(Integer.MAX_VALUE);
                    ((VHItem) viewHolder).M.setEllipsize(null);
                    PostAdapter.this.captionExpanded = true;
                }
            });
            vHItem.H.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    new MessageDialog(App.getRunningActivity()).setTitle(App.getRunningActivity().getString(R.string.delete_post_title)).setMessage(App.getRunningActivity().getString(R.string.delete_post)).setConfirmText(App.getRunningActivity().getString(R.string.yes)).setCancelText(App.getRunningActivity().getString(R.string.no)).setOnActionListener(new OnActionListener() {
                        public void onCancel() {
                        }

                        public void onConfirm() {
                            PostAdapter.this.db = new DBHelper(App.getContext()).getWritableDatabase();
                            SQLiteDatabase b = PostAdapter.this.db;
                            StringBuilder sb = new StringBuilder();
                            sb.append("DELETE FROM posts WHERE id = '");
                            sb.append(postModel.getId());
                            sb.append("'");
                            b.execSQL(sb.toString());
                            PostAdapter.this.posts.remove(i - 1);
                            PostAdapter.this.notifyDataSetChanged();
                            for (int i = 0; i < postModel.getMedia().size(); i++) {
                                String str = "";
                                if (((MediaModel) postModel.getMedia().get(i)).getFile_path() != null && !((MediaModel) postModel.getMedia().get(i)).getFile_path().equals(str)) {
                                    new File(((MediaModel) postModel.getMedia().get(i)).getFile_path()).delete();
                                }
                                if (((MediaModel) postModel.getMedia().get(i)).getPreview_file_path() != null && !((MediaModel) postModel.getMedia().get(i)).getPreview_file_path().equals(str)) {
                                    new File(((MediaModel) postModel.getMedia().get(i)).getPreview_file_path()).delete();
                                }
                            }
                            App.SuccessToast(App.getRunningActivity().getString(R.string.files_deleted)).show();
                        }
                    }).show();
                }
            });
            vHItem.I.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ClipboardManager clipboardManager = (ClipboardManager) App.getContext().getSystemService("clipboard");
                    ((ClipboardManager) Objects.requireNonNull(clipboardManager)).setPrimaryClip(ClipData.newPlainText("", postModel.getCaption()));
                    App.SuccessToast(App.getRunningActivity().getString(R.string.caption_copied)).show();
                }
            });
            vHItem.J.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (((MediaModel) ((PostModel) PostAdapter.this.posts.get(i - 1)).getMedia().get(((VHItem) viewHolder).E.getCurrentItem())).isVideo()) {
                        App.WarningToast(App.getRunningActivity().getString(R.string.set_video_wallpaper)).show();
                    } else {
                        new MessageDialog(App.getRunningActivity()).setTitle(App.getRunningActivity().getString(R.string.set_wallpaper_title)).setMessage(App.getRunningActivity().getString(R.string.set_wallpaper)).setConfirmText(App.getRunningActivity().getString(R.string.ok)).setCancelText(App.getRunningActivity().getString(R.string.cancel)).setOnActionListener(new OnActionListener() {
                            public void onCancel() {
                            }

                            public void onConfirm() {
                                File file = new File(((MediaModel) ((PostModel) PostAdapter.this.posts.get(i - 1)).getMedia().get(((VHItem) viewHolder).E.getCurrentItem())).getFile_path());
                                Bitmap decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath());
                                WallpaperManager instance = WallpaperManager.getInstance(App.getContext());
                                if (file.exists()) {
                                    try {
                                        instance.setBitmap(decodeFile);
                                        App.SuccessToast(App.getRunningActivity().getString(R.string.wallpaper_changed)).show();
                                    } catch (IOException e) {
                                        App.FailToast(App.getRunningActivity().getString(R.string.wallpaper_change_failed)).show();
                                        e.printStackTrace();
                                    }
                                } else {
                                    App.FailToast(App.getRunningActivity().getString(R.string.file_not_exist)).show();
                                }
                            }
                        }).show();
                    }
                }
            });
            vHItem.K.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    File file = new File(((MediaModel) ((PostModel) PostAdapter.this.posts.get(i - 1)).getMedia().get(((VHItem) viewHolder).E.getCurrentItem())).getFile_path());
                    if (file.exists()) {
                        Context context = App.getContext();
                        StringBuilder sb = new StringBuilder();
                        sb.append(App.getContext().getPackageName());
                        sb.append(".provider");
                        Uri uriForFile = FileProvider.getUriForFile(context, sb.toString(), file);
                        App.getRunningActivity().startActivity(IntentBuilder.from((Activity) Objects.requireNonNull(App.getRunningActivity())).setStream(uriForFile).getIntent().setAction("android.intent.action.SEND").setDataAndType(uriForFile, App.getRunningActivity().getContentResolver().getType(uriForFile)).addFlags(1));
                        return;
                    }
                    App.FailToast(App.getRunningActivity().getString(R.string.file_not_exist)).show();
                }
            });
            vHItem.G.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("http://instagram.com/_u/");
                    sb.append(postModel.getUsername());
                    String str = "android.intent.action.VIEW";
                    Intent intent = new Intent(str, Uri.parse(sb.toString()));
                    intent.setPackage("com.instagram.android");
                    try {
                        App.getRunningActivity().startActivity(intent);
                    } catch (ActivityNotFoundException unused) {
                        Activity runningActivity = App.getRunningActivity();
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("http://instagram.com/");
                        sb2.append(postModel.getUsername());
                        runningActivity.startActivity(new Intent(str, Uri.parse(sb2.toString())));
                    }
                }
            });
        } else if (viewHolder instanceof VHHeader) {
            VHHeader vHHeader = (VHHeader) viewHolder;
            vHHeader.E.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    PostAdapter.this.onModeChangeListener.onModeChange();
                }
            });
            vHHeader.G.setImageResource(R.drawable.ic_grid);
            vHHeader.H.setImageResource(R.drawable.ic_list_active);
        }
    }

    @NotNull
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            return new VHItem(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_post, viewGroup, false));
        }
        if (i == 0) {
            return new VHHeader(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_post_header, viewGroup, false));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("There is no type that matches the type ");
        sb.append(i);
        sb.append("!");
        throw new RuntimeException(sb.toString());
    }

    public PostAdapter setOnModeChangeListener(OnModeChangeListener onModeChangeListener2) {
        this.onModeChangeListener = onModeChangeListener2;
        return this;
    }
}
