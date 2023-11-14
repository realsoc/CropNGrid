## CropNGrid: Privacy policy

Welcome to the CropNGrid app for Android!

This is an open source Android app developed by Hugo Djemaa. The source code is available on GitHub under the Apache 
License 2.0; 
the app is also available on Google Play.

As an avid Android user myself, I take privacy very seriously.
I know how irritating it is when apps collect your data without your knowledge.

I hereby state, to the best of my knowledge and belief, that I have not programmed this app to collect any 
personally identifiable information. All data (app preferences (like theme, etc.) and images) created by you (the 
user) is stored on your device only, and can be simply erased by clearing the app's data or uninstalling it.

### Explanation of permissions requested in the app

The list of permissions required by the app can be found in the `AndroidManifest.xml` file:

[Link to the file]

<br/>

| Permission | Why it is required |
| :---: | -- |
| `android.permission.READ_MEDIA_IMAGES` | Required to pick images from the phone memory. No permission: no images; no images: no crop no grid. The app would then be called NoCropNorGrid |
| `android.permission.WRITE_EXTERNAL_STORAGE` | Required for android < 10 in order to save images in the external storage.  |

 <hr style="border:1px solid gray">

If you find any security vulnerability that has been inadvertently caused by me, or have any question regarding how the app protectes your privacy, please send me an email or post a discussion on GitHub, and I will surely try to fix it/help you.

Yours sincerely,  
Hugo Djemaa.  
France.  
threelittledev@gmail.com

Thanks to [Wrichik Basu](https://github.com/WrichikBasu/ShakeAlarmClock/blob/master/PRIVACY_POLICY.md?plain=1) for the privacy policy model.