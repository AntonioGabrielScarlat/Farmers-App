package admin.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmersapp.R;

import java.util.ArrayList;
import java.util.List;

import admin.AddProductActivity;
import async.Callback;
import auxiliary.adapters.ProductAdapter;
import database.product.Product;
import database.product.ProductService;


public class ProductsChangesFragment extends Fragment {
    public static final String PRODUCT_KEY = "productKey";

    private Button btnAddProduct;
    private ListView lvProducts;

    private ArrayList<Product> products =new ArrayList<>();

    private ActivityResultLauncher<Intent> addProductLauncher;
    private ProductService productService;

    public ProductsChangesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_admin_products,container,false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        btnAddProduct =view.findViewById(R.id.fragment_admin_products_btn_add_product);
        lvProducts =view.findViewById(R.id.fragment_admin_products_lv_products);
        addAdapter();
        btnAddProduct.setOnClickListener(addProductEventListener());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addProductLauncher = getAddProductLauncher();
        productService =new ProductService(getContext().getApplicationContext());
        productService.getAll(getAllProductsCallback());
    }

    private Callback<List<Product>> getAllProductsCallback() {
        return new Callback<List<Product>>() {
            @Override
            public void runResultOnUiThread(List<Product> results) {
                if (results != null) {
                    products.clear();
                    products.addAll(results);
                    notifyAdapter();
                }
            }
        };
    }

    private View.OnClickListener addProductEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), AddProductActivity.class);
                addProductLauncher.launch(intent);
            }
        };
    }


    private ActivityResultLauncher<Intent> getAddProductLauncher() {
        ActivityResultCallback<ActivityResult> callback = getAddProductActivityResultCallback();
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback);
    }

    private ActivityResultCallback<ActivityResult> getAddProductActivityResultCallback() {
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result != null && result.getResultCode() == -1 && result.getData() != null) {
                    Product product = (Product) result.getData().getSerializableExtra(AddProductActivity.PRODUCT_KEY);
                    //inserare in baza de date
                    productService.insert(product, getInsertProductCallback());
                }
            }
        };
    }

    private Callback<Product> getInsertProductCallback() {
        return new Callback<Product>() {
            @Override
            public void runResultOnUiThread(Product product) {
                if (product != null) {
                    products.add(product);
                    notifyAdapter();
                    Toast.makeText(getContext().getApplicationContext(),"Product successfully added!",Toast.LENGTH_SHORT).show();

                }
            }
        };
    }

    private void addAdapter() {
        ProductAdapter adapter = new ProductAdapter(getContext().getApplicationContext(), R.layout.lv_product_row,
                products, getLayoutInflater());
        lvProducts.setAdapter(adapter);
    }

    public void notifyAdapter() {
        ArrayAdapter<Product> adapter = (ArrayAdapter<Product>) lvProducts.getAdapter();
        adapter.notifyDataSetChanged();
    }

}
