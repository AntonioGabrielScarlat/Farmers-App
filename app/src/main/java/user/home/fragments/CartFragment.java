package user.home.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmersapp.R;
import com.stripe.android.Stripe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import async.Callback;
import auxiliary.adapters.AddedProductAdapter;
import auxiliary.listed.ListedProduct;
import database.DatabaseManager;
import database.addedProduct.AddedProduct;
import database.addedProduct.AddedProductService;
import database.cart.Cart;
import database.cart.CartService;
import database.product.Product;
import database.product.ProductDao;
import database.product.ProductService;
import user.AddProductToCartActivity;
import user.PaymentActivity;

public class CartFragment extends Fragment {

    private Button btnPay;
    private Button btnDeleteCart;
    private TextView tvTotal;
    private ListView lvAddedProducts;
    private AlertDialog.Builder builder;

    private List<Product> products=new ArrayList<>();
    private List<AddedProduct> addedProducts=new ArrayList<>();
    private List<ListedProduct> listedProducts =new ArrayList<>();
    private long userId;
    private long cartId;
    private Double total;
    private Double productPrice;


    private ActivityResultLauncher<Intent> modifyProductLauncher;
    private AddedProductService addedProductService;
    private ProductService productService;
    private CartService cartService;

    public CartFragment(long userId) {
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_user_cart,container,false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
       btnPay =view.findViewById(R.id.fragment_user_cart_btn_pay);
       btnDeleteCart=view.findViewById(R.id.fragment_user_cart_btn_delete_cart);
       tvTotal=view.findViewById(R.id.fragment_user_cart_tv_total);
       lvAddedProducts =view.findViewById(R.id.fragment_user_cart_lv_added_products);
       addAdapter();
       lvAddedProducts.setOnItemClickListener(modifyProductToCartEventListener());
       lvAddedProducts.setOnItemLongClickListener(deleteProductFromCartListener());
       btnPay.setOnClickListener(payEventListener());
       btnDeleteCart.setOnClickListener(deleteCartEventListener());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        builder = new AlertDialog.Builder(this.getContext());
        modifyProductLauncher = getModifyProductLauncher();
        total=0.0;
        addedProductService=new AddedProductService(getContext().getApplicationContext());
        productService=new ProductService(getContext().getApplicationContext());
        cartService=new CartService(getContext().getApplicationContext());
        productService.getAll(getAllProductsCallback());
    }

    private Callback<List<Product>> getAllProductsCallback() {
        return new Callback<List<Product>>() {
            @Override
            public void runResultOnUiThread(List<Product> results) {
                if (results != null) {
                    products.clear();
                    products.addAll(results);
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
                    DecimalFormat decimalFormat=new DecimalFormat("###.#");
                    tvTotal.setText(decimalFormat.format(total)+" RON");
                    addedProductService.getAddedProductByCartId(cartId, getAllAddedProductsByCartIdCallback());
                }
            }
        };
    }

    private Callback<List<AddedProduct>> getAllAddedProductsByCartIdCallback() {
        return new Callback<List<AddedProduct>>() {
            @Override
            public void runResultOnUiThread(List<AddedProduct> results) {
                if (results != null) {
                    for(AddedProduct addedProduct:results){
                        for(Product product:products){
                            if(product.getId()==addedProduct.getIdProduct())
                            {
                                listedProducts.add(new ListedProduct(product,addedProduct.getQuantity()));
                                notifyAdapter();
                            }
                        }
                    }
                    notifyAdapter();
                }
            }
        };
    }

    private AdapterView.OnItemClickListener modifyProductToCartEventListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                productPrice=listedProducts.get(position).getProduct().getPrice();
                addedProductService.getAddedProductByCartIdAndProductId(cartId,listedProducts.get(position).getProduct().getId(),getAddedProductByCartIdAndProductIdCallback());
            }
        };
    }

    private Callback<AddedProduct> getAddedProductByCartIdAndProductIdCallback() {
        return new Callback<AddedProduct>() {
            @Override
            public void runResultOnUiThread(AddedProduct addedProduct) {
                if (addedProduct != null) {
                    total=total-productPrice*addedProduct.getQuantity();
                    Intent intent = new Intent(getContext().getApplicationContext(), AddProductToCartActivity.class);
                    intent.putExtra(AddProductToCartActivity.ADDED_PRODUCT_KEY, addedProduct);
                    modifyProductLauncher.launch(intent);
                }
            }
        };
    }

    private ActivityResultLauncher<Intent> getModifyProductLauncher() {
        ActivityResultCallback<ActivityResult> callback = getModifyProductActivityResultCallback();
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback);
    }

    private ActivityResultCallback<ActivityResult> getModifyProductActivityResultCallback() {
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result != null && result.getResultCode() == -1 && result.getData() != null) {
                    AddedProduct addedProduct = (AddedProduct) result.getData().getSerializableExtra(AddProductToCartActivity.ADDED_PRODUCT_KEY);
                    addedProductService.update(addedProduct, getUpdateAddedProductCallback());
                }
            }
        };
    }

    private Callback<AddedProduct> getUpdateAddedProductCallback() {
        return new Callback<AddedProduct>() {
            @Override
            public void runResultOnUiThread(AddedProduct addedProduct) {
                if (addedProduct != null) {
                    total=total+productPrice*addedProduct.getQuantity();
                    for(ListedProduct listedProduct:listedProducts){
                        if(listedProduct.getProduct().getId()==addedProduct.getIdProduct()){
                            listedProduct.setQuantity(addedProduct.getQuantity());
                        }
                    }
                    Toast.makeText(getContext(),
                            "Product updated inside cart!",
                            Toast.LENGTH_SHORT)
                            .show();
                    cartService.getCartByUserIdAndStatus(userId,"Current",getCurrentCartByUserIdAndStatusAfterUpdateCallback());
                }
            }
        };
    }

    private Callback<Cart> getCurrentCartByUserIdAndStatusAfterUpdateCallback() {
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
                    notifyAdapter();
                    DecimalFormat decimalFormat=new DecimalFormat("###.#");
                    tvTotal.setText(decimalFormat.format(result.getTotal())+" RON");
                    if(listedProducts.size()==0){
                        cartService.delete(result,deleteCartCallback());
                    }
                }
            }
        };
    }

    private Callback<Boolean> deleteCartCallback() {
        return new Callback<Boolean>() {
            @Override
            public void runResultOnUiThread(Boolean result) {
                if (result) {
                    notifyAdapter();
                    tvTotal.setText("0 RON");
                    Toast.makeText(getContext().getApplicationContext(),"Empty cart!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private AdapterView.OnItemLongClickListener deleteProductFromCartListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                builder.setMessage("Are you sure you want to remove the product "+listedProducts.get(position).getProduct().getName()+" from the cart?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                addedProductService.getAddedProductByCartIdAndProductId(cartId, listedProducts.get(position).getProduct().getId(),getProductFromCartCallback());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Remove product from cart");
                alert.show();
                return true;
            }

        };
    }

    private Callback<AddedProduct> getProductFromCartCallback() {
        return new Callback<AddedProduct>() {
            @Override
            public void runResultOnUiThread(AddedProduct result) {
                if (result != null) {
                    for(ListedProduct listedProduct:listedProducts){
                        if(listedProduct.getProduct().getId()==result.getIdProduct()){
                            addedProductService.delete(result,deleteProductFromCartCallback(listedProducts.indexOf(listedProduct)));
                        }
                    }


                }
            }
        };
    }

    private Callback<Boolean> deleteProductFromCartCallback(int position) {
        return new Callback<Boolean>() {
            @Override
            public void runResultOnUiThread(Boolean result) {
                if (result) {
                    total=total-listedProducts.get(position).getProduct().getPrice()*listedProducts.get(position).getQuantity();
                    listedProducts.remove(position);
                    notifyAdapter();
                    cartService.getCartByUserIdAndStatus(userId,"Current",getCurrentCartByUserIdAndStatusAfterProductDeleteCallback());
                }
            }
        };
    }

    private Callback<Cart> getCurrentCartByUserIdAndStatusAfterProductDeleteCallback() {
        return new Callback<Cart>() {
            @Override
            public void runResultOnUiThread(Cart result) {
                if (result != null) {
                    Toast.makeText(getContext().getApplicationContext(),"Product successfully removed from cart!",
                            Toast.LENGTH_SHORT).show();
                    result.setTotal(total);
                    cartService.update(result,updateCartCallback());
                }
            }
        };
    }

    private View.OnClickListener payEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), PaymentActivity.class);
                intent.putExtra("total", total);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener deleteCartEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartService.getCartByUserIdAndStatus(userId,"Current",getCurrentCartByUserIdAndStatusAfterDeleteCallback());
            }
        };
    }

    private Callback<Cart> getCurrentCartByUserIdAndStatusAfterDeleteCallback() {
        return new Callback<Cart>() {
            @Override
            public void runResultOnUiThread(Cart result) {
                if (result != null) {
                    listedProducts.clear();
                    cartService.delete(result,deleteCartCallback());
                }
            }
        };
    }

    private void addAdapter() {
        AddedProductAdapter adapter = new AddedProductAdapter(getContext().getApplicationContext(), R.layout.lv_added_product_row,
               listedProducts , getLayoutInflater());
        lvAddedProducts.setAdapter(adapter);
    }

    public void notifyAdapter() {
        ArrayAdapter<ListedProduct> adapter = (ArrayAdapter<ListedProduct>) lvAddedProducts.getAdapter();
        adapter.notifyDataSetChanged();
    }











}
