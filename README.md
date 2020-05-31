# GpsService
Android Development Java Education example GpsService application

Android Studio Java Gps Service offline application Android [4 - 10](Tested on 10)

Features:
    Automatically when you press the button STOP (available only after START):
      Exports to the GPX format in a file the trip or point if you do not move;
        In a proper format for any GPS visualizers, for example https://www.gpsvisualizer.com/;
      Saves the file with the current date and time or any name you want or able to refuse the save;
      Visualize charts with Speed and Altitude for each meter from the current trip;
    Shows for the current trip, for each meter LISTS with: 
     Latitude, Longitude, Speed and Altitud;
    Calculating for the current trip: 
      Distance, Maximum Speed, Altitude Maximum/Minimu;
    Calculating and saves in the DATABASE permanently(from all trips): 
      Distance,Maximum Speed, Total Altitude Minimum/Maximum;

Most interesting technologies are:

    Scope Storage: It's the only way to create a file in Android 10 and above(The old way to keep files is not possible!). I used to create a Gps file with coordinates and save on the device.
    Room databse: It's the only way to keep a database in Android 10 and above.
    Android Architecture Components it's used and is mandatory for Room Database.
    MPAndroidChart: It's used for Speed and Altitude charts.
    Foreground service: It's the only way to keep long-running background service with a frequent updates, like location with Gps
