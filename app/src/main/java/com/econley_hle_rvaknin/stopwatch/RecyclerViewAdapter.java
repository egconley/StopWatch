package com.econley_hle_rvaknin.stopwatch;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.LinkedList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final String TAG = "rvrv";
    private LinkedList<String> mValues;

    public RecyclerViewAdapter(LinkedList<String> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String strAddress = holder.mItem;
                Intent i = new Intent(v.getContext(),MapActivity.class);
                i.putExtra("address",strAddress);
                i.putExtra("recyclerViewAddress", true);
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;

        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.destination);
        }

        @Override
        public String toString() {
            return "";
        }
    }
}
