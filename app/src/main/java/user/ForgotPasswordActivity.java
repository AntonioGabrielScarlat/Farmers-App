package user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmersapp.R;
import com.google.android.material.textfield.TextInputEditText;

import async.Callback;
import database.user.User;
import database.user.UserService;


public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText tietUsername;
    private TextInputEditText tietSecurityAnswer;
    private Button btnChangePassword;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initComponents();
        userService=new UserService(getApplicationContext());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.img);
        getSupportActionBar().setTitle("");
    }

    private void initComponents() {
        tietUsername = findViewById(R.id.forgot_password_tiet_username);
        tietSecurityAnswer=findViewById(R.id.forgot_password_tiet_security_answer);
        btnChangePassword=findViewById(R.id.forgot_password_btn_change_password);
        btnChangePassword.setOnClickListener(changePasswordEventListener());
    }

    private View.OnClickListener changePasswordEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    createFromViews();
                    finish();
                }
            }
        };
    }

    private boolean isValid() {
        if (tietUsername.getText() == null || tietUsername.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Invalid username!",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (tietSecurityAnswer.getText() == null || tietSecurityAnswer.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Invalid favorite meal!",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private void createFromViews() {
        String username = tietUsername.getText().toString();
        String securityAnswer=tietSecurityAnswer.getText().toString();
        userService.forgotPassword(username,securityAnswer,forgotPasswordCallbak());
    }

    private Callback<User> forgotPasswordCallbak() {
        return new Callback<User>() {
            @Override
            public void runResultOnUiThread(User result) {
                if (result != null) {
                    Intent intent=new Intent(getApplicationContext(), ChangePasswordActivity.class);
                    intent.putExtra("user_id",result.getId());
                    intent.putExtra("reason","ForgotPassword");
                    startActivity(intent);
                }
            }
        };
    }
}