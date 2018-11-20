# ChristmasTreePi Library
## Pi4J based project to control Christmas Tree controlled by Raspberry Pi

This directory contains the opensource library jars needed to pull off this application.  I put the jars in the project rather than allowing gradle to download manually from the maven repository because some students will run from JGrasp IDE which doesn't have native Gradle support.

### RaspBerry Pi (pi4j*.jar)
The key library that drives the GPIO pins is the Pi4J library.  It seems that the libraries aren't kept current and there isn't many contributors, because the only support for Raspberry Pi 3 and above is a non release snapshot which is included.  I also found the Pin numbers don't exactly match the Pi Pinout charts published.  I basically had to go through each pin to see which ones lit up.

### Music
If you dig into opensource libraries, it's difficult to find OpenSource libraries that can play music (MP3).  Initially this project used the JMF framework which dates back to 1997.  After a few trials & errors, I opted to go with the [JavaZoom] JLayer libraries.

* JMF (Java Media Framework) - Not used
    _jmf-2.1.1e.jar_ as well as _mp3plugin.jar_ are very old and are no longer used for this project.  I found that they would not support most of the MP3s I threw at it.  The MP3 codec is bundled separately from the JMF jar.  More complicated and outdated than it's worth, but I'm leaving here for educational sake.
   
* JLayer from [Javazoom](http://www.javazoom.net/javalayer/javalayer.html)
   _jlayer-1.0.1.jar_ is a more updated player that bundles the MP3 codec together into one package.  I put the jar in the p