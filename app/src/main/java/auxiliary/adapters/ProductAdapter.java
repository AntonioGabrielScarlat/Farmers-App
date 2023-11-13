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

import database.product.Product;


public class ProductAdapter extends ArrayAdapter<Product> {

    private Context context;
    private int resource;
    private List<Product> products;
    private LayoutInflater inflater;

    public ProductAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.products = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Product Product = products.get(position);
        if (Product != null) {
            addName(view, Product.getName());
            addPrice(view,Product.getPrice());

        }
        return view;
    }

    private void addName(View view, String name) {
        TextView textView = view.findViewById(R.id.lv_product_row_tv_name);
        addTextViewContent(textView,name);
    }

    private void addPrice(View view, Double price) {
        TextView textView = view.findViewById(R.id.lv_product_row_tv_price);
        addTextViewContent(textView,Double.toString(price)+" RON / unit");
    }

    private void addTextViewContent(TextView textView, String value) {
        if (value != null && !value.isEmpty()) {
            textView.setText(value);
        } else {
            textView.setText(R.string.lv_row_default_value);
        }
    }
}
