package database.cart;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

import database.user.User;


@Entity(tableName = "cart",foreignKeys = {@ForeignKey(entity = User.class,
parentColumns = "id",
childColumns = "id_user",
onDelete = ForeignKey.CASCADE)})
public class Cart implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name="total")
    private Double total;
    @ColumnInfo(name="status")
    private String status;
    @ColumnInfo(name="id_user")
    private long idUser;

    public Cart(long id, Double total, String status, long idUser) {
        this.id = id;
        this.total = total;
        this.status=status;
        this.idUser = idUser;
    }

    @Ignore
    public Cart(Double total,String status, long idUser) {
        this.total = total;
        this.status=status;
        this.idUser = idUser;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MealPlan{");
        sb.append("id=").append(id);
        sb.append(", total=").append(total);
        sb.append(", status=").append(status);
        sb.append(", idUser=").append(idUser);
        sb.append('}');
        return sb.toString();
    }
}
