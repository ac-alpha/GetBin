# GetBin
A project that allows users to add dustbin locations and to find dustbins near them.

Youtube Link : https://youtu.be/cdHjl7wBgQM

MAKE SURE THAT MOBILE DATA IS ON BEFORE USING THE APP…
• On opening the app swipe up to login/signup.
• If the location is off then the location settings are opened.
• On logging in, a MapActivity is opened with a blue marker showing our location
and the red ones denoting the currently added bins to the database.
• The seeker at the bottom sets the distance in meters upto which the bins have to
be displayed.
• The FAB, upon clicking shows 3 others FABs.
• On clicking Add bin FAB, new activity is opened.
• On capturing image, the image is analysed for finding bins.
• If bin is detected then a double tick FAB adds the bin to database.
• If bin is not detected, then an alert dialog says to recapture.
• If on recapturing again the bin is not detected, then the user may or may not add
the bin, i.e. on a second fail attempt, the user can add the bin directly.
• Upon clicking a red marker, the number of upvotes, downvotes and image
analysis results are shown.
• A new activity to rate the bin is open if a marker is selected only if the distance of
the bin is close to current location.
• Once the user has rated a particular bin then he can’t rate the same bin again.
• The Navigation Drawer contains some of the basic options.


KNOWN BUGS AND ISSUES:-
• The app occasionally crashes due to poor internet connection.
• Upon rating a bin, the database takes some time to update so the marker may not
update immediately.
• The image analysis leads to faulty results if the image is taken far from the bin.
