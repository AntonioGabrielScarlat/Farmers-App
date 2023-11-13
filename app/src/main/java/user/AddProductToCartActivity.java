package user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmersapp.R;

import java.util.Calendar;
import java.util.Date;

import async.Callback;
import database.addedProduct.AddedProduct;
import database.addedProduct.AddedProductService;
import database.product.Product;
import database.product.ProductService;
import database.user.User;

public class AddProductToCartActivity extends AppCompatActivity {
    public static final String ADDED_PRODUCT_KEY = "addedProductKey";
    private TextView tvCurrentProduct;
    private Spinner spnQuantity;
    private Button btnSubmit;

    private AddedProduct addedProduct;
    private long productId;
    private long cartId;

    private Intent intent;
    private AddedProductService addedProductService;
    private ProductService productService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_cart);
        initComponents();
        addedProductService=new AddedProductService(getApplicationContext());
        intent=getIntent();
        if (intent.hasExtra(ADDED_PRODUCT_KEY)) {
            addedProduct = (AddedProduct) intent.getSerializableExtra(ADDED_PRODUCT_KEY);
            productId=addedProduct.getIdProduct();
            cartId=addedProduct.getIdCart();
            createViewsFromProduct();
        }
        else
        {
            productId=intent.getLongExtra("id_product",productId);
            cartId=intent.getLongExtra("id_cart",cartId);
        }

        productService=new ProductService((getApplicationContext()));
        productService.getProductByProductId(productId,getProductByProductIdCallback());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.img);
        getSupportActionBar().setTitle("");
    }

    private void initComponents() {
        tvCurrentProduct =findViewById(R.id.add_product_to_cart_tv_current_product);
        spnQuantity=findViewById(R.id.add_product_to_cart_spn_quantity);
        btnSubmit =findViewById(R.id.add_product_to_cart_btn_submit);
        addQuantity();
        btnSubmit.setOnClickListener(addProductToCartEventListener());
    }

    private void createViewsFromProduct() {
        if (addedProduct == null) {
            return;
        }
        if (addedProduct.getQuantity() != null) {
            spnQuantity.setSelection(Integer.valueOf(Double.toString(addedProduct.getQuantity()).substring(0,Double.toString(addedProduct.getQuantity()).length()-2))-1);
        }

    }

    private Callback<Product> getProductByProductIdCallback() {
        return new Callback<Product>() {
            @Override
            public void runResultOnUiThread(Product result) {
                if (result != null) {
                    tvCurrentProduct.setText(result.getName());
                }
            }
        };
    }

    private View.OnClickListener addProductToCartEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    createFromViews();
                    intent.putExtra(ADDED_PRODUCT_KEY, addedProduct);
                    setResult(RESULT_OK, intent);
                    finish();

            }
        };
    }

    private void createFromViews() {
        Double quantity = Double.parseDouble(spnQuantity.getSelectedItem().toString());
        if(addedProduct==null){
            addedProduct=new AddedProduct(quantity,productId,cartId);
        }
        else
        {
            addedProduct.setQuantity(quantity);
        }

    }

    private void addQuantity() {
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.add_product_to_cart_quantity_values,
                R.layout.support_simple_spinner_dropdown_item);
        spnQuantity.setAdapter(adapter);
    }
}