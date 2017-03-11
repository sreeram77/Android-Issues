package co.sreeram.issues;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sreeram AN on 11/03/2017.
 */

public class IssueFrag extends Fragment {

    private String TAG = "IssueFrag";
    private ListView lv;
    public CustomAdapter myAdapter;
    private ProgressDialog progress;
    private RecyclerView myRecyclerView;
    static ArrayList<HashMap<String, String>> issueList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.issue_fragment, container, false);
        issueList = new ArrayList<>();
        myRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler1);

        new GetIssues().execute();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return rootView;
    }
    private class GetIssues extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getContext());
            progress.setMessage("Loading...");
            progress.show();
            //Toast.makeText(getActivity(),"Downloading Issues from Git",Toast.LENGTH_SHORT).show();
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Unable to download from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
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
            /*Toast.makeText(getActivity().getApplicationContext(),
                    "onPostExecute",
                    Toast.LENGTH_LONG).show();*/

            CustomAdapter clickAdapter = new CustomAdapter(issueList);
            clickAdapter.setOnEntryClickListener(new CustomAdapter.OnEntryClickListener() {
                @Override
                public void onEntryClick(View view, int position) {
                    CommentFrag newFragment = new CommentFrag();
                    Bundle args = new Bundle();
                    String extra = issueList.get(position).get("comments_url").toString();
                    args.putString("url", extra);
                    newFragment.setArguments(args);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, newFragment,"com");
                    transaction.addToBackStack("isu");
                    transaction.commit();
                }
            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            myRecyclerView.setLayoutManager(mLayoutManager);
            myRecyclerView.setAdapter(clickAdapter);
           /* myAdapter = new CustomAdapter(issueList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            myRecyclerView.setLayoutManager(mLayoutManager);
            myRecyclerView.setAdapter(myAdapter);

            /*final ListAdapter adapter = new SimpleAdapter(getActivity(), issueList,
                    R.layout.list_item, new String[]{ "title","body"},
                    new int[]{R.id.title, R.id.body});
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    CommentFrag newFragment = new CommentFrag();
                    Bundle args = new Bundle();
                    String extra = issueList.get(position).get("comments_url").toString();
                    args.putString("url", extra);
                    newFragment.setArguments(args);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, newFragment,"");
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });*/
            progress.dismiss();
        }
    }
}
