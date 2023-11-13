package database.rating;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RatingDao {

    @Insert
    long insert(Rating rating);

    @Query("select * from rating")
    List<Rating> getAll();

    @Query("select * from rating where id_user=(:introducedUserId)")
    Rating getRatingByUserId(long introducedUserId);

    @Update
    int update(Rating rating);

    @Delete
    int delete(Rating rating);
}
