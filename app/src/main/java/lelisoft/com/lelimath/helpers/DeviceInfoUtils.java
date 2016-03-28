package lelisoft.com.lelimath.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import lelisoft.com.lelimath.R;
import lelisoft.com.lelimath.data.DeviceInfo;

public class DeviceInfoUtils {
    private static final Logger log = LoggerFactory.getLogger(DeviceInfoUtils.class);

    public static DeviceInfo getDeviceInfo(Context c) {
        DeviceInfo.ApplicationInfo appInfo = null;
        try {
            PackageInfo info = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            int versionCode = info.versionCode;
            String versionName = info.versionName;
            String buildNumber = c.getString(R.string.app_build_number);
            appInfo = new DeviceInfo.ApplicationInfo(versionCode, versionName, buildNumber);
        } catch (PackageManager.NameNotFoundException e) {
            log.error("Unable to get package info for '" + c.getPackageName() + "'", e);
        }


        // ** Device:
        String board = Build.BOARD;
        String brand = Build.BRAND;
        String device = Build.DEVICE;
        String model = Build.MODEL;
        String product = Build.PRODUCT;
        String tags = Build.TAGS;

        DeviceInfo.HardwareInfo hardwareInfo = new DeviceInfo.HardwareInfo(board, brand, device, model, product, tags);

        // ** OS
        String buildRelease = Build.VERSION.RELEASE;
        String buildReleaseIncremental =  Build.VERSION.INCREMENTAL;
        String displayBuild = Build.DISPLAY;
        String fingerPrint = Build.FINGERPRINT;
        String buildID = Build.ID;
        long time = Build.TIME;
        String type = Build.TYPE;
        String user = Build.USER;
        Locale locale = c.getResources().getConfiguration().locale;

        DeviceInfo.OSInfo osInfo = new DeviceInfo.OSInfo(buildRelease, buildReleaseIncremental, displayBuild, fingerPrint, buildID, time, type, user, locale);

        // ** Density:
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        int densityDpi = metrics.densityDpi;
        float scaledDensity = metrics.scaledDensity;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;

        // ** Screen
        Point size = new Point();
        int heightPixels;
        int widthPixels;
        wm.getDefaultDisplay().getSize(size);
        heightPixels = size.y;
        widthPixels = size.x;
        DeviceInfo.ScreenInfo screenInfo = new DeviceInfo.ScreenInfo(heightPixels, widthPixels, density, densityDpi, scaledDensity, xdpi, ydpi);

        return new DeviceInfo(appInfo, hardwareInfo, osInfo, screenInfo);
    }
}
