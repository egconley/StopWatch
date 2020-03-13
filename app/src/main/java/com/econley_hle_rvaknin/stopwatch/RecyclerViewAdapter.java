package com.econley_hle_rvaknin.stopwatch;

import androidx.recyclerview.widget.RecyclerView;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.econley_hle_rvaknin.stopwatch.bottomnavigation.RecentDestinationsFragment;
import java.util.LinkedList;
import java.util.Locale;


/**
 * {@link RecyclerView.Adapter} that can display a {@link } and makes a call to the
 * specified {@link}.
 * TODO: Replace the implementation with code for your data type.
 */
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
                System.out.println("CLICKED ITEM FROM RECYCLER VIEW!!!!!" + mValues);
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String location = mValues.getFirst();
                System.out.println("LOCATION: " + location);

                // Not sure if this is the correct intent code for this purpose...
                Intent intent = new Intent(context, MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                // ELLEN STILL WORKING ON THIS PART
                MapActivity.start(context, location);
//                context.startActivity(intent);



                // Want to call setDestination from MapActivity here



//                Intent i = new Intent(context,TaskDetail.class).putExtra("Task",mValues.get(position).title())
//                        .putExtra("TaskDetails",mValues.get(position).body());
//                if(mValues.get(position).team().name() != null){
//                    i.putExtra("TaskTeam",mValues.get(position).team().name()).putExtra("TaskImg",mValues.get(position).image())
//                            .putExtra("TaskLocation",mValues.get(position).location());
//                }
//                context.startActivity(i);
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
