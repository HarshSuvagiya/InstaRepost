package com.jarvis.instarepost.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jarvis.instarepost.R;
import com.jarvis.instarepost.classes.App;
import com.jarvis.instarepost.dialogs.PostDialog;
import com.jarvis.instarepost.dialogs.PostDialog.OnPostDeleteListener;
import com.jarvis.instarepost.models.MediaModel;
import com.jarvis.instarepost.models.PostModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public class PostGridAdapter extends Adapter<ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    public ArrayList<MediaModel> media;
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
        ImageView E;
        ImageView F;
        LinearLayout G;

        VHItem(View view) {
            super(view);
            this.E = (ImageView) view.findViewById(R.id.imgMediaPreview);
            this.F = (ImageView) view.findViewById(R.id.imgVideo);
            this.G = (LinearLayout) view.findViewById(R.id.llLoadMediaFailed);
        }
    }

    public PostGridAdapter(ArrayList<PostModel> arrayList, ArrayList<MediaModel> arrayList2) {
        this.posts = arrayList;
        this.media = arrayList2;
    }

    private boolean isPositionHeader(int i) {
        return i == 0;
    }

    public int getItemCount() {
        return this.media.size() + 1;
    }

    public int getItemViewType(int i) {
        return isPositionHeader(i) ? 0 : 1;
    }

    public void onBindViewHolder(@NotNull ViewHolder viewHolder, final int i) {
        String str;
        if (viewHolder instanceof VHItem) {
            int i2 = i - 1;
            if (((MediaModel) this.media.get(i2)).isVideo()) {
                str = ((MediaModel) this.media.get(i2)).getPreview_file_path();
            } else {
                str = ((MediaModel) this.media.get(i2)).getFile_path();
            }
            if (new File(str).exists()) {
                VHItem vHItem = (VHItem) viewHolder;
                ((RequestBuilder) ((RequestBuilder) Glide.with((Activity) Objects.requireNonNull(App.getRunningActivity())).load(new File(str)).diskCacheStrategy(DiskCacheStrategy.NONE)).skipMemoryCache(true)).into(vHItem.E);
                vHItem.G.setVisibility(8);
                if (((MediaModel) this.media.get(i2)).isVideo()) {
                    vHItem.F.setVisibility(0);
                } else {
                    vHItem.F.setVisibility(8);
                }
            } else {
                ((VHItem) viewHolder).G.setVisibility(0);
            }
            ((VHItem) viewHolder).E.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    for (int j = 0; j < PostGridAdapter.this.posts.size(); j++) {
                        if (((PostModel) PostGridAdapter.this.posts.get(j)).getId().equals(((MediaModel) PostGridAdapter.this.media.get(i - 1)).getPostId())) {
                            new PostDialog(App.getRunningActivity(), (PostModel) PostGridAdapter.this.posts.get(j)).setOnPostDeleteListener(new OnPostDeleteListener() {
                                public void onPostDelete(ArrayList<MediaModel> arrayList) {
                                    PostGridAdapter.this.media.removeAll(arrayList);
                                    PostGridAdapter.this.notifyDataSetChanged();
                                }
                            }).show();
                        }
                    }
                }
            });
        } else if (viewHolder instanceof VHHeader) {
            VHHeader vHHeader = (VHHeader) viewHolder;
            vHHeader.F.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    PostGridAdapter.this.onModeChangeListener.onModeChange();
                }
            });
            vHHeader.G.setImageResource(R.drawable.ic_grid_active);
            vHHeader.H.setImageResource(R.drawable.ic_list);
        }
    }

    @NotNull
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            return new VHItem(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_post_grid, viewGroup, false));
        }
        if (i == 0) {
            return new VHHeader(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_post_grid_header, viewGroup, false));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("There is no type that matches the type ");
        sb.append(i);
        sb.append("!");
        throw new RuntimeException(sb.toString());
    }

    public PostGridAdapter setOnModeChangeListener(OnModeChangeListener onModeChangeListener2) {
        this.onModeChangeListener = onModeChangeListener2;
        return this;
    }
}
