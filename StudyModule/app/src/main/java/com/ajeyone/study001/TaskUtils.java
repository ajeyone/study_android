package com.ajeyone.study001;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

public class TaskUtils {
    private static String[] S = {"standard", "singleTop", "singleTask", "singleInstance"};
    public static String getTaskDebugString(Activity activity) {
        try {
            ActivityInfo info = activity.getPackageManager().getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            return info.taskAffinity + "#" + activity.getTaskId() + "(" + S[info.launchMode] + ")";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
