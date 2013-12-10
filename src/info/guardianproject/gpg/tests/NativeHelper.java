
package info.guardianproject.gpg.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.util.Log;

public class NativeHelper {
    public static final String TAG = "NativeHelper";

    public static final String PACKAGE = "info.guardianproject.gpg";
    public static final String GPG_PATH = "/data/data/" + PACKAGE;
    public static File GNUPGHOME = null;

    public static File app_test_files;

    private static Context context;

    public static void setup(Context c) {
        context = c;
        app_test_files = context.getDir("test_files", Context.MODE_PRIVATE).getAbsoluteFile();

        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                if (filename != null && filename.startsWith("uid=100"))
                    return true;
                else
                    return false;
            }
        };

        final PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(PACKAGE, PackageManager.GET_META_DATA);
            String uid = String.valueOf(appInfo.uid);
            Log.v(TAG, "uid: " + uid);
            Process getId = Runtime.getRuntime().exec("id");
            InputStream in = getId.getInputStream();
            getId.waitFor();
            byte[] buffer = new byte[50];
            int count = in.read(buffer);
            String id = null;
            if (count > 0) {
                Log.i(TAG, "id: " + new String(buffer));
                id = new String(buffer).split(" ")[0];
            }

            // delete the GNUPGHOME folder from previous tests
            NativeHelper.GNUPGHOME = new File(GPG_PATH, "app_gnupghome/" + id);
            if (NativeHelper.GNUPGHOME.isDirectory())
                Log.v(TAG, "found GNUPGHOME: " + NativeHelper.GNUPGHOME);
            else
                Log.e(TAG, NativeHelper.GNUPGHOME + " is not a directory I can read!");
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (GNUPGHOME != null)
            try {
                FileUtils.deleteDirectory(NativeHelper.GNUPGHOME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        Log.i(TAG, "Finished NativeHelper.setup()");
    }

    private static void copyFileOrDir(String path, File dest) {
        AssetManager assetManager = context.getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path, dest);
            } else {
                File destdir = new File(dest, new File(path).getName());
                if (!destdir.exists())
                    destdir.mkdirs();
                for (int i = 0; i < assets.length; ++i) {
                    copyFileOrDir(new File(path, assets[i]).getPath(), destdir);
                }
            }
        } catch (IOException ex) {
            Log.e(TAG, "I/O Exception", ex);
        }
    }

    private static void copyFile(String filename, File dest) {
        AssetManager assetManager = context.getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            out = new FileOutputStream(new File(app_test_files, filename).getAbsolutePath());

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e(TAG, filename + ": " + e.getMessage());
        }

    }

    public static void unpackAssets() {
        Log.i(TAG, "Setting up assets in " + app_test_files);
        try {
            FileUtils.deleteDirectory(app_test_files);
            app_test_files.mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AssetManager am = context.getAssets();
        final String[] assetList;
        try {
            assetList = am.list("");
        } catch (IOException e) {
            Log.e(TAG, "cannot get asset list", e);
            return;
        }
        // unpack the assets to app_test_files
        for (String asset : assetList) {
            if (asset.equals("images")
                    || asset.equals("sounds")
                    || asset.equals("webkit")
                    || asset.equals("databases") // Motorola
                    || asset.equals("kioskmode")) // Samsung
                continue;
            Log.i(TAG, "copying asset: " + asset);
            copyFileOrDir(asset, app_test_files);
        }
    }
}
