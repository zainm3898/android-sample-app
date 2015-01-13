package ch.datatrans.android.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import ch.datatrans.payment.Payment;
import ch.datatrans.payment.android.PaymentProcessAndroid;


public class MainActivity extends Activity implements View.OnClickListener {

    private PaymentProcessStateListener paymentProcessStateListener = new PaymentProcessStateListener();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String merchantId = "1100004450"; // Datatrans merchant ID
        String refno = "968927"; // supplied by merchant's server
        String currencyCode = "CHF";
        int amount = 1000; // 10.-
        String signature = "30916165706580013";

        Payment payment = new Payment(merchantId, refno, currencyCode, amount, null, null);

        PaymentProcessAndroid ppa = new PaymentProcessAndroid(new ResourceProvider(), this, payment);

        ppa.setTestingEnabled(true);
        ppa.addStateListener(paymentProcessStateListener);
        ppa.start();

    }
}
