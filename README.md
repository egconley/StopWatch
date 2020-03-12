# Stop Watch
## Description
An Android app that alerts bus commuters when they are close to their stop.  See [software requirements](./requirements.md).

## User Stories

## Running Instructions:
- If you have an android phone, simply download our apk file and run it on your device.
- 1. Clone the StopWatch repository into your local machine.
- 2. Use Android Studio to import in your cloned repository, and open the app.
- 3. Under `/StopWatch/app/src/main/res/values/` directory create a `secrets.xml` file
- - Within the secrets.xml file, create a string and input your own [google api key](https://developers.google.com/maps/documentation/android-sdk/get-api-key).
- 4. Start your application with a emulator that has api level of 28 or below.
- 5. When your emulator started, go into location setting to enable `High Accuracy Setting`
- 6. Lastly, run your StopWatch in your emulator


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
