package admin.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmersapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import async.Callback;
import auxiliary.adapters.UserAdapter;
import database.rating.Rating;
import database.rating.RatingService;
import database.user.User;
import database.user.UserService;
import other.LoginActivity;
import other.RegisterActivity;

public class HomeFragment extends Fragment {
    private TextView tvRatingsAverage;
    private ListView lvUsers;
    private Button btnLogout;

    private List<User> users=new ArrayList<>();
    private Double ratingsSum;

    private ActivityResultLauncher<Intent> updateUserLauncher;
    private UserService userService;
    private RatingService ratingService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_admin_home,container,false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        tvRatingsAverage =view.findViewById(R.id.fragment_admin_home_tv_ratings_average);
        lvUsers=view.findViewById(R.id.fragment_admin_home_lv_users);
        btnLogout=view.findViewById(R.id.fragment_admin_home_btn_logout);
        addAdapter();
        lvUsers.setOnItemClickListener(getItemClickEvent());
        btnLogout.setOnClickListener(logoutEventListener());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUserLauncher= getUpdateUserLauncher();
        userService=new UserService(getContext().getApplicationContext());
        ratingService=new RatingService(getContext().getApplicationContext());
        ratingService.getAll(getAllRatingsCallback());
        userService.getAll(getAllUsersCallback());
    }

    private Callback<List<User>> getAllUsersCallback() {
        return new Callback<List<User>>() {
            @Override
            public void runResultOnUiThread(List<User> results) {
                if (results != null) {
                    users.clear();
                    users.addAll(results);
                    notifyAdapter();
                }
            }
        };
    }

    private Callback<List<Rating>> getAllRatingsCallback() {
        return new Callback<List<Rating>>() {
            @Override
            public void runResultOnUiThread(List<Rating> results) {
                DecimalFormat decimalFormat=new DecimalFormat("#.##");
                if (results != null && results.size()>0) {
                    ratingsSum=0.0;
                    for(Rating rating:results){
                        ratingsSum=ratingsSum+rating.getRatingValue();
                    }
                    tvRatingsAverage.setText("The average rating given by the users is: "+decimalFormat.format(ratingsSum/results.size()));
                }
                else {
                    tvRatingsAverage.setText("There are no ratings from the users!");
                }
            }
        };
    }

    private AdapterView.OnItemClickListener getItemClickEvent() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext().getApplicationContext(), RegisterActivity.class);
                intent.putExtra(RegisterActivity.USER_KEY, users.get(position));
                updateUserLauncher.launch(intent);
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
                if (result != null && result.getResultCode() == -1 && result.getData() != null) {
                    User user = (User) result.getData().getSerializableExtra(RegisterActivity.USER_KEY);
                    userService.update(user, updateUserCallback());
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
                    notifyAdapter();
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


    private void addAdapter() {
        UserAdapter adapter = new UserAdapter(getContext().getApplicationContext(), R.layout.lv_user_row,
                users, getLayoutInflater());
        lvUsers.setAdapter(adapter);
    }

    public void notifyAdapter() {
        ArrayAdapter<User> adapter = (ArrayAdapter<User>) lvUsers.getAdapter();
        adapter.notifyDataSetChanged();
    }


}
