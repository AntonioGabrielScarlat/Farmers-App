package database.product;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;

import async.AsyncTaskRunner;
import async.Callback;
import database.DatabaseManager;


public class ProductService {

    private final ProductDao productDao;
    private final AsyncTaskRunner asyncTaskRunner;

    public ProductService(Context context) {
        this.productDao = DatabaseManager.getInstance(context).getProductDao();
        this.asyncTaskRunner = new AsyncTaskRunner();
    }

    public void insert(Product product, Callback<Product> activityThread) {
        //operation executata pe un alt thread
        Callable<Product> insertOperation = new Callable<Product>() {
            @Override
            public Product call() {
                if (product == null || product.getId() > 0) {
                    return null;
                }
                long id = productDao.insert(product);
                if (id < 0) { //ceva nu a functionat in scriptul de insert
                    return null;
                }
                product.setId(id);
                return product;
            }
        };

        //pornire thread secundar
        asyncTaskRunner.executeAsync(insertOperation, activityThread);
    }

    public void getAll(Callback<List<Product>> activityThread) {
        Callable<List<Product>> selectOperation = new Callable<List<Product>>() {
            @Override
            public List<Product> call() {
                return productDao.getAll();
            }
        };

        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void getProductByProductId(long introducedProductId, Callback<Product> activityThread) {
        Callable<Product> selectOperation = new Callable<Product>() {
            @Override
            public Product call() {
                return productDao.getProductByProductId(introducedProductId);
            }
        };

        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void getProductByProductName(String introducedProductName, Callback<Product> activityThread) {
        Callable<Product> selectOperation = new Callable<Product>() {
            @Override
            public Product call() {
                return productDao.getProductByProductName(introducedProductName);
            }
        };

        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void update(Product product, Callback<Product> activityThread) {
        Callable<Product> updateOperation = new Callable<Product>() {
            @Override
            public Product call() {
                if (product == null || product.getId() <= 0) {
                    return null;
                }
                int count = productDao.update(product);
                if (count <= 0) {
                    return null;
                }
                return product;
            }
        };

        asyncTaskRunner.executeAsync(updateOperation, activityThread);
    }

    public void delete(Product product, Callback<Boolean> activityThread) {
        Callable<Boolean> deleteOperation = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                if (product == null || product.getId() <= 0) {
                    return false;
                }
                int count = productDao.delete(product);
                return count > 0;
            }
        };

        asyncTaskRunner.executeAsync(deleteOperation, activityThread);
    }


}
