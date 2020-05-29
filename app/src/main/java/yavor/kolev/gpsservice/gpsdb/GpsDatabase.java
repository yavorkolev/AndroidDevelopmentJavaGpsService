package yavor.kolev.gpsservice.gpsdb;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import yavor.kolev.gpsservice.gpsdb.daos.DistanceDao;
import yavor.kolev.gpsservice.gpsdb.daos.SpeedDao;
import yavor.kolev.gpsservice.gpsdb.daos.AltitudeMinDao;
import yavor.kolev.gpsservice.gpsdb.daos.AltitudeMaxDao;

import yavor.kolev.gpsservice.gpsdb.models.Distance;
import yavor.kolev.gpsservice.gpsdb.models.Speed;
import yavor.kolev.gpsservice.gpsdb.models.AltitudeMax;
import yavor.kolev.gpsservice.gpsdb.models.AltitudeMin;

import yavor.kolev.gpsservice.util.Constants;
import yavor.kolev.gpsservice.util.DateRoomConverter;

@Database(entities = { Distance.class, Speed.class, AltitudeMin.class, AltitudeMax.class}, version = 1)
@TypeConverters({DateRoomConverter.class})
public abstract class GpsDatabase extends RoomDatabase {

    public abstract DistanceDao getDistanceDao();
    public abstract SpeedDao getSpeedDao();
    public abstract AltitudeMinDao getAltitudeMinDao();
    public abstract AltitudeMaxDao getAltitudeMaxDao();

    private static GpsDatabase gpsDB;

    public static GpsDatabase getInstance(Context context) {
        if (null == gpsDB) {
            gpsDB = buildDatabaseInstance(context);
        }
        return gpsDB;
    }

    private static GpsDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                GpsDatabase.class,
                Constants.DB_NAME).allowMainThreadQueries().build();
    }

    public  void cleanUp(){
        gpsDB = null;
    }
}
