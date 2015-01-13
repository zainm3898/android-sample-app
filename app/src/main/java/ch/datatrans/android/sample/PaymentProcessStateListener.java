package ch.datatrans.android.sample;

import android.util.Log;

import ch.datatrans.payment.BusinessException;
import ch.datatrans.payment.PaymentProcessState;
import ch.datatrans.payment.android.IPaymentProcessStateListener;
import ch.datatrans.payment.android.PaymentProcessAndroid;

/**
 * Created by domi on 1/12/15.
 */
public class PaymentProcessStateListener implements IPaymentProcessStateListener {

    public static final String TAG = PaymentProcessStateListener.class.getName();

    @Override
    public void paymentProcessStateChanged(PaymentProcessAndroid paymentProcessAndroid) {
        PaymentProcessState state = paymentProcessAndroid.getState();
        Log.d(TAG, state.toString());

        switch (state) {
            case COMPLETED:
                break;
            case CANCELED:
                break;
            case ERROR:
                Exception e = paymentProcessAndroid.getException();
                Log.e(TAG, "An error occured", e);

                if (e instanceof BusinessException) {
                    BusinessException be = (BusinessException) e;
                    int errorCode = be.getErrorCode(); // Datatrans error code if needed
                    Log.e(TAG, "errorCode = " + errorCode);
                }

                break;
        }
    }
}
