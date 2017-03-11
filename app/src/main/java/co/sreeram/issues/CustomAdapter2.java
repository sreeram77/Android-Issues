package co.sreeram.issues;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sreeram AN on 11/03/2017.
 */

public class CustomAdapter2 extends RecyclerView.Adapter<CustomAdapter2.CustomViewHolder> {

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView login, body;

        CustomViewHolder(View itemView) {
            super(itemView);
            login = (TextView) itemView.findViewById(R.id.uname);
            body = (TextView) itemView.findViewById(R.id.comment);
        }
    }

    private ArrayList<HashMap<String, String>> mCustomObjects;

    public CustomAdapter2(ArrayList<HashMap<String, String>> arrayList) {
        mCustomObjects = arrayList;
    }

    @Override
    public int getItemCount() {
        return mCustomObjects.size();
    }

    @Override
    public CustomAdapter2.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2, parent, false);
        return new CustomAdapter2.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomAdapter2.CustomViewHolder holder, int position) {
        String firstText = mCustomObjects.get(position).get("login").toString();
        String secondText = mCustomObjects.get(position).get("body").toString();

        holder.login.setText(firstText);
        holder.body.setText(secondText);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
