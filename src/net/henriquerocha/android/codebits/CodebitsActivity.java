package net.henriquerocha.android.codebits;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;

public abstract class CodebitsActivity extends SherlockActivity implements OnNavigationListener {

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
}
