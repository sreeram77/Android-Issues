package co.sreeram.issues;

import android.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        try {
            File httpCacheDir = new File(getApplicationContext().getCacheDir(), "main");
            long httpCacheSize = 1 * 1024 * 1024;
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:" + e);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        IssueFrag fragment = new IssueFrag();
        //CommentFrag fragment = new CommentFrag();
        fragmentTransaction.add(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = this
                        .getSupportFragmentManager();
                fm.popBackStack ("isu", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
