package yavor.kolev.gpsservice.gpsdb.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import yavor.kolev.gpsservice.gpsdb.models.AltitudeMax;
import yavor.kolev.gpsservice.util.Constants;

@Dao
public interface AltitudeMaxDao {

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ALTITUDE_MAX)
    AltitudeMax getAltitudeMax();

    @Insert
    long insertAltitudeMax(AltitudeMax altitudeMax);

    @Update
    void updateAltitudeMax(AltitudeMax repos);
}