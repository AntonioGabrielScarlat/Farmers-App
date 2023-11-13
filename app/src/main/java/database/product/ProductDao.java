package database.product;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    long insert(Product product);

    @Query("select * from Product")
    List<Product> getAll();

    @Query("select * from Product where id=(:introducedProductId)")
    Product getProductByProductId(long introducedProductId);

    @Query("select * from Product where name=(:introducedProductName)")
    Product getProductByProductName(String introducedProductName);

    @Update
    int update(Product product);

    @Delete
    int delete(Product product);
}
