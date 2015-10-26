# TelemetryBrowser
The telemetry browser is an android app that listens to the connected bluetooth serial device for variables and coordinates.
The coordinates and variables are plotted on map and graphs.  This can show where odometry, gps, and kahlman filters expect the position and state of the machine to be in.
At this point the browser has the ability to discover and modify any float variable.  This should expand to any variable.
Since the variables that are displayed are set in the transmitting software (usually arduino) dynamic display of variables is expected.


Showing AVC telemetry is just the begining.  Simple text rules should allow any serial transmitter to display complex telemtery
