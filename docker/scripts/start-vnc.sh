#!/bin/sh
# =============================================================================
# Start VNC server for remote access to JavaFX GUI
# =============================================================================

# Clean any existing locks
rm -f /tmp/.X1-lock /tmp/.X11-unix/X1

# Start virtual display
echo "Starting Xvfb virtual display..."
Xvfb :1 -screen 0 1280x720x24 &
XVFB_PID=$!

# Wait for X to start
sleep 2

# Start window manager
echo "Starting Fluxbox window manager..."
DISPLAY=:1 fluxbox &
FLUXBOX_PID=$!

# Start VNC server
echo "Starting VNC server on port 5900..."
x11vnc -display :1 -forever -shared -ncache 10 -rfbport 5900 &
VNC_PID=$!

# Function to cleanup processes on exit
cleanup() {
    echo "Stopping services..."
    kill $VNC_PID $FLUXBOX_PID $XVFB_PID 2>/dev/null
    exit
}

# Set up signal handlers
trap cleanup TERM INT

# Wait for VNC to start
sleep 3

# Start JavaFX application
echo "Starting JavaFX Logistics Application..."
echo "VNC access available on port 5900"
echo "Use VNC viewer to connect to: <container-ip>:5900"

export DISPLAY=:1
exec java $JAVA_OPTS \
    -Dprism.order=sw \
    -Dprism.gtk.disableSystemMenuBar=true \
    -cp "BOOT-INF/classes:BOOT-INF/lib/*" \
    com.logistics.app.MainApplication