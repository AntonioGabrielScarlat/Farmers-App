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

import auxiliary.listed.ListedProduct;


public class AddedProductAdapter extends ArrayAdapter<ListedProduct> {

    private Context context;
    private int resource;
    private List<ListedProduct> listedProducts;
    private LayoutInflater inflater;

    public AddedProductAdapter(@NonNull Context context, int resource, @NonNull List<ListedProduct> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.listedProducts = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        ListedProduct listedProduct = listedProducts.get(position);
        if (listedProduct != null) {
            addName(view, listedProduct.getProduct().getName());
            addPrice(view,listedProduct.getProduct().getPrice());
            addQuantity(view,listedProduct.getQuantity());
            addTotal(view, listedProduct.getProduct().getPrice(), listedProduct.getQuantity());

        }
        return view;
    }

    private void addName(View view, String name) {
        TextView textView = view.findViewById(R.id.lv_added_product_row_tv_name);
        addTextViewContent(textView,name);
    }

    private void addPrice(View view, Double price) {
        TextView textView = view.findViewById(R.id.lv_added_product_row_tv_price);
        addTextViewContent(textView,Double.toString(price)+" RON / unit");
    }

    private void addQuantity(View view, Double quantity) {
        TextView textView = view.findViewById(R.id.lv_added_product_row_tv_quantity);
        addTextViewContent(textView,Double.toString(quantity)+" units");
    }

    private void addTotal(View view, Double price, Double quantity) {
        TextView textView = view.findViewById(R.id.lv_added_product_row_tv_total_per_product);
        addTextViewContent(textView,Double.toString(quantity*price)+" RON");
    }

    private void addTextViewContent(TextView textView, String value) {
        if (value != null && !value.isEmpty()) {
            textView.setText(value);
        } else {
            textView.setText(R.string.lv_row_default_value);
        }
    }
}
