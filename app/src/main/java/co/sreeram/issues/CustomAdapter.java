package co.sreeram.issues;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sreeram AN on 11/03/2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title, body;

        CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.title);
            body = (TextView) itemView.findViewById(R.id.body);
        }
        public void onClick(View view) {
            if (mOnEntryClickListener != null) {
                mOnEntryClickListener.onEntryClick(view, getLayoutPosition());
            }
        }
    }

    private ArrayList<HashMap<String, String>> mCustomObjects;

    public CustomAdapter(ArrayList<HashMap<String, String>> arrayList) {
        mCustomObjects = arrayList;
    }

    @Override
    public int getItemCount() {
        return mCustomObjects.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        String firstText = mCustomObjects.get(position).get("title").toString();
        String secondText = mCustomObjects.get(position).get("body").toString();

        holder.title.setText(firstText);
        holder.body.setText(secondText);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    private OnEntryClickListener mOnEntryClickListener;

    public interface OnEntryClickListener {
        void onEntryClick(View view, int position);
    }

    public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener) {
        mOnEntryClickListener = onEntryClickListener;
    }
}