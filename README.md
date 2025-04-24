# APK Cloud Deployment 
# Prequisites
* Have Android Studio Installed
    * Android Studio can be installed for free on the Android Studio website: https://developer.android.com/studio
* Set up a Firebase account. 
    * Go to the Firebase console https://console.firebase.google.com/u/0/
    * Sign in with a Google account.
    * Click the “Create Project” option and enter a name for the project.
    * Accept the terms and conditions prompt and press continue.
    * Turn on Google Analytics (Optional).
    * Select Create Project
* Ensure that git is installed on your computer.
    * a. Installation instructions can be found on the git website: https://git-scm.com/downloads
* Ensure that NPM Version 18.0.0 or higher is installed on your computer.
    * a. Installation instructions can be found on the NPM website: https://docs.npmjs.com/downloading-and-installing-node-js-and-npm

# Generating an APK
* Open the project using Android Studio.
* Select “Build”, then “Generate Signed App Bundle / APK…”
* Select “APK” and click “Next”.
* Create a new KeyStore or choose an existing an if available, then click “Next”.
* Select “Release” for the build variant and then select “Create”.
* The APK should output to the project directory under app/release.

# Deploy the APK to Firebase
* Install the Firebase tools by opening a terminal or command prompt and entering the following command: npm install -g firebase-tools
* Log into your Firebase account by running the command: firebase login
* Follow the login prompts and select the “Vacation Planner” project you created in the Firebase Console.
* Copy the release APK from the “app/release” directory and paste it into the “firebase/public/assets” directory.
* Navigate to the “firebase/public/assets” directory using your terminal.
* Deploy the updated APK to FireBase by running the command: firebase deploy
* The terminal should provide you a URL to a static site where the APK can be downloaded from. 

# Install the Vacation Planner on an Android Device
* Open a browser and visit the website https://vacation-planner-f337b.web.app/ or the URL generated in the previous steps.
* Select the “Download” image icon.
* Once the APK file downloads to the mobile device, select “Open”.
* If you receive a security warning that your device isn’t allowed to install unknown apps, open your device settings and check the “Allow permission.” radio button.
* When prompted, “Do you want to install this app?” select “Install”. 
* Once the installation is completed, you will receive a notification that the installation is completed; select “Open.”

# Enable Notifications from the Vacation Planner Application
* Open the “Settings” of your Android device.
* Select “Notifications” and then select “App notifications”.
* Scroll through the list of applications; when you locate D424 Vacation Planner, toggle the radio button to the “on” position to allow notifications. 

