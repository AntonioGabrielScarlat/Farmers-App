package database.addedProduct;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import database.cart.Cart;
import database.product.Product;


@Entity(tableName = "added_product",foreignKeys = {
        @ForeignKey(entity = Product.class, parentColumns = "id", childColumns = "id_product", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Cart.class,parentColumns = "id",childColumns ="id_cart",onDelete = ForeignKey.CASCADE)})
public class AddedProduct implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "quantity")
    private Double quantity;
    @ColumnInfo(name="id_product")
    private long idProduct;
    @ColumnInfo(name="id_cart")
    private long idCart;

    public AddedProduct(long id, Double quantity,long idProduct, long idCart) {
        this.id = id;
        this.quantity = quantity;
        this.idProduct = idProduct;
        this.idCart = idCart;
    }

    @Ignore
    public AddedProduct(Double quantity,long idProduct, long idCart) {
        this.quantity = quantity;
        this.idProduct = idProduct;
        this.idCart = idCart;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(long idProduct) {
        this.idProduct = idProduct;
    }

    public long getIdCart() {
        return idCart;
    }

    public void setIdCart(long idCart) {
        this.idCart = idCart;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AddedProduct{");
        sb.append("id=").append(id);
        sb.append(", quantity=").append(quantity);
        sb.append(", idProduct=").append(idProduct);
        sb.append(", idCart=").append(idCart);
        sb.append('}');
        return sb.toString();
    }
}
