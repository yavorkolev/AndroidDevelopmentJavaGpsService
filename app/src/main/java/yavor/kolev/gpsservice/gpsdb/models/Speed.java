package yavor.kolev.gpsservice.gpsdb.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import yavor.kolev.gpsservice.util.Constants;

import java.io.Serializable;
import java.util.Date;;

@Entity(tableName = Constants.TABLE_NAME_SPEED)
public class Speed implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long speed_id;

    @ColumnInfo(name = "speed_content")

    private String speed;

    private Date date;

    public Speed(String speed) {
        this.speed = speed;
        this.date = new Date(System.currentTimeMillis());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getSpeed_id() {
        return speed_id;
    }

    public void setSpeed_id(long speed_id) {
        this.speed_id = speed_id;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Speed)) return false;

        Speed speed = (Speed) o;

        if (speed_id != speed.speed_id) return false;
        return this.speed != null ? this.speed.equals(speed.speed) : speed.speed == null;
    }

    @Override
    public int hashCode() {
        int result = (int) speed_id;
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Speed{" +
                "speed_id=" + speed_id +
                ", speed='" + speed + '\'' +
                ", date=" + date +
                '}';
    }
}