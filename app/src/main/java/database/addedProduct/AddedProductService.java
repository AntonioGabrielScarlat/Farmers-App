package database.addedProduct;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;

import async.AsyncTaskRunner;
import async.Callback;
import database.DatabaseManager;


public class AddedProductService {

    private final AddedProductDao addedProductDao;
    private final AsyncTaskRunner asyncTaskRunner;

    public AddedProductService(Context context) {
        this.addedProductDao = DatabaseManager.getInstance(context).getAddedProductDao();
        this.asyncTaskRunner = new AsyncTaskRunner();
    }

    public void insert(AddedProduct addedProduct, Callback<AddedProduct> activityThread) {
        //operation executata pe un alt thread
        Callable<AddedProduct> insertOperation = new Callable<AddedProduct>() {
            @Override
            public AddedProduct call() {
                if (addedProduct == null || addedProduct.getId() > 0) {
                    return null;
                }
                long id = addedProductDao.insert(addedProduct);
                if (id < 0) { //ceva nu a functionat in scriptul de insert
                    return null;
                }
                addedProduct.setId(id);
                return addedProduct;
            }
        };

        //pornire thread secundar
        asyncTaskRunner.executeAsync(insertOperation, activityThread);
    }

    public void getAll(Callback<List<AddedProduct>> activityThread) {
        Callable<List<AddedProduct>> selectOperation = new Callable<List<AddedProduct>>() {
            @Override
            public List<AddedProduct> call() {
                return addedProductDao.getAll();
            }
        };

        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void getAddedProductByCartId(long introducedCartId, Callback<List<AddedProduct>> activityThread) {
        Callable<List<AddedProduct>> selectOperation = new Callable<List<AddedProduct>>() {
            @Override
            public List<AddedProduct> call() {
                return addedProductDao.getAddedProductByCartId(introducedCartId);
            }
        };

        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void getAddedProductByCartIdAndProductId(long introducedCartId, long introducedProductId, Callback<AddedProduct> activityThread) {
        Callable<AddedProduct> selectOperation = new Callable<AddedProduct>() {
            @Override
            public AddedProduct call() {
                return addedProductDao.getAddedProductByCartIdAndProductId(introducedCartId,introducedProductId);
            }
        };

        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void update(AddedProduct addedProduct, Callback<AddedProduct> activityThread) {
        Callable<AddedProduct> updateOperation = new Callable<AddedProduct>() {
            @Override
            public AddedProduct call() {
                if (addedProduct == null || addedProduct.getId() <= 0) {
                    return null;
                }
                int count = addedProductDao.update(addedProduct);
                if (count <= 0) {
                    return null;
                }
                return addedProduct;
            }
        };

        asyncTaskRunner.executeAsync(updateOperation, activityThread);
    }

    public void delete(AddedProduct addedProduct, Callback<Boolean> activityThread) {
        Callable<Boolean> deleteOperation = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                if (addedProduct == null || addedProduct.getId() <= 0) {
                    return false;
                }
                int count = addedProductDao.delete(addedProduct);
                return count > 0;
            }
        };

        asyncTaskRunner.executeAsync(deleteOperation, activityThread);
    }

}
