package database.rating;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import database.user.User;


@Entity(tableName = "rating",foreignKeys = {@ForeignKey(entity = User.class,
parentColumns = "id",
childColumns = "id_user",
onDelete = ForeignKey.CASCADE)})
public class Rating implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "rating_value")
    private Double ratingValue;
    @ColumnInfo(name="id_user")
    private long idUser;

    public Rating(long id, Double ratingValue, long idUser) {
        this.id = id;
        this.ratingValue = ratingValue;
        this.idUser = idUser;
    }

    @Ignore
    public Rating(Double ratingValue, long idUser) {
        this.ratingValue = ratingValue;
        this.idUser = idUser;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rating{");
        sb.append("id=").append(id);
        sb.append(", ratingValue=").append(ratingValue);
        sb.append(", idUser=").append(idUser);
        sb.append('}');
        return sb.toString();
    }
}
