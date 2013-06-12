package info.guardianproject.gpg.tests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String AUTHORITY = "info.guardianproject.gpg";
	public static final Uri CONTENT_URI_SECRET_KEY_BY_KEY_ID = Uri
			.parse("content://" + AUTHORITY + "/keys/secret/key_id/");
	public static final Uri CONTENT_URI_SECRET_KEY_BY_EMAIL = Uri
			.parse("content://" + AUTHORITY + "/keys/secret/email/");
	public static final Uri CONTENT_URI_PUBLIC_KEY_BY_KEY_ID = Uri
			.parse("content://" + AUTHORITY + "/keys/public/key_id/");
	public static final Uri CONTENT_URI_PUBLIC_KEY_BY_EMAIL = Uri
			.parse("content://" + AUTHORITY + "/keys/public/email/");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final EditText publicEmailText = (EditText) findViewById(R.id.publicEmailText);
		Button publicEmailButton = (Button) findViewById(R.id.publicEmailButton);
		publicEmailButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				String ids[] = null;
				try {
					String email = publicEmailText.getText().toString();
					Uri contentUri = Uri.withAppendedPath(
							CONTENT_URI_PUBLIC_KEY_BY_EMAIL, email);
					Cursor c = context.getContentResolver().query(contentUri,
							new String[] { "key_id" }, null, null, null);
					if (c != null && c.getCount() > 0) {
						ids = new String[c.getCount()];
						while (c.moveToNext()) {
							ids[c.getPosition()] = c.getString(0);
						}
					}

					if (c != null)
						c.close();

					if (ids != null && ids.length > 0)
						Toast.makeText(context,
								"Fetched public: " + ids[0] + " from " + email,
								Toast.LENGTH_LONG).show();
					else
						Toast.makeText(context, "got nothing!",
								Toast.LENGTH_LONG).show();
				} catch (SecurityException e) {
					Toast.makeText(context, "insufficient permissions",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		final EditText secretEmailText = (EditText) findViewById(R.id.secretEmailText);
		Button secretEmailButton = (Button) findViewById(R.id.secretEmailButton);
		secretEmailButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				String ids[] = null;
				try {
					String email = secretEmailText.getText().toString();
					Uri contentUri = Uri.withAppendedPath(
							CONTENT_URI_SECRET_KEY_BY_KEY_ID, email);
					Cursor c = context.getContentResolver().query(contentUri,
							new String[] { "key_id" }, null, null, null);
					if (c != null && c.getCount() > 0) {
						ids = new String[c.getCount()];
						while (c.moveToNext()) {
							ids[c.getPosition()] = c.getString(0);
						}
					}

					if (c != null)
						c.close();

					if (ids != null && ids.length > 0)
						Toast.makeText(context,
								"Fetched secret: " + ids[0] + " from " + email,
								Toast.LENGTH_LONG).show();
				} catch (SecurityException e) {
					Toast.makeText(context, "insufficient permissions",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		final EditText publicKeyIdText = (EditText) findViewById(R.id.publicKeyIdText);
		Button publicKeyIdButton = (Button) findViewById(R.id.publicKeyIdButton);
		publicKeyIdButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				try {
					String keyId = publicKeyIdText.getText().toString();
					Uri contentUri = Uri.withAppendedPath(
							CONTENT_URI_PUBLIC_KEY_BY_KEY_ID, keyId);
					Cursor c = context.getContentResolver().query(contentUri,
							new String[] { "name", "email" }, null, null, null);
					if (c != null && c.getCount() > 0) {
						c.moveToNext();
						Toast.makeText(
								context,
								"Fetched public: " + c.getString(0) + " "
										+ c.getString(1) + " from " + keyId,
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(context, "got nothing!",
								Toast.LENGTH_LONG).show();
					}
					if (c != null)
						c.close();
				} catch (SecurityException e) {
					Toast.makeText(context, "insufficient permissions",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		final EditText secretKeyIdText = (EditText) findViewById(R.id.secretKeyIdText);
		Button secretKeyIdButton = (Button) findViewById(R.id.secretKeyIdButton);
		secretKeyIdButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				try {
					String keyId = secretKeyIdText.getText().toString();
					Uri contentUri = Uri.withAppendedPath(
							CONTENT_URI_SECRET_KEY_BY_KEY_ID, keyId);
					Cursor c = context.getContentResolver().query(contentUri,
							new String[] { "name", "email" }, null, null, null);
					if (c != null && c.getCount() > 0) {
						c.moveToNext();
						Toast.makeText(
								context,
								"Fetched secret: " + c.getString(0) + " "
										+ c.getString(1) + " from " + keyId,
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(context, "got nothing!",
								Toast.LENGTH_LONG).show();
					}
					if (c != null)
						c.close();
				} catch (SecurityException e) {
					Toast.makeText(context, "insufficient permissions",
							Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_apg_intents:
			startActivity(new Intent(this, ApgIntentsActivity.class));
			return true;
		}
		return false;
	}
}
