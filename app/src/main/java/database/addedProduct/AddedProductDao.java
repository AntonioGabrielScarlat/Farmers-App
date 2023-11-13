package database.addedProduct;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import database.cart.Cart;

@Dao
public interface AddedProductDao {

    @Insert
    long insert(AddedProduct addedProduct);

    @Query("select * from added_product")
    List<AddedProduct> getAll();

    @Query("select * from added_product where id_cart=(:introducedCartId)")
    List<AddedProduct> getAddedProductByCartId(long introducedCartId);

    @Query("select * from added_product where id_cart=(:introducedCartId) and id_product=(:introducedProductId)")
    AddedProduct getAddedProductByCartIdAndProductId(long introducedCartId, long introducedProductId);

    @Update
    int update(AddedProduct addedProduct);

    @Delete
    int delete(AddedProduct addedProduct);
}
