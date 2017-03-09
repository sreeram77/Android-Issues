package co.sreeram.issues;

import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private ListView lv;
    static ArrayList<HashMap<String, String>> issueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        issueList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        try {
            File httpCacheDir = new File(getApplicationContext().getCacheDir(), "main");
            long httpCacheSize = 1 * 1024 * 1024;
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:" + e);
        }
        new GetIssues().execute();
    }
    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    private class GetIssues extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Downloading Issues from Git",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String url = "https://api.github.com/repos/crashlytics/secureudid/issues?sort=updated";
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response : " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray issues = new JSONArray(jsonStr);
                    for (int i = 0; i < issues.length(); i++) {
                        JSONObject c = issues.getJSONObject(i);
                        String curl = c.getString("comments_url");
                        String body = c.getString("body");
                        String title = c.getString("title");
                        HashMap<String, String> issue = new HashMap<>();
                        issue.put("comments_url", curl);
                        issue.put("body", body);
                        issue.put("title", title);
                        issueList.add(issue);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json Error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Unable to download from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't Download JSON ",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            final ListAdapter adapter = new SimpleAdapter(MainActivity.this, issueList,
                    R.layout.list_item, new String[]{ "title","body"},
                    new int[]{R.id.title, R.id.body});
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent i = new Intent(MainActivity.this,CommentActivity.class);
                    String extra = issueList.get(position).get("comments_url").toString();
                    i.putExtra("url", extra);
                    startActivity(i);
                }
            });
        }
    }

}
