package database.cart;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {

    @Insert
    long insert(Cart cart);

    @Query("select * from cart")
    List<Cart> getAll();

    @Query("select * from cart where id_user=(:introducedUserId) and status=(:introducedStatus)")
    Cart getCartByUserIdAndStatus(long introducedUserId, String introducedStatus);

    @Update
    int update(Cart cart);

    @Delete
    int delete(Cart cart);
}
