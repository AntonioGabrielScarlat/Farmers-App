package database.rating;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;

import async.AsyncTaskRunner;
import async.Callback;
import database.DatabaseManager;

public class RatingService {

    private final RatingDao ratingDao;
    private final AsyncTaskRunner asyncTaskRunner;

    public RatingService(Context context) {
        this.ratingDao = DatabaseManager.getInstance(context).getRatingDao();
        this.asyncTaskRunner = new AsyncTaskRunner();
    }

    public void insert(Rating rating, Callback<Rating> activityThread) {
        //operation executata pe un alt thread
        Callable<Rating> insertOperation = new Callable<Rating>() {
            @Override
            public Rating call() {
                if (rating == null || rating.getId() > 0) {
                    return null;
                }
                long id = ratingDao.insert(rating);
                if (id < 0) { //ceva nu a functionat in scriptul de insert
                    return null;
                }
                rating.setId(id);
                return rating;
            }
        };

        //pornire thread secundar
        asyncTaskRunner.executeAsync(insertOperation, activityThread);
    }

    public void getAll(Callback<List<Rating>> activityThread) {
        Callable<List<Rating>> selectOperation = new Callable<List<Rating>>() {
            @Override
            public List<Rating> call() {
                return ratingDao.getAll();
            }
        };

        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void getRatingByUserId(long introducedUserId, Callback<Rating> activityThread) {
        Callable<Rating> selectOperation = new Callable<Rating>() {
            @Override
            public Rating call() {
                return ratingDao.getRatingByUserId(introducedUserId);
            }
        };

        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void update(Rating rating, Callback<Rating> activityThread) {
        Callable<Rating> updateOperation = new Callable<Rating>() {
            @Override
            public Rating call() {
                if (rating == null || rating.getId() <= 0) {
                    return null;
                }
                int count = ratingDao.update(rating);
                if (count <= 0) {
                    return null;
                }
                return rating;
            }
        };

        asyncTaskRunner.executeAsync(updateOperation, activityThread);
    }

    public void delete(Rating rating, Callback<Boolean> activityThread) {
        Callable<Boolean> deleteOperation = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                if (rating == null || rating.getId() <= 0) {
                    return false;
                }
                int count = ratingDao.delete(rating);
                return count > 0;
            }
        };

        asyncTaskRunner.executeAsync(deleteOperation, activityThread);
    }

}
