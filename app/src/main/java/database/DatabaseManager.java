package database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import database.addedProduct.AddedProduct;
import database.addedProduct.AddedProductDao;
import database.cart.Cart;
import database.cart.CartDao;
import database.product.Product;
import database.product.ProductDao;
import database.rating.Rating;
import database.rating.RatingDao;

import database.user.User;
import database.user.UserDao;


@Database(entities = {User.class, AddedProduct.class,
        Product.class, Cart.class, Rating.class}
        , exportSchema = false, version = 1)
public abstract class DatabaseManager extends RoomDatabase {
    public static final String farmers_app_DB = "farmers_app_DB";
    private static DatabaseManager databaseManager;
    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (databaseManager == null) {
                    databaseManager = Room.databaseBuilder(context, DatabaseManager.class, farmers_app_DB)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return databaseManager;
    }
    public abstract UserDao getUserDao();
    public abstract AddedProductDao getAddedProductDao();
    public abstract ProductDao getProductDao();
    public abstract CartDao getCartDao();
    public abstract RatingDao getRatingDao();  }
