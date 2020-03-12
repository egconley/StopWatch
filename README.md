# Stop Watch
## Description
An Android app that alerts bus commuters when they are close to their stop.  See [software requirements](./requirements.md).

## User Stories
- As a user, I want to be able to select a location on a map and set it as my destination endpoint so that I can be notified when I am close to my destination.
- As a user, I want to be notified when it is almost time to get off the bus so that I can focus my attention on my book/music/game/conversation.
- As a user, I want to be able to save destinations that I use frequently so that I can access them without setting a new pin on the map.
- As a user, I want to be able to see my location on the map so that I can track my progress toward reaching my destination.
- As a user, I want to be able to see the notification radius around my destination on the map.

## Running Instructions:
- If you have an android phone, simply download our apk file and run it on your device.
- 1. Clone the StopWatch repository into your local machine.
- 2. Use Android Studio to import in your cloned repository, and open the app.
- 3. Under `/StopWatch/app/src/main/res/values/` directory create a `secrets.xml` file
- - Within the secrets.xml file, create a string and input your own [google api key](https://developers.google.com/maps/documentation/android-sdk/get-api-key).
- 4. Start your application with a emulator that has api level of 28 or below.
- 5. When your emulator started, go into location setting to enable `High Accuracy Setting`
- 6. Lastly, run your StopWatch in your emulator

## Application in running
![](/assets/runningApp/HomeSS.png){:height="50%" width="50%"}
![](/assets/runningApp/recent-tab.png)
![](/assets/runningApp/search.png)
![](/assets/runningApp/dialog.png)
![](/assets/runningApp/geofence.png)
![](/assets/runningApp/setting.png)



## Built With:
- [Java](https://www.java.com/en/)
- [Gradle](https://gradle.org/)
- [AWS Amplify](https://aws.amazon.com/amplify/)
- [Google Maps Embedded API](https://developers.google.com/maps/documentation/embed/start)
- [OneBusAway RESTful API](http://developer.onebusaway.org/modules/onebusaway-application-modules/1.1.14/api/where/index.html)

## Authors
- Ellen Conley
- Hai Le
- Ran Vaknin

// to test app generate google maps api key and notification key?
