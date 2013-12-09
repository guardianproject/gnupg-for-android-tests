package com.lookout.keyswap.gpg;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GPGFactory {
    public static ArrayList<Map<String, String>> keys;

    public static String signedKey;
    public static String publicKey, publicKeyId;
    public static String receivedKey, receivedKeyId;

    public static ArrayList<Map<String, String>> getKeys() {
        return keys;
    }

    public static void buildKeyPairList() {
        keys = new ArrayList<Map<String, String>>();
        ArrayList<GPGKeyPair> keyPairs = GPGCli.getInstance().getKeyPairs();

        for(GPGKeyPair keyPair : keyPairs) {
            GPGKey key = keyPair.getPublicKey();
            keys.add(putData(key.getPrimaryKeyId().getPersonalName(),
                             key.getPrimaryKeyId().getEmail(),
                             key.getShortId()));
        }
    }

    public static void buildPublicKeyList() {
        keys = new ArrayList<Map<String, String>>();
        ArrayList<GPGKey> publicKeys = GPGCli.getInstance().getPublicKeys();

        for(GPGKey key : publicKeys) {
            keys.add(putData(key.getPrimaryKeyId().getPersonalName(),
                             key.getPrimaryKeyId().getEmail(),
                             key.getShortId()));
        }
    }

    public static void buildPrivateKeyList() {
        keys = new ArrayList<Map<String, String>>();
        ArrayList<GPGKey> privateKeys = GPGCli.getInstance().getSecretKeys();

        for(GPGKey key : privateKeys) {
            keys.add(putData(key.getPrimaryKeyId().getPersonalName(),
                    key.getPrimaryKeyId().getEmail(),
                    key.getShortId()));
        }
    }

    public static HashMap<String, String> putData(String personalName, String email, String shortId) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("full_name", personalName);
        item.put("email", email);
        item.put("short_id", shortId);
        return item;
    }


    public static HashMap<String, String> getKeyByKeyId(String key_id) {

        HashMap<String, String> x = new HashMap<String, String>();

        return x;
    }

    public static void setSignedKey(String key) {
        signedKey = key;
    }

    public static String getSignedKey() {
        if(signedKey == null)
            return "";

        return signedKey;
    }

    public static void setReceivedKey(String key, String key_id) {
        receivedKey = key;
        receivedKeyId = key_id;
        GPGCli.getInstance().importAsciiArmoredKey(key);
        buildKeyPairList();
    }

    public static String getReceivedKey() {
        if(receivedKey == null) {
            return "";
        }

        return receivedKey;
    }

    public static String getReceivedKeyId() {
        if(receivedKeyId == null) {
            return "";
        }

        return receivedKeyId;
    }

    public static void signReceivedKey(String trustLevel) {

    }

    public static void setPublicKey(String pgp_key_id) {
        publicKeyId = pgp_key_id;
        publicKey = GPGCli.getInstance().exportAsciiArmoredKey(publicKeyId);

        Log.i("KeyMaster", publicKey);
    }

    public static String getPublicKey() {
        if(publicKey != null) {
            return publicKey;
        } else {
            return "";
        }
    }

    public static String getPublicKeyId() {
        if(publicKeyId != null) {
            return publicKeyId;
        } else {
            return "";
        }
    }
}
