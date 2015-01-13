package ch.datatrans.android.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by domi on 1/13/15.
 */
public class TransactionOverviewListAdapter  extends RecyclerView.Adapter<TransactionOverviewListAdapter.ViewHolder> {

    private String[] mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mMerchantId;

        public ViewHolder(View itemView) {
            super(itemView);
            mMerchantId = (TextView) itemView.findViewById(R.id.tv_merchant_id);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TransactionOverviewListAdapter() {

    }

    // Create new views (invoked by the layout manager)
    @Override
    public TransactionOverviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_row_item, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mMerchantIdTextView.setText(mDataset[position]);
        holder.mMerchantId.setText("1100004450");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //return mDataset.length;
        return 15;
    }
}
