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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import net.henriquerocha.android.codebits.api.Comment;
import net.henriquerocha.android.codebits.api.Methods;
import net.henriquerocha.android.codebits.api.Talk;
import net.henriquerocha.android.codebits.api.TalksCommentsXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class DisplayTalkActivity extends SherlockActivity implements ActionBar.TabListener {
    private static final String TAG = DisplayTalkActivity.class.getSimpleName();

    private static final String ABSTRACT_TAB = "ABSTRACT";
    private static final String COMMENTS_TAB = "COMMENTS";
    
    private Context context;
    private Talk talk;
    private String token;
    private TextView tvUserVote;
    
    private List<Comment> comments = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_talk);

        // Set up tabs
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab tab = getSupportActionBar().newTab();
        tab.setText(ABSTRACT_TAB);
        tab.setTabListener(this);
        getSupportActionBar().addTab(tab);
        tab = getSupportActionBar().newTab();
        tab.setText(COMMENTS_TAB);
        tab.setTabListener(this);
        getSupportActionBar().addTab(tab);

        context = this;
        Intent intent = getIntent();
        talk = intent.getParcelableExtra("talk");
        getSupportActionBar().setTitle(talk.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        token = getIntent().getStringExtra(LoginActivity.AUTH_TOKEN);
        TextView tv = (TextView) findViewById(R.id.tv_talk_description);
        tv.setText(talk.getDescription());
        tv = (TextView) findViewById(R.id.tv_proposed);
        tv.setText(talk.getProposed());
        tv = (TextView) findViewById(R.id.tv_votes_up);
        tv.setText(String.valueOf(talk.getUpVotes()));
        tv = (TextView) findViewById(R.id.tv_votes_down);
        tv.setText(String.valueOf(talk.getDownVotes()));
        tvUserVote = (TextView) findViewById(R.id.tv_user_vote);
        String rate = talk.getRate();
        if ("up".equals(rate)) {
            tvUserVote.setText(R.string.you_voted_up);
        } else if ("down".equals(rate)) {
            tvUserVote.setText(R.string.you_voted_down);
        }
        if (token == null) {
            findViewById(R.id.login_warning).setVisibility(View.VISIBLE);
            findViewById(R.id.rate_it_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.login_warning).setVisibility(View.GONE);
            findViewById(R.id.rate_it_layout).setVisibility(View.VISIBLE);
        }
    }

    public void onCallUpTalk(View v) {
        new VoteTalkTask().execute("up", this.talk.getId());
    }

    public void onCallDownTalk(View v) {
        new VoteTalkTask().execute("down", this.talk.getId());
    }

    private class VoteTalkTask extends AsyncTask<String, Void, Void> {
        private String vote;

        @Override
        protected Void doInBackground(String... args) {
            this.vote = args[0];
            try {
                if ("up".equals(vote)) {
                    Methods.callUpTalk(Integer.parseInt(args[1]), token);
                } else if ("down".equals(vote)) {
                    Methods.callDownTalk(Integer.parseInt(args[1]), token);
                }
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Resources res = context.getResources();
            if ("up".equals(vote)) {
                tvUserVote.setText(res.getString(R.string.you_voted_up));
                tvUserVote.setTextColor(res.getColor(R.color.green_rated));
            } else if ("down".equals(vote)) {
                tvUserVote.setText(res.getString(R.string.you_voted_down));
                tvUserVote.setTextColor(res.getColor(R.color.red_votes_down));
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (ABSTRACT_TAB.equals(tab.getText())) {
            setContentView(R.layout.activity_display_talk);
        }
        if (COMMENTS_TAB.equals(tab.getText())) {
            new DownloadXmlTask().execute("https://codebits.eu/rss/proposal/" + talk.getId());
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        TalksCommentsXmlParser xmlParser = new TalksCommentsXmlParser();
        

        try {
            stream = downloadUrl(urlString);
            comments = xmlParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        StringBuilder htmlString = new StringBuilder();
        for (Comment comment : comments) {
            htmlString.append(comment.toString());
            htmlString.append("\n");
        }
        return htmlString.toString();
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    // Implementation of AsyncTask used to download XML feed from codebits.eu
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return "IOException"; // getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return "XmlPullParserException"; // getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            setContentView(R.layout.comments);
            String[] commentsArray = new String[comments.size()];
            int i = 0;
            for (Comment c : comments) {
                commentsArray[i++] = c.toString();
            }
            ListView lv = (ListView) findViewById(R.id.comments_list);
            lv.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, commentsArray));
            Log.d(TAG, result);
        }
    }

}
