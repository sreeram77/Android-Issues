package co.sreeram.issues;

import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

/**
 * Created by Sreeram AN on 09/03/2017.
 */

public class CommentActivity extends AppCompatActivity {

    private String TAG = "CommentActivity";
    private ListView lv2;
    static ArrayList<HashMap<String, String>> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentList = new ArrayList<>();
        lv2 = (ListView) findViewById(R.id.list2);
        try {
            File httpCacheDir = new File(getApplicationContext().getCacheDir(), "comment");
            long httpCacheSize = 1 * 1024 * 1024;
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:" + e);
        }
        new GetComments().execute();
    }
    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }
    private class GetComments extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(CommentActivity.this,"Downloading Comments...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String url=getIntent().getStringExtra("url");
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray comments = new JSONArray(jsonStr);

                    for (int i = 0; i < comments.length(); i++) {
                        JSONObject c = comments.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("body");
                        String email = c.getString("url");
                        JSONObject user = c.getJSONObject("user");
                        String uname = user.getString("login");

                        HashMap<String, String> comment = new HashMap<>();
                        comment.put("id", id);
                        comment.put("body", name);
                        comment.put("url", email);
                        comment.put("login", uname);
                        commentList.add(comment);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Unable to download from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Couldn't Download JSON", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            final ListAdapter adapter2 = new SimpleAdapter(CommentActivity.this, commentList,
                    R.layout.list_item2, new String[]{ "login","body"},
                    new int[]{R.id.uname, R.id.comment});
            lv2.setAdapter(adapter2);

        }
    }

}
