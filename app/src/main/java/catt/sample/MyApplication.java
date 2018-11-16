package catt.sample;

import android.app.Application;
import catt.compat.layout.internal.TargetScreenMetrics;

public class MyApplication extends Application {
    private String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        TargetScreenMetrics.get().initContent(getApplicationContext(), "1920x1080,2048x1536");
    }
}
