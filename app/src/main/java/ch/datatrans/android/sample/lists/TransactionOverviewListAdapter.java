package ch.datatrans.android.sample.lists;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ch.datatrans.android.sample.R;
import ch.datatrans.android.sample.models.Transaction;

/**
 * Created by domi on 1/13/15.
 */
public class TransactionOverviewListAdapter extends RecyclerView.Adapter<TransactionOverviewListAdapter.ViewHolder> {

    private List<Transaction> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mMerchantId;
        public TextView mReferenceNumber;
        public TextView mCurrency;
        public TextView mAmount;
        public TextView mDate;
        public TextView mStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            mMerchantId = (TextView) itemView.findViewById(R.id.tv_merchant_id);
            mReferenceNumber = (TextView) itemView.findViewById(R.id.tv_reference_number);
            mCurrency = (TextView) itemView.findViewById(R.id.tv_currency);
            mAmount = (TextView) itemView.findViewById(R.id.tv_amount);
            mDate = (TextView) itemView.findViewById(R.id.tv_date);
            mStatus = (TextView) itemView.findViewById(R.id.tv_status);
        }
    }

    private enum StatusColor {

        COMPLETED("#009957"), CANCELED("#FFB402"), ERROR("#D94530");

        private final String color;

        StatusColor(String color) {
            this.color = color;
        }
    }

    public TransactionOverviewListAdapter(List<Transaction> dataset) {
        mDataset = dataset;
    }

    @Override
    public TransactionOverviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mMerchantId.setText(String.valueOf(mDataset.get(position).getMerchantId()));
        holder.mReferenceNumber.setText(mDataset.get(position).getReferenceNumber());
        holder.mCurrency.setText(String.valueOf(mDataset.get(position).getCurrency()));
        holder.mAmount.setText(" " + String.valueOf(mDataset.get(position).getFormattedAmount()));
        holder.mDate.setText(mDataset.get(position).getFormattedDate());

        String status = mDataset.get(position).getStatus();
        holder.mStatus.setText(status);

        holder.mStatus.setBackgroundColor(Color.parseColor(StatusColor.valueOf(status).color));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
