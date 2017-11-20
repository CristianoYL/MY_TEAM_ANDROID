# Introduction
This is an Android App for football(you may call it soccer) team management. It allows user(football players/coaches) to manage clubs on their Android devices through the internet.
* Users may create his own profile, join/create a club, join tournaments and record game results and stats.
* The stats will be summarized and visualized in various charts on player's/club's profile pages.
* Also, the app provides chatting channels for club members to share and communicate
* The app provides location services and maps to help team leaders publish game events, locations and even navigation.
* The app allows push notification thus you won't miss any important team announcement.

This mobile app communicate with a set of REST APIs in a Flask HTTP server hosted on AWS and a PostgreSQL database on RDS as well as a Amazon S3 cloud storage instance. A sample back-end providing this service can be found at [my other GitHub repo here.](https://github.com/CristianoYL/MY_TEAM_API)
# Configurations
## IDE
The project is developed using **Android Studio 3.0**. It is recommended that you use **Android Studio** instead of other IDEs for simpler project import. [You may download Android Studio from the official website here](https://developer.android.com/studio/index.html).

## Dependencies
This service relies on several other services as well. For example:
* [Google Maps API](https://developers.google.com/maps/documentation/android-api/) for location services and maps.
* [Gson](https://sites.google.com/site/gson/gson-user-guide) for data serialization, more specifically, for jsonifying data.
* [FireBase Cloud Messaging (FCM)](https://firebase.google.com/docs/cloud-messaging/) for push notifications.
* [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) for data visualization.
* [AWS Android SDK](https://docs.aws.amazon.com/mobile/sdkforandroid/developerguide/) for communicating with Amazon S3 services.

Since **Android Studio** uses **gradle build**, you don't need to worry about dependencies when importing the code.
# SDK
* The minimum SDK is 15
* The target and compile SDK is 26
* All details can be found in the module's ```build.gradle``` file

# User Guide

## Login/Register
<img src="https://github.com/CristianoYL/MY_TEAM_ANDROID/blob/master/sample/Screenshot_20171116-233611.png" width="200">

User will first be prompt with a login/register page, just like any other app will do. After one has logged in, a profile page will appear and ask for your basic info which will be presented as your profile.

## Menu

<img src="https://github.com/CristianoYL/MY_TEAM_ANDROID/blob/master/sample/Screenshot_20171116-234430.png" width="200">

From the side menu, you can access different pages.


## Profile Page

<img src="https://github.com/CristianoYL/MY_TEAM_ANDROID/blob/master/sample/Screenshot_20171116-233952.png" width="200"> <img src="https://github.com/CristianoYL/MY_TEAM_ANDROID/blob/master/sample/Screenshot_20171116-234028.png" width="200">

A player's profile page will show his basic info stats. The stats are visualized using [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) and embedded in a ViewPager, where you can swipe back and forth to view different stats in different tabs. And this is the default page after a user logged in, unless specified otherwise.

## Club Page

The Club Page contains all the functionalities related to player's clubs, which includes chat, roster, map and event pages. The sub-pages are also embedded in a Tabbed ViewPager which allows the user navigating through them easily using swiping as well as tapping the tabs.

### Chat
The Chat Page allow players within the same club talking and sharing images.

<img src="https://github.com/CristianoYL/MY_TEAM_ANDROID/blob/master/sample/Screenshot_20171116-234116.png" width="200"> 

### Member
The Member Page shows the roster information. And if a user has admin privilege, such as Captains and Co-caps, he can manage the clubs through this page, including promoting members as admins, kicking member out and accepting new applicants to club.

### Map
The map page allows the user to see his current location as well as future game locations, and provide navigation through Google Maps.

The user may search for an address in the search bar as well. The admins can use the searched address as a new event location and post it to the club. This can be helpful in scenarios like this: When the captain is informed of a new upcoming game of their Sunday league, and it's an away game and a new opponent. He can search for the address, select it and use it to create a new event. The event will then be posted onto the Event Page where other teammates will see. They can then select the location on map and use navigation when they need to.

Another important feature of the Map Page is online check-in. If you've played amateur football, it is quite often the case that people won't show up on time as they said to. The online check-in feature allows the captains to have some prevision how his squad would turn up on a chilling Sunday game, and whether John Doe is indeed "5 minutes away" or just still in bed.

<img src="https://github.com/CristianoYL/MY_TEAM_ANDROID/blob/master/sample/Screenshot_20171116-234138.png" width="200"> 

### Event
The Event Page will present all the events of the clubs. The latest posting from the captains will show up here. Captains can post events from here too.

<img src="https://github.com/CristianoYL/MY_TEAM_ANDROID/blob/master/sample/Screenshot_20171116-234417.png" width="200">

## Tournament
For the Club Page, you can also view all the tournaments your club is participating. Once you select a particular tournament, you'll navigate to the Tournament Page which is very similar to the Club Page, but only reflects the info in this particular tournament. The Tournament Page also contains several sub-pages in its Tabbed ViewPager, which includes Result, Squad, Stats and Chat pages.

### Result
The Result page shows the club's game results in this tournament, which include the score, game events and players (goal and goal scorer for example).

<img src="https://github.com/CristianoYL/MY_TEAM_ANDROID/blob/master/sample/Screenshot_20171116-234442.png" width="200">

### Squad
The Squad Page is similar as the club's Member Page, however, not all club member may be playing this particular Tournament. Thus you may view the Squad as a sub-set of the Member of your club. The Captains also have admin privilege in the Squad Page.

### Stats
The Stats page shows the club's overall performance in this tournament.

<img src="https://github.com/CristianoYL/MY_TEAM_ANDROID/blob/master/sample/Screenshot_20171116-234452.png" width="200">
