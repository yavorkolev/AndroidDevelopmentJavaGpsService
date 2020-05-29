package yavor.kolev.gpsservice.gpsdb.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import yavor.kolev.gpsservice.util.Constants;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = Constants.TABLE_NAME_ALTITUDE_MIN)
public class AltitudeMin implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long altitudeMin_id;

    @ColumnInfo(name = "altitude_min_content")

    private String altitudeMin;

    private Date date;

    public AltitudeMin(String altitudeMin) {
        this.altitudeMin = altitudeMin;
        this.date = new Date(System.currentTimeMillis());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getAltitudeMin_id() {
        return altitudeMin_id;
    }

    public void setAltitudeMin_id(long altitudeMin_id) {
        this.altitudeMin_id = altitudeMin_id;
    }

    public String getAltitudeMin() {
        return altitudeMin;
    }

    public void setAltitudeMin(String altitudeMin) {
        this.altitudeMin = altitudeMin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AltitudeMin)) return false;

        AltitudeMin altitudeMin = (AltitudeMin) o;

        if (altitudeMin_id != altitudeMin.altitudeMin_id) return false;
        return this.altitudeMin != null ? this.altitudeMin.equals(altitudeMin.altitudeMin) : altitudeMin.altitudeMin == null;
    }

    @Override
    public int hashCode() {
        int result = (int) altitudeMin_id;
        result = 31 * result + (altitudeMin != null ? altitudeMin.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AltitudeMin{" +
                "altitudeMin_id=" + altitudeMin_id +
                ", altitudeMin='" + altitudeMin + '\'' +
                ", date=" + date +
                '}';
    }
}