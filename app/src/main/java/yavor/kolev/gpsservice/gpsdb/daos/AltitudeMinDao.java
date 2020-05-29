package yavor.kolev.gpsservice.gpsdb.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import yavor.kolev.gpsservice.gpsdb.models.AltitudeMin;
import yavor.kolev.gpsservice.util.Constants;

@Dao
public interface AltitudeMinDao {

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ALTITUDE_MIN)
    AltitudeMin getAltitudeMin();

    @Insert
    long insertAltitudeMin(AltitudeMin altitudeMin);

    @Update
    void updateAltitudeMin(AltitudeMin repos);
}
