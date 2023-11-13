package admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmersapp.R;
import com.google.android.material.textfield.TextInputEditText;

import database.product.Product;

public class AddProductActivity extends AppCompatActivity {
    public static final String PRODUCT_KEY = "productKey";

    private TextInputEditText tietName;
    private TextInputEditText tietPrice;
    private Button btnSubmit;

    private Product product;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        initComponents();
        intent=getIntent();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.img);
        getSupportActionBar().setTitle("");
    }

    private void initComponents() {
        tietName=findViewById(R.id.add_products_tiet_name);
        tietPrice =findViewById(R.id.add_products_tiet_price);
        btnSubmit =findViewById(R.id.add_products_btn_submit);
        btnSubmit.setOnClickListener(saveProductEventListener());
    }

    private View.OnClickListener saveProductEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    createFromViews();
                    intent.putExtra(PRODUCT_KEY, product);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        };
    }

    private boolean isValid() {

        if (tietName.getText() == null || tietName.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Invalid name!",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (tietPrice.getText() == null || tietPrice.getText().toString().isEmpty()
                || Double.parseDouble(tietPrice.getText().toString()) < 0) {
            Toast.makeText(getApplicationContext(),
                    "Invalid price!",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private void createFromViews() {
        String name = tietName.getText().toString();
        Double price = Double.parseDouble(tietPrice.getText().toString());
            product = new Product(name, price);
    }
}