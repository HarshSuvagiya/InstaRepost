package com.jarvis.instarepost.activities;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.jarvis.instarepost.R;
import com.jarvis.instarepost.adapters.HelpAdapter;
import com.jarvis.instarepost.adapters.PostAdapter;
import com.jarvis.instarepost.adapters.PostAdapter.OnModeChangeListener;
import com.jarvis.instarepost.adapters.PostGridAdapter;
import com.jarvis.instarepost.adapters.SettingsAdapter;
import com.jarvis.instarepost.classes.App;
import com.jarvis.instarepost.classes.ClipBoardService;
import com.jarvis.instarepost.classes.SingleMediaScanner;
import com.jarvis.instarepost.database.DBHelper;
import com.jarvis.instarepost.dialogs.DownloadDialog;
import com.jarvis.instarepost.dialogs.DownloadDialog.OnSelectListener;
import com.jarvis.instarepost.dialogs.DownloadProgressDialog;
import com.jarvis.instarepost.dialogs.MessageDialog;
import com.jarvis.instarepost.dialogs.MessageDialog.OnActionListener;
import com.jarvis.instarepost.dialogs.ProgressDialog;
import com.jarvis.instarepost.models.HelpModel;
import com.jarvis.instarepost.models.MediaModel;
import com.jarvis.instarepost.models.PostModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nineoldandroids.view.ViewHelper;
import com.securepreferences.SecurePreferences;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.HTTP;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class MainActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {
    private static final int VIEW_MODE_GRID = 1;
    private static final int VIEW_MODE_LIST = 0;

    public CardView cardMain;

    public int cardMainHeight;
    private SQLiteDatabase db;

    public DownloadDialog downloadDialog;

    public DownloadProgressDialog downloadProgressDialog;

    public Editor editor;

    public EditText edtPostUrl;

    public ArrayList<HelpModel> help = new ArrayList<>();

    public HelpAdapter helpAdapter;
    private AsyncHttpClient httpClient = new AsyncHttpClient();
    private ImageView imgDownload;

    public ImageView imgHelp;

    public ImageView imgHome;
    private ImageView imgInstagram;
    private ImageView imgRate;

    public ImageView imgSettings;

    public LinearLayout layoutDownload;
    private LinearLayout llAutoDownload;

    public LinearLayout llHelp;

    public LinearLayout llHome;

    public LinearLayout llSettings;
    private ArrayList<MediaModel> media = new ArrayList<>();

    ShowcaseView p;
    private PulsatorLayout plAutoDownload;

    public PostModel post = new PostModel();
    private PostAdapter postAdapter;
    private PostGridAdapter postGridAdapter;

    public int postViewMode;
    private ArrayList<PostModel> posts = new ArrayList<>();

    public SharedPreferences prefs;
    private ProgressDialog progressDialog;

    public ObservableRecyclerView rvPosts;

    public SettingsAdapter settingsAdapter;

    public TextView txtHelp;

    public TextView txtHome;

    public TextView txtInstagram;

    public TextView txtRate;

    public TextView txtSettings;
    String autoDownloadFlag = "auto_download";

    public void changeAdapter(int i) {
        if (i == 0) {
            rvPosts.setPadding(0, cardMainHeight - ((int) dpToFloat(40.0f)), 0, (int) dpToFloat(6.0f));
            rvPosts.setLayoutManager(new LinearLayoutManager(this));
            postAdapter = new PostAdapter(posts).setOnModeChangeListener(new OnModeChangeListener() {
                public void onModeChange() {
                    changeAdapter(1);
                }
            });
            rvPosts.setAdapter(postAdapter);
            postViewMode = 0;
            getDownloadedPosts();
            rvPosts.smoothScrollToPosition(0);
            return;
        }
        String str = "English";
        String str2 = "language";
        if (prefs.getString(str2, str).equals("Persian") || prefs.getString(str2, str).equals("Arabic")) {
            rvPosts.setPadding((int) dpToFloat(8.0f), cardMainHeight - ((int) dpToFloat(40.0f)), (int) dpToFloat(8.0f), (int) dpToFloat(6.0f));
        } else {
            rvPosts.setPadding(0, cardMainHeight - ((int) dpToFloat(40.0f)), (int) dpToFloat(16.0f), (int) dpToFloat(6.0f));
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int i) {
                return i == 0 ? 2 : 1;
            }
        });
        rvPosts.setLayoutManager(gridLayoutManager);
        for (int i2 = 0; i2 < posts.size(); i2++) {
            media.addAll(((PostModel) posts.get(i2)).getMedia());
        }
        postGridAdapter = new PostGridAdapter(posts, media).setOnModeChangeListener(new PostGridAdapter.OnModeChangeListener() {
            public void onModeChange() {
                changeAdapter(0);
            }
        });
        rvPosts.setAdapter(postGridAdapter);
        postViewMode = 1;
        getDownloadedPosts();
        rvPosts.smoothScrollToPosition((int) 3);
    }

    public void deactivateTabs() {
        llHome.setBackgroundResource(R.drawable.bg_home);
        imgHome.setImageResource(R.drawable.ic_downloads);
        txtHome.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorToastFail));
        llSettings.setBackgroundResource(R.drawable.bg_settings);
        imgSettings.setImageResource(R.drawable.ic_settings);
        txtSettings.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorToastFail));
        llHelp.setBackgroundResource(R.drawable.bg_help);
        imgHelp.setImageResource(R.drawable.ic_help);
        txtHelp.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorToastFail));
    }


    public void dismissDownloadProgressDialog() {
        DownloadProgressDialog downloadProgressDialog2 = downloadProgressDialog;
        if (downloadProgressDialog2 != null && downloadProgressDialog2.isShowing()) {
            downloadProgressDialog.dismiss();
        }
    }


    public void dismissProgressDialog() {
        ProgressDialog progressDialog2 = progressDialog;
        if (progressDialog2 != null && progressDialog2.isShowing()) {
            progressDialog.dismiss();
        }
    }


    public void downloadFile(PostModel postModel, int i, Boolean bool, int i2) {
        String str;
        String str2;
        if (i > postModel.getMedia().size() - 1) {
            downloadFilesFinished(postModel);
            return;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < postModel.getMedia().size(); i4++) {
            i3 = ((MediaModel) postModel.getMedia().get(i4)).isVideo() ? i3 + 2 : i3 + 1;
        }
        DownloadProgressDialog downloadProgressDialog2 = downloadProgressDialog;
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.downloading_files));
//        sb.append(" ");
//        sb.append(i2 + 1);
//        sb.append("/");
//        sb.append(i3);
        downloadProgressDialog2.setMessage(sb.toString()).show();
        downloadProgressDialog.setProgress(0);
        if (!((MediaModel) postModel.getMedia().get(i)).isVideo()) {
            str2 = ((MediaModel) postModel.getMedia().get(i)).getPhoto_url();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(((MediaModel) postModel.getMedia().get(i)).getId());
            sb2.append(".jpg");
            str = sb2.toString();
        } else if (bool.booleanValue()) {
            str2 = ((MediaModel) postModel.getMedia().get(i)).getDisplay_url();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(((MediaModel) postModel.getMedia().get(i)).getId());
            sb3.append("-preview.jpg");
            str = sb3.toString();
        } else {
            str2 = ((MediaModel) postModel.getMedia().get(i)).getVideo_url();
            StringBuilder sb4 = new StringBuilder();
            sb4.append(((MediaModel) postModel.getMedia().get(i)).getId());
            sb4.append(".mp4");
            str = sb4.toString();
        }
        final String str3 = str;
        DownloadRequest onProgressListener = PRDownloader.download(str2, getFolderPath(), str3).build().setOnStartOrResumeListener(new OnStartOrResumeListener() {
            public void onStartOrResume() {
            }
        }).setOnProgressListener(new OnProgressListener() {
            public void onProgress(Progress progress) {
                downloadProgressDialog.setProgress((int) ((progress.currentBytes * 100) / progress.totalBytes));
            }
        });
        final Boolean bool2 = bool;
        final PostModel postModel2 = postModel;
        final int i5 = i;
        final int i6 = i2;
        OnDownloadListener r1 = new OnDownloadListener() {
            public void onDownloadComplete() {
                String str = "/";
                if (bool2.booleanValue()) {
                    MediaModel mediaModel = (MediaModel) postModel2.getMedia().get(i5);
                    StringBuilder sb = new StringBuilder();
                    sb.append(getFolderPath());
                    sb.append(str);
                    sb.append(str3);
                    mediaModel.setPreview_file_path(sb.toString());
                } else {
                    MediaModel mediaModel2 = (MediaModel) postModel2.getMedia().get(i5);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(getFolderPath());
                    sb2.append(str);
                    sb2.append(str3);
                    mediaModel2.setFile_path(sb2.toString());
                    MainActivity mainActivity = MainActivity.this;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(getFolderPath());
                    sb3.append(str);
                    sb3.append(str3);
                    new SingleMediaScanner(mainActivity, new File(sb3.toString()));
                }
                if (!((MediaModel) postModel2.getMedia().get(i5)).isVideo() || bool2.booleanValue()) {
                    downloadFile(postModel2, i5 + 1, Boolean.valueOf(false), i6 + 1);
                } else {
                    downloadFile(postModel2, i5, Boolean.valueOf(true), i6 + 1);
                }
            }

            public void onError(Error error) {
                App.FailToast(getString(R.string.download_files_failed)).show();
                dismissDownloadProgressDialog();
            }
        };
        onProgressListener.start(r1);
    }

    private void downloadFilesFinished(PostModel postModel) {
        JsonArray asJsonArray = new GsonBuilder().create().toJsonTree(postModel.getMedia()).getAsJsonArray();
        byte[] bytes = postModel.getCaption().getBytes(StandardCharsets.UTF_8);
        db = new DBHelper(this).getWritableDatabase();
        SQLiteDatabase sQLiteDatabase = db;
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT or replace INTO posts (id, profile_pic_url, username, caption, media, time) VALUES('");
        sb.append(postModel.getId());
        String str = "','";
        sb.append(str);
        sb.append(postModel.getProfile_pic_url());
        sb.append(str);
        sb.append(postModel.getUsername());
        sb.append(str);
        sb.append(Base64.encodeToString(bytes, 0));
        sb.append(str);
        sb.append(asJsonArray.toString());
        sb.append(str);
        sb.append(System.currentTimeMillis());
        sb.append("')");
        sQLiteDatabase.execSQL(sb.toString());
        dismissDownloadProgressDialog();
        getDownloadedPosts();
        rvPosts.smoothScrollToPosition(0);
    }


    public float dpToFloat(float f) {
        return TypedValue.applyDimension(1, f, getApplicationContext().getResources().getDisplayMetrics());
    }

    private int getActionBarSize() {
        TypedArray obtainStyledAttributes = getTheme().obtainStyledAttributes(new int[]{16843499});
        int dimension = (int) obtainStyledAttributes.getDimension(0, 0.0f);
        obtainStyledAttributes.recycle();
        return dimension;
    }

    private void getDownloadedPosts() {
        posts.clear();
        media.clear();
        db = new DBHelper(this).getWritableDatabase();
        Cursor rawQuery = db.rawQuery("SELECT * FROM posts ORDER BY time DESC", null);
        while (rawQuery.moveToNext()) {
            PostModel postModel = new PostModel();
            postModel.setId(rawQuery.getString(rawQuery.getColumnIndex("id")));
            postModel.setProfile_pic_url(rawQuery.getString(rawQuery.getColumnIndex("profile_pic_url")));
            postModel.setUsername(rawQuery.getString(rawQuery.getColumnIndex("username")));
            postModel.setCaption(new String(Base64.decode(rawQuery.getString(rawQuery.getColumnIndex("caption")), 0), StandardCharsets.UTF_8));
            postModel.setMedia((ArrayList) new GsonBuilder().create().fromJson(rawQuery.getString(rawQuery.getColumnIndex("media")), new TypeToken<ArrayList<MediaModel>>() {
            }.getType()));
            posts.add(postModel);
            media.addAll(postModel.getMedia());
        }
        rawQuery.close();
        if (postViewMode == 0) {
            postAdapter.notifyDataSetChanged();
        } else {
            postGridAdapter.notifyDataSetChanged();
        }
    }


    public String getFolderPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append("/");
        sb.append(prefs.getString("app_folder_path", getResources().getString(R.string.app_name)));
        File file = new File(sb.toString());
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        if (file.mkdir()) {
            return file.getAbsolutePath();
        }
        App.FailToast(getString(R.string.create_folder_failed)).show();
        return null;
    }


    public void getPostInfo(String str) {
        String str2;
        if (!networkAvailable()) {
            App.WarningToast(getString(R.string.no_internet)).show();
            return;
        }
        if (str.endsWith("/")) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("?__a=1");
            str2 = sb.toString();
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append("/?__a=1");
            str2 = sb2.toString();
        }
        progressDialog = new ProgressDialog(this);
        if (!isFinishing()) {
            progressDialog.show();
        }
        httpClient.get(str2, new TextHttpResponseHandler() {
            public void onFailure(int i, Header[] headerArr, String str, Throwable th) {
                dismissProgressDialog();
                if (str == null || !str.contains("Page Not Found")) {
                    urlNotValid();
                } else {
                    App.WarningToast(getString(R.string.private_page)).show();
                }
            }

            public void onSuccess(int i, Header[] headerArr, String str) {
                JSONObject jSONObject;
                String str2 = "edge_sidecar_to_children";
                String str3 = "edges";
                String str4 = "id";
                JSONObject jSONObject2 = null;
                try {
                    jSONObject = new JSONObject(str);
                } catch (Throwable unused) {
                    App.WarningToast(getString(R.string.private_page)).show();
                    jSONObject = null;
                }
                if (jSONObject != null) {
                    try {
                        jSONObject2 = jSONObject.getJSONObject("graphql").getJSONObject("shortcode_media");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        urlNotValid();
                    }
                }
                if (jSONObject2 != null) {
                    JSONObject jSONObject3 = null;
                    try {
                        jSONObject3 = jSONObject2.getJSONObject("owner");

                        post.setId(jSONObject2.getString(str4));
                        post.setProfile_pic_url(jSONObject3.getString("profile_pic_url"));
                        post.setUsername(jSONObject3.getString("username"));
                        JSONArray jSONArray = jSONObject2.getJSONObject("edge_media_to_caption").getJSONArray(str3);

                        Log.e("MainActID",jSONObject2.getString(str4));
                        Log.e("MainActDP",jSONObject3.getString("profile_pic_url"));
                        Log.e("MainActUsername",jSONObject3.getString("username"));

                        String str5 = "node";
                        if (jSONArray.length() > 0) {
                            post.setCaption(jSONArray.getJSONObject(0).getJSONObject(str5).getString("text"));
                        } else {
                            post.setCaption("");
                        }
                        ArrayList arrayList = new ArrayList();
                        String str6 = "video_url";
                        String str7 = "is_video";
                        String str8 = "display_resources";
                        String str9 = "src";
                        if (jSONObject2.has(str2)) {
                            JSONArray jSONArray2 = jSONObject2.getJSONObject(str2).getJSONArray(str3);
                            for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                                JSONObject jSONObject4 = jSONArray2.getJSONObject(i2).getJSONObject(str5);
                                JSONArray jSONArray3 = jSONObject4.getJSONArray(str8);
                                MediaModel mediaModel = new MediaModel();
                                mediaModel.setId(jSONObject4.getString(str4));
                                mediaModel.setPostId(jSONObject2.getString(str4));
                                mediaModel.setVideo(jSONObject4.getBoolean(str7));
                                mediaModel.setDisplay_url(jSONArray3.getJSONObject(0).getString(str9));
                                if (mediaModel.isVideo()) {
                                    mediaModel.setVideo_url(jSONObject4.getString(str6));
                                } else {
                                    mediaModel.setPhoto_url(jSONArray3.getJSONObject(jSONArray3.length() - 1).getString(str9));
                                }
                                mediaModel.setSelected(false);
                                arrayList.add(mediaModel);
                            }
                        } else {
                            JSONArray jSONArray4 = jSONObject2.getJSONArray(str8);
                            MediaModel mediaModel2 = new MediaModel();
                            mediaModel2.setId(jSONObject2.getString(str4));
                            mediaModel2.setPostId(jSONObject2.getString(str4));
                            mediaModel2.setVideo(jSONObject2.getBoolean(str7));
                            mediaModel2.setDisplay_url(jSONArray4.getJSONObject(0).getString(str9));
                            if (mediaModel2.isVideo()) {
                                mediaModel2.setVideo_url(jSONObject2.getString(str6));
                            } else {
                                mediaModel2.setPhoto_url(jSONArray4.getJSONObject(jSONArray4.length() - 1).getString(str9));
                            }
                            mediaModel2.setSelected(false);
                            arrayList.add(mediaModel2);
                        }
                        post.setMedia(arrayList);
                        downloadDialog = new DownloadDialog(MainActivity.this, post);
                        downloadDialog.setOnSelectListener(new OnSelectListener() {
                            public void onConfirm(PostModel postModel) {
                                MainActivity mainActivity = MainActivity.this;
                                mainActivity.downloadProgressDialog = new DownloadProgressDialog(mainActivity);
                                if (!isFinishing()) {
                                    downloadProgressDialog.show();
                                }
                                downloadFile(postModel, 0, Boolean.valueOf(false), 0);
                            }
                        });
                        if (!isFinishing()) {
                            downloadDialog.show();
                        }
                    } catch (Exception e) { }

                } else {
                    urlNotValid();
                }
                dismissProgressDialog();
            }
        });
    }

    private int getStatusBarHeight() {
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", AbstractSpiCall.ANDROID_CLIENT_TYPE);
        if (identifier > 0) {
            return getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }


    public String getUrlWithoutParameters(String str) throws URISyntaxException {
        URI uri = new URI(str);
        URI uri2 = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, uri.getFragment());
        return uri2.toString();
    }

    public static boolean hasPermissions(Context context, String... strArr) {
        if (!(context == null || strArr == null)) {
            for (String checkSelfPermission : strArr) {
                if (ContextCompat.checkSelfPermission(context, checkSelfPermission) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initUI() {
        cardMain = (CardView) findViewById(R.id.cardMain);

//        Animation shake;
//        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mtrl_bottom_sheet_slide_in);
//        cardMain.startAnimation(shake);

        llHome = (LinearLayout) findViewById(R.id.llHome);
        llSettings = (LinearLayout) findViewById(R.id.llSettings);
        llHelp = (LinearLayout) findViewById(R.id.llHelp);
        llAutoDownload = (LinearLayout) findViewById(R.id.llAutoDownload);
        layoutDownload = (LinearLayout) findViewById(R.id.layoutDownload);
        imgHome = (ImageView) findViewById(R.id.imgHome);
        imgSettings = (ImageView) findViewById(R.id.imgSettings);
        imgHelp = (ImageView) findViewById(R.id.imgHelp);
        imgDownload = (ImageView) findViewById(R.id.imgDownload);
        imgRate = (ImageView) findViewById(R.id.imgRate);
        imgInstagram = (ImageView) findViewById(R.id.imgInstagram);
        txtHome = (TextView) findViewById(R.id.txtHome);
        txtSettings = (TextView) findViewById(R.id.txtSettings);
        txtHelp = (TextView) findViewById(R.id.txtHelp);
        txtRate = (TextView) findViewById(R.id.txtRate);
        txtInstagram = (TextView) findViewById(R.id.txtInstagram);
        edtPostUrl = (EditText) findViewById(R.id.edtPostUrl);
        plAutoDownload = (PulsatorLayout) findViewById(R.id.plAutoDownload);
        rvPosts = (ObservableRecyclerView) findViewById(R.id.rvPosts);
        rvPosts.setScrollViewCallbacks(this);
        changeAdapter(1);
//        if (VERSION.SDK_INT < 21) {
//            LayoutParams layoutParams = new LayoutParams(-2, -2);
//            layoutParams.setMargins(-((int) dpToFloat(10.0f)), -((int) dpToFloat(50.0f)), -((int) dpToFloat(10.0f)), 0);
//            cardMain.setLayoutParams(layoutParams);
//        }
        cardMain.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                cardMain.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                MainActivity mainActivity = MainActivity.this;
                mainActivity.cardMainHeight = mainActivity.cardMain.getHeight();
                String str = "English";
                String str2 = "language";
                if (prefs.getString(str2, str).equals("Persian") || prefs.getString(str2, str).equals("Arabic")) {
                    rvPosts.setPadding((int) dpToFloat(8.0f), cardMainHeight - ((int) dpToFloat(40.0f)), (int) dpToFloat(8.0f), (int) dpToFloat(6.0f));
                } else {
                    rvPosts.setPadding(0, cardMainHeight - ((int) dpToFloat(40.0f)), (int) dpToFloat(16.0f), (int) dpToFloat(6.0f));
                }
            }
        });
        if (!prefs.getBoolean("rated", false)) {
            txtRate.setVisibility(0);
            txtRate.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake));
        }
        deactivateTabs();
        llHome.setBackgroundResource(R.drawable.bg_home_active);
        imgHome.setImageResource(R.drawable.ic_downloads_active);
        txtHome.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorHomeTab));
        llHome.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                deactivateTabs();
                llHome.setBackgroundResource(R.drawable.bg_home_active);
                imgHome.setImageResource(R.drawable.ic_downloads_active);
                txtHome.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorHomeTab));
                MainActivity mainActivity = MainActivity.this;
                mainActivity.changeAdapter(mainActivity.postViewMode);
            }
        });
        settingsAdapter = new SettingsAdapter(this);
        llSettings.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                deactivateTabs();
                llSettings.setBackgroundResource(R.drawable.bg_settings_active);
                imgSettings.setImageResource(R.drawable.ic_settings_active);
                txtSettings.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSettingsTab));
                rvPosts.setPadding(0, cardMainHeight - ((int) dpToFloat(40.0f)), 0, 0);
                rvPosts.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                rvPosts.setAdapter(settingsAdapter);
            }
        });
        helpAdapter = new HelpAdapter(help);
        llHelp.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                deactivateTabs();
                llHelp.setBackgroundResource(R.drawable.bg_help_active);
                imgHelp.setImageResource(R.drawable.ic_help_active);
                txtHelp.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorHelpTab));
                rvPosts.setPadding(0, cardMainHeight - ((int) dpToFloat(40.0f)), 0, (int) dpToFloat(11.0f));
                rvPosts.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                help.clear();
//                help.add(new HelpModel().setImage(R.drawable.help_first).setHelp(getString(R.string.help1)));
//                help.add(new HelpModel().setImage(R.drawable.help_second).setHelp(getString(R.string.help1)));
//                help.add(new HelpModel().setImage(R.drawable.help_third).setHelp(getString(R.string.help1)));
                rvPosts.setAdapter(helpAdapter);
            }
        });
        llAutoDownload.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String str = "auto_download";
                if (prefs.getBoolean(autoDownloadFlag, true)) {
                    editor.putBoolean(autoDownloadFlag, false).commit();
                    stopAutoDownloadService();
                }
                else{
                    editor.putBoolean(autoDownloadFlag, true).commit();
                    startAutoDownloadService();
                }

//                if (prefs.getBoolean("open_instagram", true)) {
//                    Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
//                    if (launchIntentForPackage == null) {
//                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://instagram.com/")));
//                        return;
//                    }
//                    startActivity(launchIntentForPackage);
//                }
            }
        });
        layoutDownload.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    if (edtPostUrl.getText().toString().isEmpty()) {
                        urlNotValid();
                    } else {
                        getPostInfo(getUrlWithoutParameters(edtPostUrl.getText().toString()));
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
        edtPostUrl.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    layoutDownload.setBackgroundResource(R.drawable.bg_download_btn_active);
                } else {
                    layoutDownload.setBackgroundResource(R.drawable.bg_download_btn_inactive);
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        imgRate.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StringBuilder sb = new StringBuilder();
                sb.append("market://details?id=");
                sb.append(getApplicationContext().getPackageName());
                String str = "android.intent.action.VIEW";
                Intent intent = new Intent(str, Uri.parse(sb.toString()));
                intent.addFlags(1208483840);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException unused) {
                    MainActivity mainActivity = MainActivity.this;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("http://play.google.com/store/apps/details?id=");
                    sb2.append(getApplicationContext().getPackageName());
                    mainActivity.startActivity(new Intent(str, Uri.parse(sb2.toString())));
                }
                editor.putBoolean("rated", true).apply();
                txtRate.clearAnimation();
                txtRate.setVisibility(4);
            }
        });
        imgInstagram.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                editor.putBoolean("instagram_opened", true).apply();
                txtInstagram.clearAnimation();
                txtInstagram.setVisibility(4);
                Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                if (launchIntentForPackage == null) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://instagram.com/")));
                    return;
                }
                startActivity(launchIntentForPackage);
            }
        });
    }

    private boolean networkAvailable() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) Objects.requireNonNull((ConnectivityManager) getSystemService("connectivity"))).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void startAutoDownloadService() {
        llAutoDownload.setBackgroundResource(R.drawable.bg_auto_download_active);
        plAutoDownload.setVisibility(0);
        plAutoDownload.start();
        startService(new Intent(this, ClipBoardService.class));
    }


    public void stopAutoDownloadService() {
        llAutoDownload.setBackgroundResource(R.drawable.bg_auto_download_inactive);
        plAutoDownload.setVisibility(4);
        plAutoDownload.stop();
        stopService(new Intent(this, ClipBoardService.class));
    }


    public void urlNotValid() {
        App.WarningToast(getString(R.string.invalid_post_link)).show();
    }


    public void attachBaseContext(Context context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);
        prefs = new SecurePreferences(this);
        editor = prefs.edit();
        editor.apply();
        String str = "language";
        String str2 = "English";
        String str3 = "en";
//        if (prefs.getString(str, str2).equals(str2)) {
//            setTheme(R.style.AppTheme);
//        } else if (prefs.getString(str, str2).equals("Persian")) {
//            setTheme(R.style.AppThemePersian);
//            str3 = "fa";
//        } else if (prefs.getString(str, str2).equals("Finnish")) {
//            setTheme(R.style.AppTheme);
//            str3 = "fi";
//        } else if (prefs.getString(str, str2).equals("Turkish")) {
//            setTheme(R.style.AppTheme);
//            str3 = "tr";
//        } else if (prefs.getString(str, str2).equals("Arabic")) {
//            setTheme(R.style.AppThemePersian);
//            str3 = "ar";
//        } else if (prefs.getString(str, str2).equals("Russian")) {
//            setTheme(R.style.AppTheme);
//            str3 = "ru";
//        }
//        Resources resources = getResources();
//        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
//        Configuration configuration = resources.getConfiguration();
//        configuration.setLocale(new Locale(str3.toLowerCase()));
//        resources.updateConfiguration(configuration, displayMetrics);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String str4 = "first_time";
        if (!defaultSharedPreferences.getBoolean(str4, false)) {
            showTour();
            Editor edit = defaultSharedPreferences.edit();
            edit.putBoolean(str4, true);
            edit.apply();
        }

        final String[] strArr = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
        if (!hasPermissions(this, strArr)) {
            new MessageDialog(this).setTitle(getString(R.string.permission)).setMessage(getString(R.string.request_permission)).setConfirmText(getString(R.string.ok)).setCancelText(getString(R.string.cancel)).setOnActionListener(new OnActionListener() {
                public void onCancel() {
                    finish();
                }

                public void onConfirm() {
                    ActivityCompat.requestPermissions(MainActivity.this, strArr, 1);
                }
            }).show();
        }
        initUI();

        if (prefs.getBoolean(autoDownloadFlag, true)) {
            startAutoDownloadService();
        } else {
            stopAutoDownloadService();
        }
        PRDownloader.initialize(getApplicationContext(), PRDownloaderConfig.newBuilder().setDatabaseEnabled(true).build());
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("from_service", false) && prefs.getBoolean(autoDownloadFlag, true)) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService("clipboard");
            if (clipboardManager != null && clipboardManager.hasPrimaryClip() && ((ClipDescription) Objects.requireNonNull(clipboardManager.getPrimaryClipDescription())).hasMimeType(HTTP.PLAIN_TEXT_TYPE)) {
                String charSequence = ((ClipData) Objects.requireNonNull(clipboardManager.getPrimaryClip())).getItemAt(0).getText().toString();
                if (charSequence.matches("https://www.instagram.com/p/(.*)")) {
                    try {
                        getPostInfo(getUrlWithoutParameters(charSequence.trim()));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void onDestroy() {
        dismissProgressDialog();
        dismissDownloadProgressDialog();
        super.onDestroy();
    }

    public void onDownMotionEvent() {
    }


    public void onPause() {
        super.onPause();
        DownloadDialog downloadDialog2 = downloadDialog;
        if (downloadDialog2 != null && downloadDialog2.isShowing()) {
            downloadDialog.dismiss();
        }
    }

    public void onRequestPermissionsResult(int i, @NotNull String[] strArr, @NotNull int[] iArr) {
        if (i == 1 && iArr.length > 0) {
            for (String str : strArr) {
                if (iArr[0] == -1) {
                    finish();
                }
            }
        }
    }


    public void onResume() {
        super.onResume();
        App.setRunningActivity(this);
    }

    public void onScrollChanged(int i, boolean z, boolean z2) {
        int i2;
        int i3;
        if (VERSION.SDK_INT < 21) {
            i2 = (cardMainHeight - ((int) dpToFloat(40.0f))) - getStatusBarHeight();
            i3 = 0;
        } else {
            i2 = cardMainHeight - ((int) dpToFloat(40.0f));
            i3 = 10;
        }
        int i4 = i + i2;
        if (i4 <= 0) {
            i4 = 0;
        }
        int i5 = cardMainHeight;
        if (i5 != 0) {
            float f = (float) i4;
            float f2 = (1.0f - (f / ((float) i5))) * 40.0f;
            if (f2 >= 0.0f && f2 <= 40.0f) {
                cardMain.setRadius(dpToFloat(f2));
            }
            if (i4 <= i2 - getActionBarSize()) {
                ViewHelper.setTranslationY(cardMain, (float) (-i4));
            }
            float f3 = (float) i3;
            if (f >= ((float) (i2 - getActionBarSize())) - dpToFloat(f3)) {
                cardMain.setRadius(dpToFloat(0.0f));
                ViewHelper.setTranslationY(cardMain, -(((float) (i2 - 0)) - dpToFloat(f3)));
            }
        }
    }

    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    public void showTour() {
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.addRule(12);
        layoutParams.addRule(9);
        int intValue = Float.valueOf(getResources().getDisplayMetrics().density * 12.0f).intValue();
        layoutParams.setMargins(intValue, intValue, intValue, intValue);
        p = new ShowcaseView.Builder(this).withMaterialShowcase().setTarget(new ViewTarget(R.id.imgInstagram, this)).setContentTitle((int) R.string.tour_main_title).setContentText((int) R.string.tour_main_text).setStyle(R.style.CustomShowcaseTheme2).replaceEndButton((int) R.layout.view_custom_button).hideOnTouchOutside().build();
        p.setButtonPosition(layoutParams);
    }
}
