package auxiliary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.farmersapp.R;

import java.util.List;

import database.user.User;


public class UserAdapter extends ArrayAdapter<User> {

    private Context context;
    private int resource;
    private List<User> users;
    private LayoutInflater inflater;

    public UserAdapter(@NonNull Context context, int resource, @NonNull List<User> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.users = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        User user = users.get(position);
        if (user != null) {
            addName(view, user.getName());
            addUsername(view, user.getUsername());
            addPassword(view, user.getPassword());
        }
        return view;
    }

    private void addName(View view, String name) {
        TextView textView = view.findViewById(R.id.tv_lv_user_row_name);
        addTextViewContent(textView,name);
    }

    private void addUsername(View view, String username) {
        TextView textView = view.findViewById(R.id.tv_lv_user_row_username);
        addTextViewContent(textView, username);
    }

    private void addPassword(View view, String password) {
        TextView textView = view.findViewById(R.id.tv_lv_user_row_password);
        addTextViewContent(textView, password);
    }

    private void addTextViewContent(TextView textView, String value) {
        if (value != null && !value.isEmpty()) {
            textView.setText(value);
        } else {
            textView.setText(R.string.lv_row_default_value);
        }
    }
}
