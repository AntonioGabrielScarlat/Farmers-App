package user.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.farmersapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import async.Callback;
import database.cart.Cart;
import database.cart.CartService;
import database.product.Product;
import database.product.ProductService;
import user.home.fragments.ProductsFragment;
import user.home.fragments.CartFragment;
import user.home.fragments.ProfileFragment;


public class UserHomeActivity extends AppCompatActivity {
    private long userId;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.img);
        getSupportActionBar().setTitle("");
        intent=getIntent();
        userId=intent.getLongExtra("user_id",userId);
        BottomNavigationView bottomNavigationView=findViewById(R.id.user_home_bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.user_home_fragment_container,
                new ProductsFragment(userId)).commit();
    }

    private NavigationBarView.OnItemSelectedListener navigationItemSelectedListener= new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment=null;
            switch(item.getItemId()){
                case R.id.nav_user_products:
                    selectedFragment=new ProductsFragment(userId);
                    break;
                case R.id.nav_user_cart:
                    selectedFragment=new CartFragment(userId);
                    break;
                case R.id.nav_user_profile:
                    selectedFragment=new ProfileFragment(userId);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.user_home_fragment_container,
                    selectedFragment).commit();
            return true;
        }
    };
}