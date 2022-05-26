# LocationUitls

Java class for android to get GPS location (latitude and longitude) which I wrote for my own projects which I decided to release as open source.

## How to use
Add the file in you android project, the following permissions must be added in the manifest.xml file in order to get permission to use the Location Manager.

If the location setting is disabled, it will display a dialog to enable it

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

```java

LocationUtils locationUtils = new LocationUtils(context, actAddLocation.this,
                getString("OK text for dialog"),
                getString("Cancel text for dialog"),
                getString("Location disabled text for dialog"),
                this::getWeatherData); //listener method will pass the two coordinates as double (latitude, longitude)
locationUtils.getCoordinates();
```
