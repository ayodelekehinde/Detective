### Detective
A realtime image detection sample app written in compose

Steps to run
1. Install [Android Studio](https://developer.android.com/studio)
2. Open Android studio and create a new project
3. Click "Get from VCS"
4. Enter this clone url: https://github.com/ayodelekehinde/Detective.git
5. Sync the project
6. Click on the play button to run the app.

#### Assumptions
1. Made assumptions about the category of the objects detected, they are limited to just
  person, thing, animals, plants and unknown. So some objects that falls within this categories might be missed as the list is not exhaustive.

#### Challenges faced
1. Had an issue with taking a snapshot of the current frame due the dimension and orientation of image on the preview, so the saved images were saved as landscape.
So I had to rotate the image before saving.
2. I also had the issue of taking a snapshot of the frame as some of the UI controls are showing up in the snapshot due to the fact that I am using compose.
I addressed this issue by taking the Bitmap of the current frame, then setting the bitmap to an Image compose while drawing the current 
detection result on it.
