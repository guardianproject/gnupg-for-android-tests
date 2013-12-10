
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
        NativeHelper.setup(getContext());
        testFilePath = NativeHelper.app_test_files;
    }

    protected void tearDown() throws Exception {
        Log.i(TAG, "tearDown");
        super.tearDown();
    }

    public void testImportPublicKeys() {
        Log.i(TAG, "testImportPublicKeys");
        Log.i(TAG, "BEFORE");
        List<GPGKey> before = gpgcli.getPublicKeys();
        assertTrue("the keyring should be empty!", before.size() == 0);
        for (GPGKey key : before)
            Log.i(TAG, "key: " + key.getKeyId());
        gpgcli.importKey(new File(testFilePath, "public-keys.pkr"));
        Log.i(TAG, "AFTER");
        List<GPGKey> after = gpgcli.getPublicKeys();
        assertTrue("the keyring should have keys in it", after.size() == 12);
        for (GPGKey key : after)
            Log.i(TAG, "key: " + key.getKeyId());

    }

    public void testForKeyPresence() {
        Log.i(TAG, "testForKeyPresence");
        List<GPGKey> keys = gpgcli.getPublicKeys();
        for (GPGKey key : keys)
            if (key.getFingerprint().equals("5E61C8780F86295CE17D86779F0FE587374BBE81")) {
                assert true;
                return;
            }
        assert false;

    }
}
