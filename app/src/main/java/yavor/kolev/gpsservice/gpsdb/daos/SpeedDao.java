package yavor.kolev.gpsservice.gpsdb.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import yavor.kolev.gpsservice.gpsdb.models.Speed;
import yavor.kolev.gpsservice.util.Constants;

@Dao
public interface SpeedDao {

    @Query("SELECT * FROM " + Constants.TABLE_NAME_SPEED)
    Speed getSpeed();

    @Insert
    long insertSpeed(Speed speed);

    @Update
    void updateSpeed(Speed repos);
}
