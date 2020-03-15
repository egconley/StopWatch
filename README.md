# Stop Watch
## Description
An Android app that alerts bus commuters when they are close to their stop.  See [software requirements](./requirements.md).

## Built With
- [Java](https://www.java.com/en/)
- [Android Studio](https://developer.android.com/)
- [Gradle](https://gradle.org/)
- [Google Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/intro)

## Setup Instructions
Android Phone
- Download the StopWatch apk and run it on your device.

Android Emulator

1. Clone the StopWatch repository to your local machine.
2. Open Android Studio.
3. At the welcome screen, select `Import Project`, navigate to the location of the repository, and select Ok.
3. Create a `secrets.xml` in `/StopWatch/app/src/main/res/values/` and add the following resource with your own [google api key](https://developers.google.com/maps/documentation/android-sdk/get-api-key)

    `<string name="google_maps_api_key">xx-your-api-key-here-xx</string>`
4. Make sure that the `compileSdkVersion` in the app build.gradle is 28.
5. Under Settings/Location/Advanced on the emulator, enable `High Accuracy` (if applicable).
6. Click the green play button to build and run StopWatch.

## Screenshots
<img src="/assets/runningApp/HomeSS.png" width="397" height="836">
![](/assets/runningApp/recent-destinations.png)
<img src="/assets/runningApp/search.png" width="397" height="836">
<img src="/assets/runningApp/dialog.png" width="397" height="836">
<img src="/assets/runningApp/geofence.png" width="397" height="836">
<img src="/assets/runningApp/settings.png" width="397" height="836">

## Authors
- [Ellen Conley](https://github.com/egconley)
- [Hai Le](https://github.com/haitle16)
- [Ran Vaknin](https://github.com/RanVaknin)
