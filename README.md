
# What is this ?
This is the source code for the Android App, [App Usage Monitor](https://play.google.com/store/apps/details?id=com.dhgg.appusagemonitor). 


# How can I run this ?
Import the project into Android Studio. 


# How can I build this ?
./gradlew assembleDebug
or 
./gradlew assembleRelease


# Dependencies ?
If you want to store historical data to a cloud back-end, this depends on a another [cloud app] (https://github.com/dhan12/appusagemonitor_web_backend).
To communicate with this cloud app, you'd need the web client id. 
This is stored on the local machine of the original author. 
So you shouldn't be able to communicate with the real cloud application. 


# How to Publish a signed apk to the app store ? 
The keystore, keystore password and password are all stored on the original author's local machine. 
Therefore, only the original author can do this. 


# File information ?
- app/main/java/com/appspot.appusagemonitor.appusagemonitor/*
  This code is auto-generated from a app-engine cloud endpoint service. 
- app/main/java/com/dhgg/appusagemonitor/*
  Main activity and other android code is here. 
- app/main/java/com/dhgg/appusagemonitor_tests/*
  Testing code for the android code is here.
- app/main/java/com/dhgg/cloudbackend/*
  Code to interact with the cloud backend is here.
  

# Other background information ?
I started writing this app as a way for me to learn about programming an Android App. 


