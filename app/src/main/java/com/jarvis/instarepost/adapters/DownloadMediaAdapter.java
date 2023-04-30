package com.jarvis.instarepost.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.jarvis.instarepost.R;
import com.jarvis.instarepost.classes.App;
import com.jarvis.instarepost.models.MediaModel;
import java.util.ArrayList;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class DownloadMediaAdapter extends PagerAdapter {
    private ImageView imgMediaPreview;
    private ImageView imgVideo;
    private LayoutInflater layoutInflater;

    public ArrayList<MediaModel> media;

    public OnSelectChangeListener onSelectChangeListener;

    public interface OnSelectChangeListener {
        void onSelectChange(ArrayList<MediaModel> arrayList);
    }

    public DownloadMediaAdapter(Context context, ArrayList<MediaModel> arrayList) {
        this.media = arrayList;
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public void destroyItem(ViewGroup viewGroup, int i, @NotNull Object obj) {
        viewGroup.removeView((View) obj);
    }

    public int getCount() {
        return this.media.size();
    }

    @NotNull
    public Object instantiateItem(@NotNull ViewGroup viewGroup, final int i) {
        View inflate = this.layoutInflater.inflate(R.layout.adapter_download_media, viewGroup, false);
        this.imgMediaPreview = (ImageView) inflate.findViewById(R.id.imgMediaPreview);
        this.imgVideo = (ImageView) inflate.findViewById(R.id.imgVideo);
        if (((MediaModel) this.media.get(i)).isVideo()) {
            this.imgVideo.setVisibility(0);
        } else {
            this.imgVideo.setVisibility(8);
        }
        Glide.with((Activity) Objects.requireNonNull(App.getRunningActivity())).load(((MediaModel) this.media.get(i)).getDisplay_url()).into(this.imgMediaPreview);
        if (this.media.size() == 1) {
            ((MediaModel) this.media.get(i)).setSelected(true);
            this.onSelectChangeListener.onSelectChange(this.media);
        }
        this.imgMediaPreview.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (DownloadMediaAdapter.this.media.size() != 1) {
                    if (((MediaModel) DownloadMediaAdapter.this.media.get(i)).isSelected()) {
                        ((MediaModel) DownloadMediaAdapter.this.media.get(i)).setSelected(false);
                    } else {
                        ((MediaModel) DownloadMediaAdapter.this.media.get(i)).setSelected(true);
                    }
                    DownloadMediaAdapter.this.onSelectChangeListener.onSelectChange(DownloadMediaAdapter.this.media);
                }
            }
        });
        viewGroup.addView(inflate);
        return inflate;
    }

    public boolean isViewFromObject(@NotNull View view, @NotNull Object obj) {
        return view == obj;
    }

    public DownloadMediaAdapter setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener2) {
        this.onSelectChangeListener = onSelectChangeListener2;
        return this;
    }
}
