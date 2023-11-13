package database.user;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;

import async.AsyncTaskRunner;
import async.Callback;
import database.DatabaseManager;


public class UserService {
    private final UserDao userDao;
    private final AsyncTaskRunner asyncTaskRunner;

    public UserService(Context context) {
        this.userDao = DatabaseManager.getInstance(context).getUserDao();
        this.asyncTaskRunner = new AsyncTaskRunner();
    }

    public void insert(User user, Callback<User> activityThread) {
        Callable<User> insertOperation = new Callable<User>() {
            @Override
            public User call() {
                if (user == null || user.getId() > 0) {
                    return null;
                }
                long id = userDao.insert(user);
                if (id < 0) {
                    return null;
                }
                user.setId(id);
                return user;
            }
        };
        asyncTaskRunner.executeAsync(insertOperation, activityThread);
    }

    public void update(User user, Callback<User> activityThread) {
        Callable<User> updateOperation = new Callable<User>() {
            @Override
            public User call() {
                if (user == null || user.getId() <= 0) {
                    return null;
                }
                int count = userDao.update(user);
                if (count <= 0) {
                    return null;
                }
                return user;
            }
        };
        asyncTaskRunner.executeAsync(updateOperation, activityThread);
    }

    public void delete(User user, Callback<Boolean> activityThread) {
        Callable<Boolean> deleteOperation = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                if (user == null || user.getId() <= 0) {
                    return false;
                }
                int count = userDao.delete(user);
                return count > 0;
            }
        };
        asyncTaskRunner.executeAsync(deleteOperation, activityThread);
    }

    public void getAll(Callback<List<User>> activityThread) {
        Callable<List<User>> selectOperation = new Callable<List<User>>() {
            @Override
            public List<User> call() {
                return userDao.getAll();
            }
        };
        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void login(String introducedUsername,String introducedPassword, Callback<User> activityThread) {
        Callable<User> selectOperation = new Callable<User>() {
            @Override
            public User call() {
                return userDao.login(introducedUsername,introducedPassword);
            }
        };
        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void getUserById(long introducedId, Callback<User> activityThread) {
        Callable<User> selectOperation = new Callable<User>() {
            @Override
            public User call() {
                return userDao.getUserById(introducedId);
            }
        };
        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    }

    public void forgotPassword(String introducedUsername,String introducedAnswer, Callback<User> activityThread) {
        Callable<User> selectOperation = new Callable<User>() {
            @Override
            public User call() {
                return userDao.forgotPassword(introducedUsername,introducedAnswer);
            }
        };
        asyncTaskRunner.executeAsync(selectOperation, activityThread);
    } }
