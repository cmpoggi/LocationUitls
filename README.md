# LocationUitls

Java class for android to get GPS location (latitude and longitude) which I wrote for my own projects which I decided to release as open source.

## How to use
Add the file in you android project, the following permissions must be added in the AndroidManifest.xml file in order to get permission to use the Location Manager.


AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

Paste this code to your project:
```java
LocationUtils locationUtils = new LocationUtils(context, actAddLocation.this,
                getString("OK text for dialog"),
                getString("Cancel text for dialog"),
                getString("Location disabled text for dialog"),
                this::listenerMethod); //listener method will pass the two coordinates as double (latitude, longitude)
locationUtils.getCoordinates();
```
If the location setting is disabled, it will display a dialog to enable it
