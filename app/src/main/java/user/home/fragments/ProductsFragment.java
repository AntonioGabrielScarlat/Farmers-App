package user.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import async.Callback;
import auxiliary.adapters.ProductAdapter;
import database.addedProduct.AddedProduct;
import database.addedProduct.AddedProductService;
import database.cart.Cart;
import database.cart.CartService;
import database.product.Product;
import database.product.ProductService;
import other.RegisterActivity;
import user.AddProductToCartActivity;

public class ProductsFragment extends Fragment {
    public static final String ADDED_PRODUCT_KEY = "addedProductKey";

    private AutoCompleteTextView actvProductSearch;
    private Button btnSearch;
    private ListView lvProducts;

    private String[] productsNames;
    private long userId;
    private long cartId;
    private Double total;
    private Double productPrice;
    private List<Product> products=new ArrayList<>();

    private ActivityResultLauncher<Intent> addProductToCartLauncher;
    private ActivityResultLauncher<Intent> updateProductToCartLauncher;
    private AddedProductService addedProductService;
    private ProductService productService;
    private CartService cartService;

    public ProductsFragment(long userId) {
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_user_products,container,false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        actvProductSearch=view.findViewById(R.id.fragment_user_products_actv_product_name);
        btnSearch=view.findViewById(R.id.fragment_user_products_btn_search);
        lvProducts=view.findViewById(R.id.fragment_user_products_lv_products);
        addAdapter();
        lvProducts.setOnItemClickListener(addProductToCartEventListener());
        btnSearch.setOnClickListener(addProductToCartAfterSearchListener());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addProductToCartLauncher = getAddProductToCartLauncher();
        updateProductToCartLauncher = getUpdateProductToCartLauncher();
        addedProductService=new AddedProductService(getContext().getApplicationContext());
        productService=new ProductService(getContext().getApplicationContext());
        cartService=new CartService(getContext().getApplicationContext());
        total=0.0;
        productPrice=0.0;
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
                    List<String> listProductNames=new ArrayList<String>();
                    for(Product product:products){
                        listProductNames.add(product.getName());
                    }
                    productsNames =new String[listProductNames.size()];
                    productsNames =listProductNames.toArray(productsNames);
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                            getContext().getApplicationContext(),android.R.layout.simple_list_item_1,productsNames);
                    actvProductSearch.setAdapter(adapter);

                    cartService.getCartByUserIdAndStatus(userId,"Current",getCurrentCartByUserIdAndStatusCallback());
                }
            }
        };
    }

    private Callback<Cart> getCurrentCartByUserIdAndStatusCallback() {
        return new Callback<Cart>() {
            @Override
            public void runResultOnUiThread(Cart result) {
                if (result != null) {
                    cartId=result.getId();
                    total=result.getTotal();
                }
                else
                {
                    Cart newCart=new Cart(0.0,"Current",userId);
                    cartService.insert(newCart, getNewCartCallback());
                }
            }
        };
    }

    private Callback<Cart> getNewCartCallback() {
        return new Callback<Cart>() {
            @Override
            public void runResultOnUiThread(Cart result) {
                if (result != null) {
                    cartId=result.getId();
                    total=result.getTotal();
                }
            }
        };
    }

    private AdapterView.OnItemClickListener addProductToCartEventListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                productPrice=products.get(position).getPrice();
                addedProductService.getAddedProductByCartIdAndProductId(cartId, products.get(position).getId(),getAddedProductByCartIdAndProductIdCallback(products.get(position)));
            }
        };
    }

    private Callback<AddedProduct> getAddedProductByCartIdAndProductIdCallback(Product product) {
        return new Callback<AddedProduct>() {
            @Override
            public void runResultOnUiThread(AddedProduct addedProduct) {
                if (addedProduct != null) {
                    total=total-productPrice*addedProduct.getQuantity();
                    Intent intent = new Intent(getContext().getApplicationContext(), AddProductToCartActivity.class);
                    intent.putExtra(AddProductToCartActivity.ADDED_PRODUCT_KEY, addedProduct);
                    updateProductToCartLauncher.launch(intent);
                }
                else
                {
                    Intent intent = new Intent(getContext().getApplicationContext(), AddProductToCartActivity.class);
                    intent.putExtra("id_product", product.getId());
                    intent.putExtra("id_cart", cartId);
                    addProductToCartLauncher.launch(intent);
                }
            }
        };
    }

    private ActivityResultLauncher<Intent> getAddProductToCartLauncher() {
        ActivityResultCallback<ActivityResult> callback = getAddProductActivityResultCallback();
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback);
    }

    private ActivityResultCallback<ActivityResult> getAddProductActivityResultCallback() {
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result != null && result.getResultCode() == -1 && result.getData() != null) {
                    AddedProduct addedProduct = (AddedProduct) result.getData().getSerializableExtra(AddProductToCartActivity.ADDED_PRODUCT_KEY);
                    //inserare in baza de date
                    total=total+productPrice*addedProduct.getQuantity();
                    addedProductService.insert(addedProduct, getInsertAddedProductCallback());
                }
            }
        };
    }

    private Callback<AddedProduct> getInsertAddedProductCallback() {
        return new Callback<AddedProduct>() {
            @Override
            public void runResultOnUiThread(AddedProduct addedProduct) {
                if (addedProduct != null) {
                    cartService.getCartByUserIdAndStatus(userId,"Current",getCurrentCartByUserIdAndStatusAfterInsertCallback());


                }
            }
        };
    }

    private Callback<Cart> getCurrentCartByUserIdAndStatusAfterInsertCallback() {
        return new Callback<Cart>() {
            @Override
            public void runResultOnUiThread(Cart result) {
                if (result != null) {
                    result.setTotal(total);
                    cartService.update(result,updateCartCallback());

                }
            }
        };
    }

    private Callback<Cart> updateCartCallback() {
        return new Callback<Cart>() {
            @Override
            public void runResultOnUiThread(Cart result) {
                if (result != null) {
                    Toast.makeText(getContext(),
                            "Product added/modified to cart!",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
    }

    private ActivityResultLauncher<Intent> getUpdateProductToCartLauncher() {
        ActivityResultCallback<ActivityResult> callback = getUpdateProductActivityResultCallback();
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback);
    }

    private ActivityResultCallback<ActivityResult> getUpdateProductActivityResultCallback() {
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result != null && result.getResultCode() == -1 && result.getData() != null) {
                    AddedProduct addedProduct = (AddedProduct) result.getData().getSerializableExtra(AddProductToCartActivity.ADDED_PRODUCT_KEY);
                    //inserare in baza de date
                    total=total+productPrice*addedProduct.getQuantity();
                    addedProductService.update(addedProduct, getInsertAddedProductCallback());
                }
            }
        };
    }

    private View.OnClickListener addProductToCartAfterSearchListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actvProductSearch.getText().toString().length()>0)
                {
                    productService.getProductByProductName(actvProductSearch.getText().toString(),getProductByProductNameCallback());
                }
                else
                {
                    Toast.makeText(getContext(),
                            "You have to enter a product to search it!",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
    }

    private Callback<Product> getProductByProductNameCallback() {
        return new Callback<Product>() {
            @Override
            public void runResultOnUiThread(Product result) {
                if (result != null) {
                    productPrice=result.getPrice();
                    addedProductService.getAddedProductByCartIdAndProductId(cartId, result.getId(),getAddedProductByCartIdAndProductIdCallback(result));
                }
                else
                {
                    Toast.makeText(getContext(),
                            "Product doesn't exist. Try Searching again!",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
    }

    private void addAdapter() {
        ProductAdapter adapter = new ProductAdapter(getContext().getApplicationContext(), R.layout.lv_product_row,
                products , getLayoutInflater());
        lvProducts.setAdapter(adapter);
    }

    public void notifyAdapter() {
        ArrayAdapter<Product> adapter = (ArrayAdapter<Product>) lvProducts.getAdapter();
        adapter.notifyDataSetChanged();
    }
}

