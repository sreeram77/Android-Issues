package co.sreeram.issues;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sreeram AN on 11/03/2017.
 */

public class CommentFrag extends Fragment {

    private String TAG = "CommentFrag";
    public CustomAdapter2 myAdapter;
    private ProgressDialog progress;
    private RecyclerView myRecyclerView;
    static ArrayList<HashMap<String, String>> commentList;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comment_fragment, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        commentList = new ArrayList<>();
        myRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler2);
       // lv2 = (ListView) rootView.findViewById(R.id.list);
        new GetComments().execute();
        return rootView;
    }


    private class GetComments extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getContext());
            progress.setMessage("Loading...");
            progress.show();
            //Toast.makeText(this,"Downloading Comments...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            Bundle bundle = getArguments();
            String url = bundle.getString("url");
            //String url = "https://api.github.com/repos/crashlytics/secureudid/issues/4/comments";
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Unable to download from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),"Couldn't Download JSON", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            myAdapter = new CustomAdapter2(commentList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            myRecyclerView.setLayoutManager(mLayoutManager);
            myRecyclerView.setAdapter(myAdapter);
            /*final ListAdapter adapter2 = new SimpleAdapter(getActivity(), commentList,
                    R.layout.list_item2, new String[]{ "login","body"},
                    new int[]{R.id.uname, R.id.comment});
            lv2.setAdapter(adapter2);*/
            progress.dismiss();

        }
    }
}
