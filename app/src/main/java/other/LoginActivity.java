package other;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import com.example.farmersapp.R;

import admin.home.AdminHomeActivity;
import user.ForgotPasswordActivity;
import user.home.UserHomeActivity;
import async.Callback;
import database.user.User;
import database.user.UserService;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText username;
    private TextInputEditText password;
    private Button login;
    private TextView tvForgotPassword;
    private Button btnRegister;

    private List<User> users = new ArrayList<>();

    private ActivityResultLauncher<Intent> addUserLauncher;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();
        addUserLauncher = getAddUserLauncher();
        userService = new UserService(getApplicationContext());
        userService.getAll(getAllUsersCallback());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.img);
        getSupportActionBar().setTitle("");
    }


    private void initComponents() {
        username=findViewById(R.id.login_tiet_username);
        password=findViewById(R.id.login_tiet_password);
        tvForgotPassword=findViewById(R.id.login_tv_forgot_password);
        btnRegister = findViewById(R.id.login_btn_register);
        btnRegister.setOnClickListener(addUserEventListener());
        login=findViewById(R.id.login_btn_submit);
        login.setOnClickListener(loginEventListener());
        tvForgotPassword.setOnClickListener(forgotPasswordEventListener());

    }

    private View.OnClickListener forgotPasswordEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);

            }
        };
    }

    private View.OnClickListener loginEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    userService.login(username.getText().toString(),password.getText().toString(),loginCallback());
                }

            }
        };
    }

    private boolean isValid() {
        if (username.getText() == null || username.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Invalid username!",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (password.getText() == null || password.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Invalid password!",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        for(User user:users){
        if (username.getText().toString().equals(user.getUsername()) && password.getText().toString().equals(user.getPassword())) {
            return true;
        }
        }
        Toast.makeText(getApplicationContext(),"User not found!",Toast.LENGTH_SHORT).show();
        return false;

    }

    private View.OnClickListener addUserEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                addUserLauncher.launch(intent);
            }
        };
    }

    private ActivityResultLauncher<Intent> getAddUserLauncher() {
        ActivityResultCallback<ActivityResult> callback = getAddUserActivityResultCallback();
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback);
    }

    private ActivityResultCallback<ActivityResult> getAddUserActivityResultCallback() {
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
                    User user = (User) result.getData().getSerializableExtra(RegisterActivity.USER_KEY);
                    userService.insert(user, getInsertUserCallback());
                }
            }
        };
    }

    private ActivityResultLauncher<Intent> getUpdateUserLauncher() {
        ActivityResultCallback<ActivityResult> callback = getUpdateUserActivityResultCallback();
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback);
    }

    private ActivityResultCallback<ActivityResult> getUpdateUserActivityResultCallback() {
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
                    User user = (User) result.getData().getSerializableExtra(RegisterActivity.USER_KEY);
                    userService.update(user, updateUserCallback());
                }
            }
        };
    }

    //------------------------------ SQLite -------------------------------
    private Callback<User> getInsertUserCallback() {
        return new Callback<User>() {
            @Override
            public void runResultOnUiThread(User user) {
                if (user != null) {
                    users.add(user);
                    if(!user.getUsername().equals("admin") && !user.getPassword().equals("admin"))
                    {
                        Toast.makeText(getApplicationContext(),"User successfully registered!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    private Callback<List<User>> getAllUsersCallback() {
        return new Callback<List<User>>() {
            @Override
            public void runResultOnUiThread(List<User> results) {
                if (results != null) {
                    users.clear();
                    users.addAll(results);
                }
            }
        };
    }

    private Callback<User> loginCallback() {
        return new Callback<User>() {
            @Override
            public void runResultOnUiThread(User result) {
                if (result != null) {
                        if(result.getUsername().toString().equals("admin")&&result.getPassword().toString().equals("admin")){
                            Toast.makeText(getApplicationContext(),"Admin logged in successfully!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                                    Toast.makeText(getApplicationContext(),"User logged in successfully!",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                                    intent.putExtra("user_id",result.getId());
                                    startActivity(intent);
                                    finish();
                                }
                        }
                }

        };
    }

    private Callback<User> updateUserCallback() {
        return new Callback<User>() {
            @Override
            public void runResultOnUiThread(User result) {
                if (result != null) {
                    //actualizare ListView
                    for (User user : users) {
                        if (user.getId() == result.getId()) {
                            user.setName(result.getName());
                            user.setUsername(result.getUsername());
                            user.setPassword(result.getPassword());
                            break;
                        }
                    }
                }
            }
        };
    }

    private Callback<Boolean> deleteUserCallback(int position) {
        return new Callback<Boolean>() {
            @Override
            public void runResultOnUiThread(Boolean result) {
                if (result) {
                    users.remove(position);
                }
            }
        };
    }
}