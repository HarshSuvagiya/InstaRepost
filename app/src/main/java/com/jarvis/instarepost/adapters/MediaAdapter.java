package com.jarvis.instarepost.adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jarvis.instarepost.R;
import com.jarvis.instarepost.classes.App;
import com.jarvis.instarepost.models.MediaModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class MediaAdapter extends PagerAdapter {
    private ImageView imgMediaPreview;
    private ImageView imgVideo;
    private LayoutInflater layoutInflater;
    private LinearLayout llLoadMediaFailed;
    public ArrayList<MediaModel> media;

    public MediaAdapter(Context context, ArrayList<MediaModel> arrayList) {
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
        String str;
        View inflate = this.layoutInflater.inflate(R.layout.adapter_media, viewGroup, false);
        this.imgMediaPreview = (ImageView) inflate.findViewById(R.id.imgMediaPreview);
        this.imgVideo = (ImageView) inflate.findViewById(R.id.imgVideo);
        this.llLoadMediaFailed = (LinearLayout) inflate.findViewById(R.id.llLoadMediaFailed);
        if (((MediaModel) this.media.get(i)).isVideo()) {
            str = ((MediaModel) this.media.get(i)).getPreview_file_path();
            this.imgVideo.setVisibility(0);
        } else {
            str = ((MediaModel) this.media.get(i)).getFile_path();
            this.imgVideo.setVisibility(8);
        }
        if (new File(str).exists()) {
            ((RequestBuilder) ((RequestBuilder) Glide.with((Activity) Objects.requireNonNull(App.getRunningActivity())).load(new File(str)).diskCacheStrategy(DiskCacheStrategy.NONE)).skipMemoryCache(true)).into(this.imgMediaPreview);
            this.llLoadMediaFailed.setVisibility(8);
        } else {
            this.llLoadMediaFailed.setVisibility(0);
        }
        this.imgMediaPreview.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    Context context = App.getContext();
                    StringBuilder sb = new StringBuilder();
                    sb.append(App.getContext().getPackageName());
                    sb.append(".provider");
                    Uri uriForFile = FileProvider.getUriForFile(context, sb.toString(), new File(((MediaModel) MediaAdapter.this.media.get(i)).getFile_path()));
                    intent.addFlags(1);
                    intent.setDataAndType(uriForFile, App.getRunningActivity().getContentResolver().getType(uriForFile));
                    App.getRunningActivity().startActivity(intent);
                } catch (ActivityNotFoundException unused) {
                    App.FailToast(App.getRunningActivity().getString(R.string.no_viewer)).show();
                }
            }
        });
        viewGroup.addView(inflate);
        return inflate;
    }

    public boolean isViewFromObject(@NotNull View view, @NotNull Object obj) {
        return view == obj;
    }
}
