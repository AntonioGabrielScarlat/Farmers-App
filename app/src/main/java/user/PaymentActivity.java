package user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.farmersapp.R;

import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.exception.StripeException;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

public class PaymentActivity extends AppCompatActivity {
    private TextView tvTotal;
    private CardInputWidget cardInputWidget;
    private Button btnPay;
    private Intent intent;
    private Double total;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        PaymentConfiguration.init(getApplicationContext(), getString(R.string.stripe_publishable_key));
        tvTotal=findViewById(R.id.payment_tv_total);
        cardInputWidget = findViewById(R.id.payment_card_input_widget);
        btnPay = findViewById(R.id.payment_btn_pay);
        intent=getIntent();
        total= intent.getDoubleExtra("total",0.0);
        tvTotal.setText(Double.toString(total)+" RON");
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });

    }

    private void processPayment() {
        // Obține cardul introdus de utilizator
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

        if (params == null) {
            // Cardul nu este valid
            Toast.makeText(this, "Cardul nu este valid", Toast.LENGTH_SHORT).show();
            return;
        }

        Stripe stripe = new Stripe(getApplicationContext(), getString(R.string.stripe_publishable_key));

        // Crează un PaymentMethod utilizând cardul
        new CreatePaymentMethodTask().execute(stripe, params);
    }

    private class CreatePaymentMethodTask extends AsyncTask<Object, Void, PaymentMethod> {

        @Override
        protected PaymentMethod doInBackground(Object... objects) {
            Stripe stripe = (Stripe) objects[0];
            PaymentMethodCreateParams cardParams = (PaymentMethodCreateParams) objects[1];

            try {
                return stripe.createPaymentMethodSynchronous(cardParams);

            } catch (StripeException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(PaymentMethod paymentMethod) {
            super.onPostExecute(paymentMethod);

            if (paymentMethod != null) {
                // Plata a fost procesată cu succes
                Toast.makeText(PaymentActivity.this, "Plata a fost procesată cu succes", Toast.LENGTH_SHORT).show();
            } else {
                // Plata a eșuat
                Toast.makeText(PaymentActivity.this, "Plata a eșuat", Toast.LENGTH_SHORT).show();
            }
        }
    }
}