# ROLOI

Android Studio application that's intended to schedule meetings through the google calendar app to check whether a conference room is booked or available. The app was developed to allow the user to create, read, update and delete events directly from the app and also for internal use by Esper. 

### App Features and Functionality

By using the CalendarContract.Events feature in Android Studio, the app can successfully insert events into google calendar and read the events as well. The app supports scheduling through google calendar, meaning that if the device has proper internet connection, a meeting can be scheduled via google calendar and be seen here on the app. This will sometimes take a few minutes, but the app does support this scheduling. However, the user must add the account to the device.

On the left side of the device, the navigation menu can be accessed. The menu offers the user the option to toggle between the week view, day view, and general overview of events. All of these features are similar to what you would see with the google calendar app with the exception of the month view, which we removed for simplicity purposes. The app is designed to schedule meetings directly outside of the conference room, and we believe that adding this feature wouldn't be the most practical solution. 

On the top right corner, there is the create events button which prompts the create events dialog, and there is another button which gives the user the option of either going back to the current day or restarting the view. 

Additionally, the app has been integrated with the Esper SDK. The app is compatible with provisioned devices. The device uses an ambient-light sensor to adjust the brightness accordingly using the SDK. 

Finally, every 30 seconds of inactivity, the app defaults back to the day view. We have accomplished this by running a Handler in the background that goes back to the current day once the screen has not been touched for 30 seconds. 

### Current Issues

The three issues that have yet to have been resolved are: the delete events button, displaying recurring events, and the timezone. 

For the delete events feature, the events are being deleted from the google calendar app, but the scheduling app's user interface does not display any of the changes. Regarding the recurring events, the first instance of a recurring event is showing up in the app, but the rest of the instances are not. We suspect that this is because of the matching ID's. 

Lastly, the CalendarContracts.Events.EVENT_TIMEZONE feature needs to be hardcoded to the timezone the device is located in when integrated with the Esper SDK (in our case, PST).
Without the SDK, the feature seemed to be working, but when the SDK code was added, we kept receiving a NullPointerException. It can be solved by setting the timezone to "America/Los_Angeles", but if this app is going to be used elsewhere, this is only a temporary fix.

### Thank you to these developers: 
      - https://github.com/jignesh13/googlecalendar
      - https://github.com/codexpedia/android_google_calendar_crud
      
### Developers
      - Nidhi Krishna Kumar
      - Rishi Aniga
