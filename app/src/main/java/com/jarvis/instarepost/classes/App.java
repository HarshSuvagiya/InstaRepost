package com.jarvis.instarepost.classes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import com.jarvis.instarepost.R;
import com.securepreferences.SecurePreferences;
import io.github.inflationx.calligraphy3.CalligraphyConfig.Builder;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import java.lang.ref.WeakReference;

public class App extends MultiDexApplication {
    public static LayoutInflater inflater;
    private static Application instance;
    private static SharedPreferences prefs;
    private static WeakReference<Activity> runningActivity;

    public static Toast FailToast(String str) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.layout_toast, null).findViewById(R.id.rlToast);
        ImageView imageView = (ImageView) viewGroup.findViewById(R.id.imgToast);
        TextView textView = (TextView) viewGroup.findViewById(R.id.txtToastMessage);
        viewGroup.setBackgroundResource(R.drawable.bg_toast_fail);
        imageView.setImageResource(R.drawable.ic_error);
        textView.setText(str);
        String str2 = "language";
        String str3 = "English";
        String str4 = "fonts/rubik.ttf";
        if (!prefs.getString(str2, str3).equals(str3) && prefs.getString(str2, str3).equals("Persian")) {
            str4 = "fonts/vazir.ttf";
        }
        textView.setTypeface(Typeface.createFromAsset(getContext().getAssets(), str4));
        Toast toast = new Toast(getContext());
        toast.setDuration(1);
        toast.setGravity(80, 0, (int) dpToFloat(48.0f));
        toast.setView(viewGroup);

//        Toast toast1 = Toast.makeText(getContext(),
//                str,
//                Toast.LENGTH_SHORT);

        return toast;
    }

    public static Toast SuccessToast(String str) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.layout_toast, null).findViewById(R.id.rlToast);
        TextView textView = (TextView) viewGroup.findViewById(R.id.txtToastMessage);
        textView.setText(str);
        String str2 = "language";
        String str3 = "English";
        String str4 = "fonts/rubik.ttf";
        if (!prefs.getString(str2, str3).equals(str3) && prefs.getString(str2, str3).equals("Persian")) {
            str4 = "fonts/vazir.ttf";
        }
        textView.setTypeface(Typeface.createFromAsset(getContext().getAssets(), str4));
        Toast toast = new Toast(getContext());
        toast.setDuration(1);
        toast.setGravity(80, 0, (int) dpToFloat(48.0f));
        toast.setView(viewGroup);

//        Toast toast1 = Toast.makeText(getContext(),
//                str,
//                Toast.LENGTH_SHORT);

        return toast;
    }

    public static Toast WarningToast(String str) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.layout_toast, null).findViewById(R.id.rlToast);
        ImageView imageView = (ImageView) viewGroup.findViewById(R.id.imgToast);
        TextView textView = (TextView) viewGroup.findViewById(R.id.txtToastMessage);
        viewGroup.setBackgroundResource(R.drawable.bg_toast_warning);
        imageView.setImageResource(R.drawable.ic_warning);
        textView.setText(str);
        String str2 = "language";
        String str3 = "English";
        String str4 = "fonts/rubik.ttf";
        if (!prefs.getString(str2, str3).equals(str3) && prefs.getString(str2, str3).equals("Persian")) {
            str4 = "fonts/vazir.ttf";
        }
        textView.setTypeface(Typeface.createFromAsset(getContext().getAssets(), str4));
        Toast toast = new Toast(getContext());
        toast.setDuration(1);
        toast.setGravity(80, 0, (int) dpToFloat(48.0f));
        toast.setView(viewGroup);

//        Toast toast1 = Toast.makeText(getContext(),
//                str,
//                Toast.LENGTH_SHORT);

        return toast;
    }

    public static float dpToFloat(float f) {
        return TypedValue.applyDimension(1, f, getContext().getResources().getDisplayMetrics());
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static Activity getRunningActivity() {
        WeakReference<Activity> weakReference = runningActivity;
        if (weakReference == null || weakReference.get() == null) {
            return null;
        }
        return (Activity) runningActivity.get();
    }

    public static void setRunningActivity(Activity activity) {
        runningActivity = new WeakReference<>(activity);
    }


    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        inflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
        prefs = new SecurePreferences(this);
        String str = "language";
        String str2 = "English";
        String str3 = "fonts/rubik.ttf";
        if (!prefs.getString(str, str2).equals(str2) && prefs.getString(str, str2).equals("Persian")) {
            str3 = "fonts/vazir.ttf";
        }
        ViewPump.init(ViewPump.builder().addInterceptor(new CalligraphyInterceptor(new Builder().setDefaultFontPath(str3).setFontAttrId(R.attr.fontPath).build())).build());
    }
}
