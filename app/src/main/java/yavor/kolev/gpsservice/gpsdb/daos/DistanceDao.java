package yavor.kolev.gpsservice.gpsdb.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import yavor.kolev.gpsservice.gpsdb.models.Distance;
import yavor.kolev.gpsservice.util.Constants;

@Dao
public interface DistanceDao {

    @Query("SELECT * FROM " + Constants.TABLE_NAME_DISTANCE)
    Distance getDistance();

    @Insert
    long insertDistance(Distance distance);

    @Update
    void updateDistance(Distance repos);
}