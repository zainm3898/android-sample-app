package ch.datatrans.android.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import ch.datatrans.payment.BusinessException;
import ch.datatrans.payment.Payment;
import ch.datatrans.payment.PaymentProcessState;
import ch.datatrans.payment.android.IPaymentProcessStateListener;
import ch.datatrans.payment.android.PaymentProcessAndroid;

public class TransactionActivity extends Activity {

    public static final String TAG = TransactionActivity.class.getName();

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
                startTransaction();
                return true;
            case R.id.action_menu_autofill:
                autofillPaymentInformation();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void startTransaction() {
        PaymentInformation paymentInformation = createPaymentInformation();

        // TODO - extend Payment class to allow PaymentInformation as constructor param
        Payment payment = new Payment(paymentInformation.getMerchantId(),
                                      paymentInformation.getRefrenceNumber(),
                                      paymentInformation.getCurrency(),
                                      paymentInformation.getAmount(),
                                      paymentInformation.getSign(),
                                      null);

        PaymentProcessAndroid ppa = new PaymentProcessAndroid(new ResourceProvider(), this, payment);

        ppa.setTestingEnabled(true);
        ppa.addStateListener(paymentProcessStateListener);
        ppa.start();
    }

    private PaymentInformation createPaymentInformation() {
        String merchantID = ((EditText) findViewById(R.id.et_merchant_id)).getText().toString();
        String amount = ((EditText) findViewById(R.id.et_amount)).getText().toString();
        String currency = ((EditText) findViewById(R.id.et_currency)).getText().toString();
        String referenceNumber = ((EditText) findViewById(R.id.et_refrence_number)).getText().toString();
        String sign = ((EditText) findViewById(R.id.et_sign)).getText().toString();

        return new PaymentInformation(merchantID, Integer.parseInt(amount), currency, referenceNumber, sign);
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
                    showToast("Transaction completed successfully!");
                    break;
                case CANCELED:
                    showToast("Transaction canceled!");
                    break;
                case ERROR:
                    showToast("An error occurred!");
                    Exception e = paymentProcess.getException();
                    if (e instanceof BusinessException) {
                        BusinessException be = (BusinessException) e;
                        int errorCode = be.getErrorCode(); // Datatrans error code if needed
                        Log.e(TAG, "errorCode = " + errorCode);
                    }

                    break;
            }
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
