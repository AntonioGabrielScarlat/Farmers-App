package auxiliary.listed;

import java.util.Date;

import database.product.Product;

public class ListedProduct {
    private Product product;
    private Double quantity;

    public ListedProduct(Product product, Double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ListedProduct() {
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
