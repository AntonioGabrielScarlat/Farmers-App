package other;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmersapp.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import async.Callback;
import database.user.User;
import database.user.UserService;


public class RegisterActivity extends AppCompatActivity {
    public static final String USER_KEY = "userKey";
    private TextInputEditText tietName;
    private TextInputEditText tietUsername;
    private TextInputEditText tietPassword;
    private TextInputEditText tietSecurityAnswer;
    private Button btnSubmit;

    private User user;
    private Intent intent;

    private UserService userService;
    private List<User> users=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
        userService=new UserService(getApplicationContext());
        intent = getIntent();
        if (intent.hasExtra(USER_KEY)) {
            user = (User) intent.getSerializableExtra(USER_KEY);
            createViewsFromUser();
        }
        userService.getAll(getAllUsersCallback());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.img);
        getSupportActionBar().setTitle("");
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

    private void createViewsFromUser() {
        if (user == null) {
            return;
        }

        if (user.getName() != null) {
            tietName.setText(user.getName());
        }

        if (user.getUsername() != null) {
            tietUsername.setText(user.getUsername());
        }

        if (user.getPassword() != null) {
            tietPassword.setText(user.getPassword());
        }

        if (user.getSecurityAnswer() != null) {
            tietSecurityAnswer.setText(user.getSecurityAnswer());
        }

    }

    private void initComponents() {
        tietName = findViewById(R.id.add_user_tiet_name);
        tietUsername = findViewById(R.id.add_user_tiet_username);
        tietPassword = findViewById(R.id.add_user_tiet_password);
        tietSecurityAnswer=findViewById(R.id.add_user_tiet_security_answer);
        btnSubmit = findViewById(R.id.add_user_btn_submit);
        btnSubmit.setOnClickListener(saveUserEventListener());
    }

    private View.OnClickListener saveUserEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    createFromViews();
                    intent.putExtra(USER_KEY, user);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        };
    }

    private boolean isValid() {
        if (tietName.getText() == null || tietName.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Invalid name!",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (tietUsername.getText() == null || tietUsername.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Invalid username!",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        for(User tempUser:users){
            if(tietUsername.getText().toString().equals(tempUser.getUsername()) && user==null){
                Toast.makeText(getApplicationContext(),
                        "Username already exists!",
                        Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        }
        if (tietPassword.getText() == null || tietPassword.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Invalid password!",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (tietSecurityAnswer.getText() == null || tietSecurityAnswer.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Invalid answer!",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }


        return true;
    }

    private void createFromViews() {
        String name = tietName.getText().toString();
        String username = tietUsername.getText().toString();
        String password = tietPassword.getText().toString();
        String securityAnswer=tietSecurityAnswer.getText().toString();
        if (user == null) {
            user = new User(name,username,password,securityAnswer);
        } else {
            user.setName(name);
            user.setUsername(username);
            user.setPassword(password);
            user.setSecurityAnswer(securityAnswer);
        }
    }

}