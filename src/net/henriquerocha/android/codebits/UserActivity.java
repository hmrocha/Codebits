/*  
 *  Codebits
 *  Copyright (C) 2012 Henrique Rocha <hmrocha@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.henriquerocha.android.codebits;

import java.net.URL;

import net.henriquerocha.android.codebits.api.Methods;
import net.henriquerocha.android.codebits.api.User;

import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserActivity extends CodebitsActivity {
    private static final String TAG = "UserActivity";

    private String token;
    private String id;
    private String nick;

    private TextView mTvName;
    private TextView mTvKarmaPoints;
    private TextView mTvBio;
    private RelativeLayout mLayout;
    private ImageView mIvAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setUpActivity();
        setProgressBarIndeterminateVisibility(true);
        SharedPreferences settings = getSharedPreferences(Constants.LOGIN_INFO, 0);
        id = settings.getString(Constants.KEY_USER_ID, "");
        nick = getIntent().getStringExtra(Constants.KEY_USER_NICK);
        mActionBar.setSelectedNavigationItem(1);
        if (nick == null) {
            new DownloadUserTask().execute(Methods.USER);
        } else {
            new DownloadUserTask().execute(Methods.NICK);
        }
    }

    private void setUpActivity() {
        token = getIntent().getStringExtra(Constants.AUTH_TOKEN);
        mTvName = (TextView) findViewById(R.id.name);
        mTvKarmaPoints = (TextView) findViewById(R.id.karma_points);
        mTvBio = (TextView) findViewById(R.id.bio);
        mLayout = (RelativeLayout) findViewById(R.id.layout);
        mLayout.setVisibility(View.GONE); // hide while we don't have data
        mIvAvatar = (ImageView) findViewById(R.id.avatar);
    }

    public void showUser(User user) {
        mTvName.setText(user.getName());
        mTvKarmaPoints.setText(user.getKarma());
        mTvBio.setText(user.getBio());
        mLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (mMenu[itemPosition].equals("SCAN USER")) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            scanQrCode();
        } else if (mMenu[itemPosition].equals("CALL FOR TALKS")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.AUTH_TOKEN, mToken);
            startActivity(intent);
        }
        return true;
    }

    private class DownloadUserTask extends AsyncTask<String, Void, Void> {
        private User user = null;
        private Bitmap avatar = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... urls) {
            String result = null;
            try {
                String url = urls[0];
                if (token != null) {
                    if (url.contains("/user")) {
                        url += "/" + id + "?token=" + token;
                    } else {
                        url += "/" + nick + "?token=" + token;
                    }
                }
                result = NetworkUtils.downloadUrl(url);
                this.user = new User(new JSONObject(result));
                URL avatarUrl = new URL(this.user.getAvatarLarge());
                this.avatar = BitmapFactory.decodeStream(avatarUrl.openConnection()
                        .getInputStream());
            } catch (Exception e) {
                Log.d(TAG, "" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (this.avatar != null) {
                mIvAvatar.setImageBitmap(this.avatar);
            }
            showUser(this.user);
            setProgressBarIndeterminateVisibility(false);
        }
    }

}
