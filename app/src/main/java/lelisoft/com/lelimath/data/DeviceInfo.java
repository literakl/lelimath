package lelisoft.com.lelimath.data;

import java.util.Locale;

public class DeviceInfo {

    private final static String NEW_LINE = "\n";
    private final static String NEW_PARAGRAPH = NEW_LINE + NEW_LINE;

    private final ApplicationInfo appInfo;
    private final HardwareInfo hardwareInfo;
    private final OSInfo osInfo;
    private final ScreenInfo screenInfo;

    public DeviceInfo(ApplicationInfo appInfo, HardwareInfo hardwareInfo, OSInfo osInfo, ScreenInfo screenInfo) {
        this.appInfo = appInfo;
        this.hardwareInfo = hardwareInfo;
        this.osInfo = osInfo;
        this.screenInfo = screenInfo;
    }

    @Override
    public String toString() {
        return new StringBuilder(1000)
            .append(appInfo == null ? "Application info could not be obtained" : appInfo)
            .append(NEW_PARAGRAPH)
            .append(hardwareInfo)
            .append(NEW_PARAGRAPH)
            .append(osInfo)
            .append(NEW_PARAGRAPH)
            .append(screenInfo)
            .toString();
    }

    public static class ApplicationInfo {
        private final int versionCode;
        private final String versionName;
        private final String buildNumber;

        public ApplicationInfo(int versionCode, String versionName, String buildNumber) {
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.buildNumber = buildNumber;
        }

        @Override
        public String toString() {
            String name = "Application Info";
            StringBuilder s = new StringBuilder(250);

            s.append(name).append(NEW_LINE);

            for (int i = 0; i < name.length(); i++) {
                s.append('=');
            }

            s.append(NEW_LINE)
                .append("Application version code:     ").append(versionCode).append(NEW_LINE)
                .append("Application version name:     ").append(versionName).append(NEW_LINE)
                .append("Application build date:       ").append(buildNumber).append(NEW_LINE);

            return s.toString();
        }
    }

    public static class HardwareInfo {
        private final String board;
        private final String brand;
        private final String device;
        private final String model;
        private final String product;
        private final String tags;

        public HardwareInfo(String board, String brand, String device, String model, String product, String tags) {
            this.board = board;
            this.brand = brand;
            this.device = device;
            this.model = model;
            this.product = product;
            this.tags = tags;
        }

        @Override
        public String toString() {
            String name = "Hardware Info";
            StringBuilder s = new StringBuilder(200);
            s.append(name).append(NEW_LINE);

            for (int i = 0; i < name.length(); i++) {
                s.append('=');
            }

            s.append(NEW_LINE)
                .append("Board:   ").append(board).append(NEW_LINE)
                .append("Brand:   ").append(brand).append(NEW_LINE)
                .append("Device:  ").append(device).append(NEW_LINE)
                .append("Model:   ").append(model).append(NEW_LINE)
                .append("Product: ").append(product).append(NEW_LINE)
                .append("Tags:    ").append(tags).append(NEW_LINE);

            return s.toString();
        }
    }

    public static class OSInfo {
        private final String buildRelease;
        private final String buildReleaseIncremental;
        private final String displayBuild;
        private final String fingerPrint;
        private final String buildID;
        private final long time;
        private final String type;
        private final String user;
        private final Locale locale;

        public OSInfo(String buildRelease, String buildReleaseIncremental, String displayBuild, String fingerPrint, String buildID, long time, String type, String user, Locale locale) {
            this.buildRelease = buildRelease;
            this.buildReleaseIncremental = buildReleaseIncremental;
            this.displayBuild = displayBuild;
            this.fingerPrint = fingerPrint;
            this.buildID = buildID;
            this.time = time;
            this.type = type;
            this.user = user;
            this.locale = locale;
        }

        @Override
        public String toString() {
            String name = "Operating System Info";
            StringBuilder s = new StringBuilder(400);
            s.append(name).append(NEW_LINE);

            for (int i = 0; i < name.length(); i++) {
                s.append('=');
            }

            s.append(NEW_LINE)
                    .append("Build Release:               ").append(buildRelease).append(NEW_LINE)
                    .append("Build Release Incremental:   ").append(buildReleaseIncremental).append(NEW_LINE)
                    .append("Display Build:               ").append(displayBuild).append(NEW_LINE)
                    .append("Finger Print:                ").append(fingerPrint).append(NEW_LINE)
                    .append("Build ID:                    ").append(buildID).append(NEW_LINE)
                    .append("Time:                        ").append(time).append(NEW_LINE)
                    .append("Type:                        ").append(type).append(NEW_LINE)
                    .append("User:                        ").append(user).append(NEW_LINE)
                    .append("Locale:                      ").append(locale).append(NEW_LINE);

            return s.toString();
        }
    }

    public static class ScreenInfo {
        private final int heightPixels;
        private final int widthPixels;
        private final float density;
        private final int densityDpi;
        private final float scaledDensity;
        private final float xdpi;
        private final float ydpi;

        public ScreenInfo(int heightPixels, int widthPixels, float density, int densityDpi, float scaledDensity, float xdpi, float ydpi) {
            this.heightPixels = heightPixels;
            this.widthPixels = widthPixels;
            this.density = density;
            this.densityDpi = densityDpi;
            this.scaledDensity = scaledDensity;
            this.xdpi = xdpi;
            this.ydpi = ydpi;
        }

        @Override
        public String toString() {
            String name = "Screen Info";
            StringBuilder s = new StringBuilder(300);
            s.append(name).append(NEW_LINE);

            for (int i = 0; i < name.length(); i++) {
                s.append('=');
            }

            s.append(NEW_LINE)
                    .append("Height Pixels:  ").append(heightPixels).append(NEW_LINE)
                    .append("Width Pixels:   ").append(widthPixels).append(NEW_LINE)
                    .append("Density:        ").append(density).append(NEW_LINE)
                    .append("Density Dpi:    ").append(densityDpi).append(NEW_LINE)
                    .append("Scaled Density: ").append(scaledDensity).append(NEW_LINE)
                    .append("X dpi:          ").append(xdpi).append(NEW_LINE)
                    .append("Y dpi:          ").append(ydpi).append(NEW_LINE);

            return s.toString();
        }
    }
}
