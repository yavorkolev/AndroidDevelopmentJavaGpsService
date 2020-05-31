# GpsService
Android Development Java Education example GpsService application

Android Studio Java Gps Service offline application Android [4 - 10](Tested on 10)

    Exports the trip(or point if you no moove) to the Gpx, file with Latitude and Longitude(automatically on stop). In a proper format for GPS Visualizers, for example https://www.gpsvisualizer.com/
    Displays for the current trip: Lists Latitude and Longitude, Speed and Altitude
    Displays for the current trip: Distance, Maximum Speed, Altitude Maximum/Minimum
    Keeps in database Total Distance, Total Maximum Speed, Total Altitude Minimum and Maximum, when you finish.
    Visualize charts with Speed and Altitude for each meter from the current trip, when you finish

Most interesting technologies are:

    Scope Storage: It's the only way to create a file in Android 10 and above(The old way to keep files is not possible!). I used to create a Gps file with coordinates and save on the device.
    Room databse: It's the only way to keep a database in Android 10 and above.
    Android Architecture Components it's used and is mandatory for Room Database.
    MPAndroidChart: It's used for Speed and Altitude charts.
    Foreground service: It's the only way to keep long-running background service with a frequent updates, like location with Gps
