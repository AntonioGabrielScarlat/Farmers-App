package database.cart;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;

import async.AsyncTaskRunner;
import async.Callback;
import database.DatabaseManager;


public class CartService {

    private final CartDao cartDao;
    private final AsyncTaskRunner asyncTaskRunner;

    public CartService(Context context) {
        this.cartDao = DatabaseManager.getInstance(context).getCartDao();
        this.asyncTaskRunner = new AsyncTaskRunner();
    }

    public void insert(Cart cart, Callback<Cart> activityThread) {
        //operation executata pe un alt thread
        Callable<Cart> insertOperation = new Callable<Cart>() {
            @Override
            public Cart call() {
                if (cart == null || cart.getId() > 0) {
                    return null;
                }
                long id = cartDao.insert(cart);
                if (id < 0) { //ceva nu a functionat in scriptul de insert
                    return null;
                }
                cart.setId(id);
                return cart;
            }
        };

        //pornire thread secundar
        asyncTaskRunner.executeAsync(insertOperation, activityThread);
    }

    public void getAll(Callback<List<Cart>> activityThread) {
        Callable<List<Cart>> selectOperation = new Callable<List<Cart>>() {
            @Override
            public List<Cart> call() {
                return cartDao.getAll();
            }
        };

        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void getCartByUserIdAndStatus(long introducedUserId, String introducedStatus, Callback<Cart> activityThread) {
        Callable<Cart> selectOperation = new Callable<Cart>() {
            @Override
            public Cart call() {
                return cartDao.getCartByUserIdAndStatus(introducedUserId,introducedStatus);
            }
        };

        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void update(Cart cart, Callback<Cart> activityThread) {
        Callable<Cart> updateOperation = new Callable<Cart>() {
            @Override
            public Cart call() {
                if (cart == null || cart.getId() <= 0) {
                    return null;
                }
                int count = cartDao.update(cart);
                if (count <= 0) {
                    return null;
                }
                return cart;
            }
        };

        asyncTaskRunner.executeAsync(updateOperation, activityThread);
    }

    public void delete(Cart cart, Callback<Boolean> activityThread) {
        Callable<Boolean> deleteOperation = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                if (cart == null || cart.getId() <= 0) {
                    return false;
                }
                int count = cartDao.delete(cart);
                return count > 0;
            }
        };

        asyncTaskRunner.executeAsync(deleteOperation, activityThread);
    }


}
