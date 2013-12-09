package com.lookout.keyswap.gpg;

import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GPGCli implements GPGBinding {

    private static GPGCli instance;

    private final String GPG_PATH = "gpg2";

    public static GPGCli getInstance() {
        if(instance == null) {
            instance = new GPGCli();
        }
        return instance;
    }

    private GPGCli() {
        Log.i("KeySwap", "GPGCli initialized");
    }

    public GPGKey getPublicKey(String keyId) {
        String rawList = Exec(GPG_PATH, "--with-colons", "--with-fingerprint", "--list-keys", keyId);
        Log.i("KeySwap", "Got public key: " + keyId);

        Scanner scanner = new Scanner(rawList);
        GPGKey key = parseKey(scanner, "pub:.*");
        scanner.close();

        return key;
    }

    public GPGKey getSecretKey(String keyId) {
        String rawList = Exec(GPG_PATH, "--with-colons", "--with-fingerprint", "--list-secret-keys", keyId);
        Log.i("KeySwap", "Got secret key: " + keyId);

        Scanner scanner = new Scanner(rawList);
        GPGKey key = parseKey(scanner, "sec:.*");
        scanner.close();

        return key;
    }

    public ArrayList<GPGKey> getPublicKeys() {
        String rawList = Exec(GPG_PATH, "--with-colons", "--with-fingerprint", "--list-keys");
        Log.i("KeySwap", "Got public keys: " + rawList);

        ArrayList<GPGKey> keys = new ArrayList<GPGKey>();
        Scanner scanner = new Scanner(rawList);
        GPGKey key;
        while((key = parseKey(scanner, "pub:.*")) != null) {
            keys.add(key);
        }
        scanner.close();

        return keys;
    }

    public ArrayList<GPGKey> getSecretKeys() {
        String rawList = Exec(GPG_PATH, "--with-colons", "--with-fingerprint", "--list-secret-keys");
        Log.i("KeySwap", "Got secret keys: " + rawList);

        ArrayList<GPGKey> keys = new ArrayList<GPGKey>();
        Scanner scanner = new Scanner(rawList);
        GPGKey key;
        while((key = parseKey(scanner, "sec:.*")) != null) {
            keys.add(key);
        }
        scanner.close();

        return keys;
    }

    public ArrayList<GPGKeyPair> getKeyPairs() {
        ArrayList<GPGKeyPair> keyPairs = new ArrayList<GPGKeyPair>();

        ArrayList<GPGKey> secretKeys = this.getPublicKeys();
        for(GPGKey secretKey : secretKeys) {
            GPGKey publicKey = this.getPublicKey(secretKey.getKeyId());
            GPGKeyPair keyPair = new GPGKeyPair(publicKey, secretKey);
            keyPairs.add(keyPair);
        }

        return keyPairs;
    }

    private GPGKey parseKey(Scanner scanner, String keyDelimiter) {
        Pattern keyRegex = Pattern.compile(keyDelimiter);

        if(scanner.hasNextLine() && !scanner.hasNext(keyRegex)) {
            scanner.nextLine();
        }

        if(!scanner.hasNextLine()) {
            return null;
        }

        String line = scanner.nextLine();
        GPGRecord parentKey = GPGRecord.FromColonListingFactory(line);
        GPGKey key = new GPGKey(parentKey);

        while(scanner.hasNextLine() && !scanner.hasNext(keyRegex)) {
            GPGRecord subRecord = GPGRecord.FromColonListingFactory(scanner.nextLine());
            switch(subRecord.getType()) {
                case UserId:
                    key.addUserId(subRecord);
                    break;
                case Fingerprint:
                    //Fingerprint records use the userId field as the fingerprint
                    key.setFingerprint(subRecord.getUserId());
                    break;
                default:
                    key.addSubKey(subRecord);
                    break;
            }
        }

        return key;
    }

    public void signKey(String fingerprint, TrustLevel trustLevel) {
        int gpgTrustLevel = 1;
        switch(trustLevel) {
            case New:
                gpgTrustLevel = 1;
                break;
            case None:
                gpgTrustLevel = 2;
            case Marginal:
                gpgTrustLevel = 3;
                break;
            case Full:
                gpgTrustLevel = 4;
                break;
            case Ultimate:
                gpgTrustLevel = 5;
                break;
        }

        String trustDBRecord = fingerprint + ":" + gpgTrustLevel + ":";
        try {
            String tempPath = "/sdcard/KeySwap/tempTrustDb";
            PrintWriter printWriter = new PrintWriter(tempPath);
            printWriter.print(trustDBRecord);
            printWriter.close();

            Exec(GPG_PATH, "--import-ownertrust", tempPath);
            new File(tempPath).delete();
        } catch(Exception e) {
        }

    }

    public void exportPublicKeyring(String destination) {
        String output = Exec(GPG_PATH, "--yes", "--output", destination, "--export");

        Log.i("KeySwap", "Public Keyring exported");
    }

    public void exportSecretKeyring(String destination) {
        String output = Exec(GPG_PATH, "--yes", "--output", destination, "--export-secret-keys");

        Log.i("KeySwap", "Secret Keyring exported");
    }

    public void exportKey(String destination, String keyId) {
        String outputPath = new File(destination, keyId + ".gpg").getAbsolutePath();
        Exec(GPG_PATH, "--yes", "--output", outputPath, "--export-secret-keys", keyId);

        Log.i("KeySwap", keyId + " exported to " + outputPath);
    }

    public void importKey(String source) {
        Exec(GPG_PATH, "--yes", "--allow-secret-key-import", "--import", source);

        Log.i("KeySwap", source + " imported");
    }

    public void pushToKeyServer(String server, String keyId) {
        Exec(GPG_PATH, "--yes", "--key-server", server, "--send-key", keyId);

        Log.i("KeySwap", keyId + " pushed to " + server);
    }

    public String exportAsciiArmoredKey(String keyId) {
        String output = Exec(GPG_PATH, "--armor", "--export", keyId);
        Log.i("KeySwap", keyId + " exported");

        return output;
    }

    public void importAsciiArmoredKey(String armoredKey) {
        try {
            String tempPath = "/sdcard/KeySwap/tempArmoredKey.asc";
            PrintWriter printWriter = new PrintWriter(tempPath);
            printWriter.print(armoredKey);
            printWriter.close();

            Exec(GPG_PATH, "--yes", "--import", tempPath);
            new File(tempPath).delete();
        } catch(Exception e) {
        }
    }

    private String Exec(String... command) {
        String rawOutput = "";
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Map<String, String> environment = pb.environment();
            environment.put("PATH", environment.get("PATH") + ":/data/data/info.guardianproject.gpg/app_opt/aliases");
            environment.put("LD_LIBRARY_PATH", environment.get("LD_LIBRARY_PATH") + ":/data/data/info.guardianproject.gpg/app_opt/lib:/data/data/info.guardianproject.gpg/lib");
            Process p = pb.start();
            p.waitFor();
            rawOutput = getProcessOutput(p);
        } catch(IOException e) {
            Log.e("KeySwap", e.getMessage());
        } catch (InterruptedException e) {
            Log.e("KeySwap", e.getMessage());
        }
        return rawOutput;
    }

    private String getProcessOutput(Process p) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = input.readLine()) != null) {
            sb.append(line + "\n");
        }
        input.close();

        return sb.toString();
    }
}
