package info.guardianproject.gpg.tests;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ApgIntentsActivity extends Activity {
	public static final String TAG = "ApgIntentsActivity";

	public static class ApgId {
		public static final String VERSION = "1";

		public static final String EXTRA_INTENT_VERSION = "intentVersion";

		public static final int DECRYPT = 0x21070001;
		public static final int ENCRYPT = 0x21070002;
		public static final int SELECT_PUBLIC_KEYS = 0x21070003;
		public static final int SELECT_SECRET_KEY = 0x21070004;
		public static final int GENERATE_SIGNATURE = 0x21070005;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apg_intents);
		wireTestButtons();
	}

	private void setOnClick(Button button, final String intentName,
			final int intentId) {
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new android.content.Intent(intentName);
				intent.putExtra(ApgId.EXTRA_INTENT_VERSION, ApgId.VERSION);
				try {
					startActivityForResult(intent, intentId);
					Toast.makeText(view.getContext(),
							"started " + intentName + " " + intentId,
							Toast.LENGTH_SHORT).show();
					Log.i(TAG, "started " + intentName + " " + intentId);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(view.getContext(),
							R.string.error_activity_not_found,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void wireTestButtons() {
		Button selectPublicKeysButton = (Button) findViewById(R.id.select_public_keys);
		setOnClick(selectPublicKeysButton, Apg.Intent.SELECT_PUBLIC_KEYS,
				ApgId.SELECT_PUBLIC_KEYS);

		Button selectSecretKeyButton = (Button) findViewById(R.id.select_secret_key);
		setOnClick(selectSecretKeyButton, Apg.Intent.SELECT_SECRET_KEY,
				ApgId.SELECT_SECRET_KEY);

		Button encryptButton = (Button) findViewById(R.id.encrypt);
		setOnClick(encryptButton, Apg.Intent.ENCRYPT, ApgId.ENCRYPT);

		Button encryptFileButton = (Button) findViewById(R.id.encrypt_file);
		setOnClick(encryptFileButton, Apg.Intent.ENCRYPT_FILE, ApgId.ENCRYPT);

		Button decryptButton = (Button) findViewById(R.id.decrypt);
		setOnClick(decryptButton, Apg.Intent.DECRYPT, ApgId.DECRYPT);

		Button decryptFileButton = (Button) findViewById(R.id.decrypt_file);
		setOnClick(decryptFileButton, Apg.Intent.DECRYPT_FILE, ApgId.DECRYPT);

		Button generateSignatureButton = (Button) findViewById(R.id.generate_signature);
		setOnClick(generateSignatureButton, Apg.Intent.GENERATE_SIGNATURE,
				ApgId.GENERATE_SIGNATURE);
	}
}
