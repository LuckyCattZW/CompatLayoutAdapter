package catt.sample;

import android.app.Application;
import android.util.Log;
import catt.compat.layout.internal.TargetScreenMetrics;

public class MyApplication extends Application {
    private String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        TargetScreenMetrics.TargetStatic.get().initContent(getApplicationContext());
    }
}
