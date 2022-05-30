package kelompok.tiga.app;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.File;

public final class Util {
    public final static String[] categories = new String[]{
            "Hewan",
            "Buah",
            "Benda",
            "Tumbuhan"
    };
    private static File path = null;

    @NonNull
    public static synchronized File getPath(@NonNull Context ctx) {
        if (path == null) {
            // try to get data directory
            var result = ActivityCompat.getDataDir(ctx);
            // try to get files directory
            if (result == null) {
                result = ctx.getFilesDir();
            }
            // try to get cache directory
            if (result == null) {
                result = ctx.getCacheDir();
            }
            path = result;
        }

        return notNull(path);
    }

    @NonNull
    public static <T> T notNull(@Nullable T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }
}
