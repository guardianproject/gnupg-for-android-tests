
package info.guardianproject.gpg.tests.junit;

import info.guardianproject.gpg.tests.NativeHelper;

import java.io.File;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.lookout.keyswap.gpg.GPGCli;
import com.lookout.keyswap.gpg.GPGKey;

public class GpgCliTests extends AndroidTestCase {
    public static final String TAG = "GpgCliTests";

    private final GPGCli gpgcli = GPGCli.getInstance();
    private File testFilePath;

    protected void setUp() throws Exception {
        Log.i(TAG, "setUp");
        super.setUp();
        testFilePath = NativeHelper.app_test_files;
    }

    protected void tearDown() throws Exception {
        Log.i(TAG, "tearDown");
        super.tearDown();
    }

    public void testImportKey() {
        Log.i(TAG, "testImportKey");
        List<GPGKey> before = gpgcli.getPublicKeys();
        Log.i(TAG, "BEFORE");
        for (GPGKey key : before)
            Log.i(TAG, "key: " + key.toString());
        gpgcli.importKey(new File(testFilePath, "public-keys.pkr"));
        Log.i(TAG, "AFTER");
        List<GPGKey> after = gpgcli.getPublicKeys();
        for (GPGKey key : after)
            Log.i(TAG, "key: " + key.toString());

    }
}
