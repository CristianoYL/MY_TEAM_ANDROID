# Introduction
This is an Android App for football(you may call it soccer) team management. It allows user(football players/coaches) to manage clubs on their Android devices through the internet. 
* Users may create his own profile, join/create a club, join tournaments and record game results and stats.
* The stats will be sumarized and visualized in various charts on player's/club's profile pages. 
* Also, the app provides chatting channels for club members to share and communicate
* The app provides location services and maps to help team leaders publish game events, locations and even navigation.
* The app allows push notification thus you won't miss any important team announcement.

This mobile app communicate with a set of REST APIs in a Flask HTTP server hosted on AWS and a PostgreSQL database on RDS as well as a Amazon S3 cloud storage instance. A sample back-end providing this service can be found at [my other GitHub repo here.](https://github.com/CristianoYL/MY_TEAM_API)
# Configurations
## IDE
The project is developed using **Android Studio 3.0**. It is recommended that you use **Android Studio** instead of other IDEs for simpler project import. [You may download Android Studio from the official website here](https://developer.android.com/studio/index.html).

## Dependencies
This service relies on serveral other services as well. For example:
* [Google Maps API](https://developers.google.com/maps/documentation/android-api/) for location services and maps. 
* [Gson](https://sites.google.com/site/gson/gson-user-guide) for data serialization, more specificly, for jsonifying data.
* [FireBase Cloud Messaging (FCM)](https://firebase.google.com/docs/cloud-messaging/) for push notifications.
* [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) for data visualization.
* [AWS Android SDK](https://docs.aws.amazon.com/mobile/sdkforandroid/developerguide/) for communicating with Amazon S3 services.

Since **Android Studio** uses **gradle build**, you don't need to worry about dependencies when importing the code.
# SDK
* The minimum SDK is 15
* The target and compile SDK is 25
* All details can be found in the module's ```build.gradle``` file

# User Guide
Coming soon...
