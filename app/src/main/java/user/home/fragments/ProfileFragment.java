package user.home.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmersapp.R;

import async.Callback;
import database.rating.Rating;
import database.rating.RatingService;
import database.user.User;
import database.user.UserService;
import other.LoginActivity;
import user.ChangePasswordActivity;


public class ProfileFragment extends Fragment {
    public static final String CHANGE_PASSWORD_KEY = "changePasswordKey";
    private Button btnLogout;
    private Button btnChangePassword;
    private Button btnDeleteAccount;
    private RatingBar rbRateApp;
    private Button btnSubmitRating;
    private AlertDialog.Builder builder;

    private Rating currentRating;
    private long userId;
    private User user;

    private UserService userService;
    private RatingService ratingService;

    public ProfileFragment(long userId) {
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_user_profile,container,false);
        initComponents(view);
        return view;
    }
    private void initComponents(View view) {
        btnLogout=view.findViewById(R.id.fragment_user_profile_btn_logout);
        btnChangePassword=view.findViewById(R.id.fragment_user_profile_btn_change_password);
        btnDeleteAccount=view.findViewById(R.id.fragment_user_profile_btn_delete_account);
        rbRateApp=view.findViewById(R.id.fragment_user_profile_rb_rate_app);
        btnSubmitRating=view.findViewById(R.id.fragment_user_profile_btn_submit_rating);
        btnLogout.setOnClickListener(logoutEventListener());
        btnChangePassword.setOnClickListener(changePasswordEventListener());
        btnDeleteAccount.setOnClickListener(deleteAccountEventListener());
        btnSubmitRating.setOnClickListener(submitRatingEventListener());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService=new UserService(getContext().getApplicationContext());
        ratingService=new RatingService(getContext().getApplicationContext());
        builder = new AlertDialog.Builder(this.getContext());
        userService.getUserById(userId,getUserByIdCallback());
        ratingService.getRatingByUserId(userId,getRatingByUserIdCallback());
    }

    private Callback<User> getUserByIdCallback() {
        return new Callback<User>() {
            @Override
            public void runResultOnUiThread(User result) {
                if (result != null) {
                    user=new User(result.getId(),result.getName(),result.getUsername(),result.getPassword(),result.getSecurityAnswer());
                }
            }
        };
    }

    private Callback<Rating> getRatingByUserIdCallback() {
        return new Callback<Rating>() {
            @Override
            public void runResultOnUiThread(Rating result) {
                if (result != null) {
                    currentRating=new Rating(result.getId(),result.getRatingValue(),result.getIdUser());
                    rbRateApp.setRating(currentRating.getRatingValue().floatValue());
                }
            }
        };
    }

    private View.OnClickListener submitRatingEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentRating!=null)
                {
                    currentRating.setRatingValue(Double.parseDouble(String.valueOf(rbRateApp.getRating())));
                    ratingService.update(currentRating,updateRatingCallback());
                }
                else
                {
                    currentRating=new Rating(Double.parseDouble(String.valueOf(rbRateApp.getRating())),userId);
                    ratingService.insert(currentRating,insertRatingCallback());
                }

            }
        };
    }

    private Callback<Rating> insertRatingCallback() {
        return new Callback<Rating>() {
            @Override
            public void runResultOnUiThread(Rating result) {
                if (result != null) {
                    Toast.makeText(getContext().getApplicationContext(),"Rating successfully submitted!",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private Callback<Rating> updateRatingCallback() {
        return new Callback<Rating>() {
            @Override
            public void runResultOnUiThread(Rating result) {
                if (result != null) {
                    Toast.makeText(getContext().getApplicationContext(),"Rating successfully submitted!",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private View.OnClickListener changePasswordEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), ChangePasswordActivity.class);
                intent.putExtra("user_id",userId);
                intent.putExtra("reason","ChangePassword");
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener deleteAccountEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to delete this account?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                userService.delete(user,deleteUserCallback());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Delete Account");
                alert.show();
            }
        };
    }

    private Callback<Boolean> deleteUserCallback() {
        return new Callback<Boolean>() {
            @Override
            public void runResultOnUiThread(Boolean result) {
                if (result) {
                    Toast.makeText(getContext().getApplicationContext(),"Account successfully deleted!",
                            Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getContext().getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    private View.OnClickListener logoutEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent intent=new Intent(getContext().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        };
    }
}
