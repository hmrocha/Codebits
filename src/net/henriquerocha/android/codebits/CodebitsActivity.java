package net.henriquerocha.android.codebits;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;

public abstract class CodebitsActivity extends SherlockActivity implements OnNavigationListener {
    private static final String TAG = "CodebitsActivity";

    protected String[] mMenu;
    protected String mToken;
    protected ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(this, R.array.menu,
                R.layout.sherlock_spinner_item);
        this.mMenu = getResources().getStringArray(R.array.menu);
        this.mToken = getIntent().getStringExtra(Constants.AUTH_TOKEN);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setListNavigationCallbacks(list, this);
        mActionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public abstract boolean onNavigationItemSelected(int itemPosition, long itemId);

    protected void scanQrCode() {
        Log.d(TAG, "scanQrCode");
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The scanned a qr-code.
                String contents = data.getStringExtra("SCAN_RESULT");
                if (contents.startsWith("https://codebits.eu")) {
                    String nick = contents.substring(contents.lastIndexOf('/') + 1,
                            contents.length());
                    Intent intent = new Intent(this, UserActivity.class);
                    intent.putExtra(Constants.KEY_USER_NICK, nick);
                    intent.putExtra(Constants.AUTH_TOKEN, mToken);
                    startActivity(intent);
                }
            }
            if (resultCode == RESULT_CANCELED) {
            }
        }
    }
}
