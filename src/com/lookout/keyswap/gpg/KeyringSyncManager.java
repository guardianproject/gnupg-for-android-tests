package com.lookout.keyswap.gpg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class KeyringSyncManager {

    private static KeyringSyncManager instance;
    private static final String KEY_STORE_DIRECTORY = "/sdcard/KeySwap/";

    private String storagePath;

    public static KeyringSyncManager getInstance() {
        if(instance == null) {
            instance = new KeyringSyncManager();
        }
        return instance;
    }

    private KeyringSyncManager() {
        File file = new File(KEY_STORE_DIRECTORY);
        file.mkdirs();

        this.storagePath = file.getAbsolutePath();

        Log.i("KeySwap", "KeyringSyncManager initialized");
    }

    public void sync(Context context) {
        this.importKeys();
        this.exportPublicKeyring();
        this.exportSecretKeyring();

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + storagePath)));
    }

    public void exportPublicKeyring() {
        GPGCli.getInstance().exportPublicKeyring(new File(storagePath, "/PublicKeyring").getAbsolutePath());
    }

    public void exportSecretKeyring() {
        GPGCli.getInstance().exportSecretKeyring(new File(storagePath, "/SecretKeyring").getAbsolutePath());
    }

    public void importKeys() {
        for(File file : new File(storagePath).listFiles()) {
            if(file.getName().equals("PublicKeyring") || file.getName().equals("SecretKeyring")) {
                continue;
            }
            GPGCli.getInstance().importKey(file.getAbsolutePath());
            file.delete();
        }
    }
}
