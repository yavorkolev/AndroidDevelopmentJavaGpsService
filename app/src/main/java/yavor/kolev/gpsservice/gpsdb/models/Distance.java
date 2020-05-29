package yavor.kolev.gpsservice.gpsdb.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import yavor.kolev.gpsservice.util.Constants;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = Constants.TABLE_NAME_DISTANCE)
public class Distance implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long distance_id;

    @ColumnInfo(name = "distance_content")

    private String distance;

    private Date date;

    public Distance(String distance) {
        this.distance = distance;
        this.date = new Date(System.currentTimeMillis());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDistance_id() {
        return distance_id;
    }

    public void setDistance_id(long distance_id) {
        this.distance_id = distance_id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Distance)) return false;

        Distance distance = (Distance) o;

        if (distance_id != distance.distance_id) return false;
        return this.distance != null ? this.distance.equals(distance.distance) : distance.distance == null;
    }

    @Override
    public int hashCode() {
        int result = (int) distance_id;
        result = 31 * result + (distance != null ? distance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "distance_id=" + distance_id +
                ", distance='" + distance + '\'' +
                ", date=" + date +
                '}';
    }
}
