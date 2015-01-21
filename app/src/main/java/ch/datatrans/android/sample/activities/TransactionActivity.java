package ch.datatrans.android.sample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import ch.datatrans.android.sample.R;
import ch.datatrans.android.sample.ResourceProvider;
import ch.datatrans.android.sample.TransactionDetails;
import ch.datatrans.android.sample.TransactionsDataSource;
import ch.datatrans.payment.BusinessException;
import ch.datatrans.payment.Payment;
import ch.datatrans.payment.PaymentProcessState;
import ch.datatrans.payment.android.IPaymentProcessStateListener;
import ch.datatrans.payment.android.PaymentProcessAndroid;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class TransactionActivity extends ActionBarActivity {

    public final static String TAG = TransactionActivity.class.getName();
    private final static int MY_SCAN_REQUEST_CODE = 1337;

    private TransactionsDataSource transactionsDataSource;
    private TransactionDetails transactionDetails;

    private PaymentProcessStateListener paymentProcessStateListener = new PaymentProcessStateListener();

    interface DefaultPaymentInformation {
        public static final String MERCHANT_ID = "1100004450";
        public static final String AMOUNT = "10";
        public static final String CURRENCY = "CHF";
        public static final String REFERENCE_NUMBER = "968927";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("New transaction");
        setContentView(R.layout.activity_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        transactionsDataSource = new TransactionsDataSource(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_menu_send:

                new MaterialDialog.Builder(this)
                        .title(R.string.authorisation_method)
                        .items(R.array.authorisation_methods)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0: // standard mode
                                        startTransaction();
                                        break;
                                    case 1: // hidden mode - launch card.io

                                        Intent scanIntent = new Intent(TransactionActivity.this, CardIOActivity.class);
                                        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: true
                                        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
                                        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, false);
                                        scanIntent.putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, 0xFF76B4CF);
                                        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true); // default: false
                                        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);

                                        break;
                                }
                            }
                        })
                        .positiveText(R.string.authorisation_choose)
                        .show();

                return true;
            case R.id.action_menu_autofill:
                autofillPaymentInformation();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);


                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }

            Toast.makeText(this, resultDisplayStr, Toast.LENGTH_LONG).show();

        }

    }

    private void startTransaction() {
        transactionDetails = createPaymentInformation();

        Payment payment = new Payment(transactionDetails.getMerchantId(),
                transactionDetails.getRefrenceNumber(),
                transactionDetails.getCurrency(),
                transactionDetails.getAmount(),
                transactionDetails.getSign(),
                null);

        PaymentProcessAndroid ppa = new PaymentProcessAndroid(new ResourceProvider(), this, payment);
        ppa.getPaymentOptions().setCertificatePinning(true);

        ppa.setTestingEnabled(true);
        ppa.addStateListener(paymentProcessStateListener);
        ppa.start();
    }

    private TransactionDetails createPaymentInformation() {
        String merchantID = ((EditText) findViewById(R.id.et_merchant_id)).getText().toString();
        String amount = ((EditText) findViewById(R.id.et_amount)).getText().toString();
        String currency = ((EditText) findViewById(R.id.et_currency)).getText().toString();
        String referenceNumber = ((EditText) findViewById(R.id.et_refrence_number)).getText().toString();
        String sign = ((EditText) findViewById(R.id.et_sign)).getText().toString();

        return new TransactionDetails(merchantID,
                TextUtils.isEmpty(amount) ? Integer.parseInt(DefaultPaymentInformation.AMOUNT) : Integer.parseInt(amount),
                currency,
                referenceNumber,
                sign);
    }

    private void setText(int id, String text) {
        EditText editText = (EditText) findViewById(id);
        editText.setText(text);
    }

    private void autofillPaymentInformation() {
        setText(R.id.et_merchant_id, DefaultPaymentInformation.MERCHANT_ID);
        setText(R.id.et_amount, DefaultPaymentInformation.AMOUNT);
        setText(R.id.et_currency, DefaultPaymentInformation.CURRENCY);
        setText(R.id.et_refrence_number, DefaultPaymentInformation.REFERENCE_NUMBER);
    }

    class PaymentProcessStateListener implements IPaymentProcessStateListener {

        @Override
        public void paymentProcessStateChanged(PaymentProcessAndroid paymentProcess) {
            PaymentProcessState state = paymentProcess.getState();
            Log.d(TAG, state.toString());

            switch (state) {
                case COMPLETED:
                    saveTransaction(state);
                    showToast("Transaction completed successfully!");
                    break;
                case CANCELED:
                    saveTransaction(state);
                    showToast("Transaction canceled!");
                    break;
                case ERROR:
                    saveTransaction(state);
                    showToast("An error occurred!");
                    break;
            }
        }

        private void saveTransaction(PaymentProcessState state) {
            transactionDetails.setStatus(state.name());
            transactionsDataSource.open();
            transactionsDataSource.saveTransaction(transactionDetails);
            transactionsDataSource.close();
        }

        private void showToast(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TransactionActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
