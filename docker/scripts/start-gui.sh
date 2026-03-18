#!/bin/sh
# =============================================================================
# Start JavaFX application with X11 forwarding
# =============================================================================

# Start X11 virtual display if not running on host X11
if [ -z "$DISPLAY" ] || [ "$DISPLAY" = ":1" ]; then
    echo "Starting virtual X11 display..."
    Xvfb :1 -screen 0 1024x768x24 &
    export DISPLAY=:1

    # Start window manager
    fluxbox &
fi

echo "Starting JavaFX Logistics Application..."
echo "Display: $DISPLAY"
echo "Java Options: $JAVA_OPTS"

# Launch JavaFX application
exec java $JAVA_OPTS \
    -Dprism.order=sw \
    -Dprism.gtk.disableSystemMenuBar=true \
    -cp "BOOT-INF/classes:BOOT-INF/lib/*" \
    com.logistics.app.MainApplication