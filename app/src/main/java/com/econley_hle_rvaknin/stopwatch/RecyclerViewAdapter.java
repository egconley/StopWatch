package com.econley_hle_rvaknin.stopwatch;

import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.econley_hle_rvaknin.stopwatch.bottomnavigation.RecentDestinationsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final String TAG = "rvrv";
    private LinkedList<String> mValues;
    private final RecentDestinationsFragment.OnListFragmentInteractionListener mListener;

    public RecyclerViewAdapter(LinkedList<String> items, RecentDestinationsFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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
        System.out.println("mValues = " + mValues);
        holder.mTitleView.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Context context = v.getContext();
                String location = mValues.getFirst();
                MapActivity.start(context, location);
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
