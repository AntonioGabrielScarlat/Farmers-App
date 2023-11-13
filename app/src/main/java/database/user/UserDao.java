package database.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    int update(User user);

    @Delete
    int delete(User user);

    @Query("select * from user")
    List<User> getAll();

    @Query("select * from user where username=(:introducedUsername) and password=(:introducedPassword)")
    User login(String introducedUsername, String introducedPassword);

    @Query("select * from user where id=(:introducedId)")
    User getUserById(long introducedId);

    @Query("select * from user where username=(:introducedUsername) and security_answer=(:introducedAnswer)")
    User forgotPassword(String introducedUsername, String introducedAnswer);
}
