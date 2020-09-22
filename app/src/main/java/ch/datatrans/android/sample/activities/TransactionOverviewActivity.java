package ch.datatrans.android.sample.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import ch.datatrans.android.sample.R;
import ch.datatrans.android.sample.adapters.TransactionOverviewListAdapter;
import ch.datatrans.android.sample.persistence.TransactionsDataSource;

public class TransactionOverviewActivity extends AppCompatActivity implements View.OnClickListener {

    private TransactionsDataSource transactionsDataSource;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_overview_empty);

        setTitle("Transactions overview");

        transactionsDataSource = new TransactionsDataSource(this);
        transactionsDataSource.open();

        if(transactionsDataSource.getAllTransactions().size() > 0) {
            setContentView(R.layout.activity_transaction_overview);
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new TransactionOverviewListAdapter(transactionsDataSource.getAllTransactions());
            mRecyclerView.setAdapter(mAdapter);

        }

        View createTransactionButton = findViewById(R.id.btn_create_transaction);
        createTransactionButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        transactionsDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        transactionsDataSource.close();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, TransactionActivity.class);
        startActivity(intent);
    }
}
