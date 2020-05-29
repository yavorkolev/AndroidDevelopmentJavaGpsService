package yavor.kolev.gpsservice.gpsdb.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import yavor.kolev.gpsservice.util.Constants;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = Constants.TABLE_NAME_ALTITUDE_MAX)
public class AltitudeMax implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long altitudeMax_id;

    @ColumnInfo(name = "altitude_max_content")

    private String altitudeMax;

    private Date date;

    public AltitudeMax(String altitudeMax) {
        this.altitudeMax = altitudeMax;
        this.date = new Date(System.currentTimeMillis());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getAltitudeMax_id() {
        return altitudeMax_id;
    }

    public void setAltitudeMax_id(long altitudeMax_id) {
        this.altitudeMax_id = altitudeMax_id;
    }

    public String getAltitudeMax() {
        return altitudeMax;
    }

    public void setAltitudeMax(String altitudeMax) {
        this.altitudeMax = altitudeMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AltitudeMax)) return false;

        AltitudeMax altitudeMax = (AltitudeMax) o;

        if (altitudeMax_id != altitudeMax.altitudeMax_id) return false;
        return this.altitudeMax != null ? this.altitudeMax.equals(altitudeMax.altitudeMax) : altitudeMax.altitudeMax == null;
    }

    @Override
    public int hashCode() {
        int result = (int) altitudeMax_id;
        result = 31 * result + (altitudeMax != null ? altitudeMax.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AltitudeMax{" +
                "altitudeMax_id=" + altitudeMax_id +
                ", altitudeMax='" + altitudeMax + '\'' +
                ", date=" + date +
                '}';
    }
}
