package com.jarvis.instarepost.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.jarvis.instarepost.R;
import com.jarvis.instarepost.adapters.DownloadMediaAdapter;
import com.jarvis.instarepost.adapters.DownloadMediaAdapter.OnSelectChangeListener;
import com.jarvis.instarepost.classes.App;
import com.jarvis.instarepost.models.MediaModel;
import com.jarvis.instarepost.models.PostModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class DownloadDialog extends Dialog implements OnSelectChangeListener {

    public boolean allSelected = false;

    public boolean captionExpanded = false;

    public ImageView imgSelect;
    private ImageView imgUserProfile;
    private DotsIndicator indicator;
    private LinearLayout llSelectAll;

    public OnSelectListener onSelectListener;

    public PostModel post;

    public TextView txtCaption;
    private TextView txtDownload;
    private TextView txtUserName;
    private ViewPager viewPager;
    private View viewSelectAll;

    public interface OnSelectListener {
        void onConfirm(PostModel postModel);
    }

    public DownloadDialog(final Context context, PostModel postModel) {
        super(context);
        requestWindowFeature(1);
        ((Window) Objects.requireNonNull(getWindow())).setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_download);
        setCancelable(true);
        post = postModel;
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (DotsIndicator) findViewById(R.id.indicator);
        imgUserProfile = (ImageView) findViewById(R.id.imgUserProfile);
        imgSelect = (ImageView) findViewById(R.id.imgSelect);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtCaption = (TextView) findViewById(R.id.txtCaption);
        txtDownload = (TextView) findViewById(R.id.txtDownload);
        llSelectAll = (LinearLayout) findViewById(R.id.llSelectAll);
        viewSelectAll = findViewById(R.id.viewSelectAll);
        viewPager.setAdapter(new DownloadMediaAdapter(context, post.getMedia()).setOnSelectChangeListener(this));
        indicator.setViewPager(viewPager);
        ((RequestBuilder) Glide.with((Activity) Objects.requireNonNull(App.getRunningActivity())).load(post.getProfile_pic_url()).apply((BaseRequestOptions<?>) RequestOptions.bitmapTransform(new RoundedCorners(1000))).error((int) R.drawable.image_user)).into(imgUserProfile);
        txtUserName.setText(post.getUsername());
        txtCaption.setText(post.getCaption());
        txtDownload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < post.getMedia().size(); i++) {
                    if (((MediaModel) post.getMedia().get(i)).isSelected()) {
                        arrayList.add(post.getMedia().get(i));
                    }
                }
                if (arrayList.size() > 0) {
                    post.setMedia(arrayList);
                    onSelectListener.onConfirm(post);
                    cancel();
                    return;
                }
                App.WarningToast(context.getString(R.string.no_media_selected)).show();
//                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

        changeAllMediaState(true);

        llSelectAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (allSelected) {
                    changeAllMediaState(false);
                } else {
                    changeAllMediaState(true);
                }
            }
        });
        txtCaption.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (captionExpanded) {
                    txtCaption.setMaxLines(3);
                    txtCaption.setEllipsize(TruncateAt.END);
                    captionExpanded = false;
                    return;
                }
                txtCaption.setMaxLines(Integer.MAX_VALUE);
                txtCaption.setEllipsize(null);
                captionExpanded = true;
            }
        });
        changeMediaState(0);
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
                if (post.getMedia().size() != 1) {
                    if (i == 0) {
                        imgSelect.setVisibility(0);
                    } else {
                        imgSelect.setVisibility(4);
                    }
                }
            }

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
                changeMediaState(i);
            }
        });
        if (post.getMedia().size() == 1) {
            llSelectAll.setVisibility(8);
            imgSelect.setVisibility(8);
            indicator.setVisibility(8);
        }
    }

    private boolean allSelected() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < post.getMedia().size(); i++) {
            arrayList.add(Boolean.valueOf(((MediaModel) post.getMedia().get(i)).isSelected()));
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (!((Boolean) it.next()).booleanValue()) {
                return false;
            }
        }
        return true;
    }


    public void changeAllMediaState(boolean z) {
        for (int i = 0; i < post.getMedia().size(); i++) {
            ((MediaModel) post.getMedia().get(i)).setSelected(z);
        }
        if (z) {
            viewSelectAll.setBackgroundResource(R.drawable.bg_select_all_active);
            allSelected = true;
            changeMediaState(viewPager.getCurrentItem());
            return;
        }
        viewSelectAll.setBackgroundResource(R.drawable.bg_select_all);
        allSelected = false;
        changeMediaState(viewPager.getCurrentItem());
    }


    public void changeMediaState(int i) {
        if (allSelected && !allSelected()) {
            viewSelectAll.setBackgroundResource(R.drawable.bg_select_all);
            allSelected = false;
        } else if (!allSelected && allSelected()) {
            viewSelectAll.setBackgroundResource(R.drawable.bg_select_all_active);
            allSelected = true;
        }
        if (((MediaModel) post.getMedia().get(i)).isSelected()) {
            imgSelect.setImageResource(R.drawable.ic_check_white);
            imgSelect.setBackgroundResource(R.drawable.bg_select_active);
            return;
        }
        imgSelect.setImageResource(17170445);
        imgSelect.setBackgroundResource(R.drawable.bg_select);
    }

    public void onSelectChange(ArrayList<MediaModel> arrayList) {
        post.setMedia(arrayList);
        changeMediaState(viewPager.getCurrentItem());
    }

    public DownloadDialog setOnSelectListener(OnSelectListener onSelectListener2) {
        onSelectListener = onSelectListener2;
        return this;
    }
}
