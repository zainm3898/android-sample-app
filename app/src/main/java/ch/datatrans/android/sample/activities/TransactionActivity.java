package ch.datatrans.android.sample.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.datatrans.android.sample.R;
import ch.datatrans.android.sample.ResourceProvider;
import ch.datatrans.android.sample.model.Transaction;
import ch.datatrans.android.sample.model.TransactionDetails;
import ch.datatrans.android.sample.persistence.TransactionsDataSource;
import ch.datatrans.payment.AliasPaymentMethod;
import ch.datatrans.payment.AliasPaymentMethodCreditCard;
import ch.datatrans.payment.AliasPaymentMethodPostFinanceCard;
import ch.datatrans.payment.Payment;
import ch.datatrans.payment.PaymentMethod;
import ch.datatrans.payment.PaymentMethodCreditCard;
import ch.datatrans.payment.PaymentMethodType;
import ch.datatrans.payment.PaymentProcessState;
import ch.datatrans.payment.DisplayContext;
import ch.datatrans.payment.IPaymentProcessStateListener;
import ch.datatrans.payment.PaymentProcessAndroid;
import ch.twint.payment.sdk.TwintEnvironment;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class TransactionActivity extends AppCompatActivity {

    public final static String TAG = TransactionActivity.class.getName();
    private final static int MY_SCAN_REQUEST_CODE = 1337;

    private TransactionsDataSource transactionsDataSource;
    private TransactionDetails transactionDetails;
    private PaymentProcessStateListener paymentProcessStateListener = new PaymentProcessStateListener();

    interface DefaultPaymentInformation {
        String MERCHANT_ID = "1100004450";
        String AMOUNT = "1000";
        String CURRENCY = "CHF";
        String REFERENCE_NUMBER = "968927";
        String SIGN = "30916165706580013";
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

                // TODO - check if selected payment method is of type credit card.
                // TODO - if not, do not offer the "scan" option
                new MaterialDialog.Builder(this)
                        .title(R.string.authorisation_method)
                        .items(R.array.authorisation_methods)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0: // standard mode
                                        startTransaction(getPaymentInformation());
                                        break;
                                    case 1: // hidden mode - launch card.io
                                        Intent scanIntent = new Intent(TransactionActivity.this, CardIOActivity.class);
                                        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false);
                                        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false);
                                        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true);
                                        scanIntent.putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, 0xFF76B4CF);
                                        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
                                        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);
                                        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
                                        break;
                                }
                                return false;
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
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                final CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.dialog_edit_credit_card_details, null);

                setText(R.id.et_card_number, scanResult.cardNumber, view);
                setText(R.id.et_expiry_month, scanResult.isExpiryValid() ? String.valueOf(scanResult.expiryMonth) : "", view);
                setText(R.id.et_expiry_year, scanResult.isExpiryValid() ? String.valueOf(scanResult.expiryYear) : "", view);
                setText(R.id.et_cvv, scanResult.cvv, view);

                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .icon(getResources().getDrawable(R.drawable.ic_credit_card))
                        .title(scanResult.getCardType().name)
                        .customView(view, true)
                        .autoDismiss(false)
                        .positiveText(R.string.cc_details_ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                PaymentMethodType paymentMethodType = PaymentMethodType.valueOf(scanResult.getCardType().name());

                                try {
                                    PaymentMethodCreditCard paymentMethod = new PaymentMethodCreditCard(paymentMethodType,
                                            getText(R.id.et_card_number, view),
                                            Integer.parseInt(getText(R.id.et_expiry_year, view)),
                                            Integer.parseInt(getText(R.id.et_expiry_month, view)),
                                            Integer.parseInt(getText(R.id.et_cvv, view)),
                                            null);

                                    dialog.dismiss();
                                    startTransaction(getPaymentInformation(), paymentMethod);
                                } catch (Exception e) {
                                    Toast.makeText(TransactionActivity.this, "Invalid credit card data!", Toast.LENGTH_LONG).show();

                                }

                            }
                        })
                        .build();

                dialog.show();

            } else {
                Toast.makeText(this, "Scan was canceled!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startTransaction(TransactionDetails transactionDetails) {
        startTransaction(transactionDetails, null);
    }

    private void startTransaction(TransactionDetails transactionDetails, PaymentMethod scannedCard) {
        Map<String, String> merchantProperties = new HashMap<>();
        //merchantProperties.put("param1", "value1");
        //merchantProperties.put("param2", "value2");

        Payment payment = new Payment(transactionDetails.getMerchantId(),
                transactionDetails.getRefrenceNumber(),
                transactionDetails.getCurrency(),
                transactionDetails.getAmount(),
                transactionDetails.getSign());

        // set a custom language if needed
        //setCustomLanguage("fr", this);

        DisplayContext dc = new DisplayContext(new ResourceProvider(), this);

        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(PaymentMethod.createMethod(PaymentMethodType.VISA));
        paymentMethods.add(PaymentMethod.createMethod(PaymentMethodType.MASTERCARD));
        paymentMethods.add(PaymentMethod.createMethod(PaymentMethodType.AMEX));
        paymentMethods.add(PaymentMethod.createMethod(PaymentMethodType.PFCARD));
        paymentMethods.add(PaymentMethod.createMethod(PaymentMethodType.PAYPAL));

        // normal payment
        PaymentProcessAndroid ppa = new PaymentProcessAndroid(dc, payment, paymentMethods);

        // payment with alias request for registrations only
        //AliasRequest ar = new AliasRequest(transactionDetails.getMerchantId(), "CHF", paymentMethods);

        //PaymentProcessAndroid ppa = new PaymentProcessAndroid(dc, ar);

        // payment with aliasCC
        //PaymentProcessAndroid ppa = new PaymentProcessAndroid(dc, payment, new AliasPaymentMethodCreditCard(PaymentMethodType.VISA, "70119122433810042", "", 2018, 12, "DME"));

        if (transactionDetails.getPaymentMethod() != null && !transactionDetails.getPaymentMethod().isEmpty()) {
            try {
                PaymentMethodType paymentMethodType = PaymentMethodType.getPaymentMethodTypeByIdentifier(transactionDetails.getPaymentMethod());
                PaymentMethod paymentMethod = new PaymentMethod(paymentMethodType);
                ppa = new PaymentProcessAndroid(dc, payment, paymentMethod);
            } catch (IllegalArgumentException e) {
                // do nothing - just proceed with empty payment method
            }
        }

        if (scannedCard != null) {
            ppa = new PaymentProcessAndroid(dc, payment, scannedCard);
        }

        this.transactionDetails = transactionDetails;

        // useAlias
        // https://docs.datatrans.ch/docs/payment-process-alias
        ppa.getPaymentOptions().setRecurringPayment(true);

        // CAA
        //ppa.getPaymentOptions().setAutoSettlement(true);

        // this invokes the 'BEFORE_COMPLETION' callback which allows the user to show
        // a custom confirmation screen/dialog
        ppa.setManualCompletionEnabled(true);

        // activate split mode. use transactionId from callback to complete transaction
        // https://docs.datatrans.ch/docs/integrations-split-mode#section-finalize-the-authorization
        //ppa.getPaymentOptions().setSkipAuthorizationCompletion(true);

        // send custom merchant properties
        ppa.getPaymentOptions().getMerchantProperties().putAll(merchantProperties);

        // used to ensure a proper switch back to the app
        ppa.getPaymentOptions().setAppCallbackScheme("ch.datatrans.android.sample");

        // set correct TWINT environment
        ppa.getPaymentOptions().setTWINTEnvironment(TwintEnvironment.INT);

        ppa.setTestingEnabled(true);
        ppa.getPaymentOptions().setCertificatePinning(true);
        ppa.addStateListener(paymentProcessStateListener);
        ppa.start();
    }

    private TransactionDetails getPaymentInformation() {
        String merchantID = ((EditText) findViewById(R.id.et_merchant_id)).getText().toString();
        String amount = ((EditText) findViewById(R.id.et_amount)).getText().toString();
        String currency = ((EditText) findViewById(R.id.et_currency)).getText().toString();
        String referenceNumber = ((EditText) findViewById(R.id.et_refrence_number)).getText().toString();
        String paymentMethod = ((EditText) findViewById(R.id.et_payment_method)).getText().toString();
        String sign = ((EditText) findViewById(R.id.et_sign)).getText().toString();

        return new TransactionDetails(merchantID,
                TextUtils.isEmpty(amount) ? Integer.parseInt(DefaultPaymentInformation.AMOUNT) : Integer.parseInt(amount),
                currency,
                referenceNumber,
                paymentMethod,
                sign);
    }

    private void setText(int id, String text) {
        setText(id, text, null);
    }

    private void setText(int id, String text, View view) {
        EditText editText;

        if (view == null) {
            editText = (EditText) findViewById(id);
        } else {
            editText = (EditText) view.findViewById(id);
        }

        editText.setText(text);
    }

    private String getText(int id, View view) {
        return ((EditText) view.findViewById(id)).getText().toString();
    }

    private void autofillPaymentInformation() {
        setText(R.id.et_merchant_id, DefaultPaymentInformation.MERCHANT_ID);
        setText(R.id.et_amount, DefaultPaymentInformation.AMOUNT);
        setText(R.id.et_currency, DefaultPaymentInformation.CURRENCY);
        setText(R.id.et_refrence_number, DefaultPaymentInformation.REFERENCE_NUMBER);
        setText(R.id.et_sign, DefaultPaymentInformation.SIGN);
    }

    private void setCustomLanguage(String language, Context context) {
        Locale locale = new Locale(language);

        // Resources (SDK dialogs):
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // Locale (SDK string representations + webview):
        Locale.setDefault(locale);
    }

    class PaymentProcessStateListener implements IPaymentProcessStateListener {

        @Override
        public void paymentProcessStateChanged(final PaymentProcessAndroid paymentProcess) {

            PaymentProcessState state = paymentProcess.getState();
            Log.d(TAG, state.toString());

            switch (state) {
                case BEFORE_COMPLETION:
                    TransactionActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MaterialDialog dialog = new MaterialDialog.Builder(TransactionActivity.this)
                                    .icon(getResources().getDrawable(R.drawable.ic_credit_card))
                                    .autoDismiss(false)
                                    .negativeText("Cancel")
                                    .content("Please confirm payment of " + transactionDetails.getCurrency() + " " + Transaction.getFormattedAmount(transactionDetails.getAmount()))
                                    .positiveText(R.string.cc_details_ok)
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            paymentProcess.complete();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onNegative(MaterialDialog dialog) {
                                            paymentProcess.cancel();
                                            dialog.dismiss();
                                        }
                                    })
                                    .build();

                            dialog.show();

                        }
                    });
                    break;

                case COMPLETED:
                    StringBuilder successMessage = new StringBuilder("Transaction completed successfully!");
                    String alias = null;

                    PaymentMethodType paymentMethodType = PaymentMethodType.getPaymentMethodTypeByIdentifier("INT");

                    if (paymentProcess.getAliasPaymentMethod() != null) {
                        AliasPaymentMethod aliasPaymentMethod = paymentProcess.getAliasPaymentMethod();
                        alias = aliasPaymentMethod.getAlias();
                        successMessage.append("\naliasCC=" + alias);

                        Log.i(TAG, "received AliasPaymentMethod:");
                        Log.i(TAG, "\tcredit card: " + ((aliasPaymentMethod instanceof AliasPaymentMethodCreditCard) ? "yes" : "no"));
                        Log.i(TAG, "\tpf card: " + ((aliasPaymentMethod instanceof AliasPaymentMethodPostFinanceCard) ? "yes" : "no"));
                        Log.i(TAG, "\ttype: " + aliasPaymentMethod.getType());
                        Log.i(TAG, "\ttype/name: " + aliasPaymentMethod.getType().getName());

                        if (aliasPaymentMethod instanceof AliasPaymentMethodCreditCard) {
                            AliasPaymentMethodCreditCard aliasPaymentMethodCreditCard = (AliasPaymentMethodCreditCard) aliasPaymentMethod;
                            String cardHolder = aliasPaymentMethodCreditCard.getCardHolder();
                            if (cardHolder != null && !cardHolder.isEmpty()) {
                                String firstname = cardHolder.split(" ")[0].replace("+", " ");
                                String lastname = cardHolder.substring(firstname.length() + 1).replace("+", " ");
                                successMessage.append("\nfirstname=" + firstname + ", lastname=" + lastname);
                            }
                        }

                    }

                    showToast(successMessage.toString());
                    saveTransaction(state, alias);
                    break;
                case CANCELED:
                    showToast("Transaction canceled!");
                    saveTransaction(state);
                    break;
                case ERROR:
                    showToast("An error occurred!");


                    Log.e(TAG, paymentProcess.getException().getMessage());
                    saveTransaction(state);
                    break;
            }
        }

        private void saveTransaction(PaymentProcessState state, String alias) {
            transactionDetails.setStatus(state.name());
            transactionDetails.setAliasCC(alias);
            transactionsDataSource.open();
            transactionsDataSource.saveTransaction(transactionDetails);
            transactionsDataSource.close();
        }

        private void saveTransaction(PaymentProcessState state) {
            saveTransaction(state, null);
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
