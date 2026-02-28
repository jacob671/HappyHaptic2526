#!/usr/bin/env bash
# capture_logs.sh — Stream ADB logcat from the Control Hub to a timestamped file.
# Usage: ./capture_logs.sh [IP:PORT]
# Default target: 192.168.43.1:5555

TARGET="${1:-192.168.43.1:5555}"
mkdir -p logs
LOGFILE="logs/session_$(date +%Y%m%d_%H%M%S).txt"

echo "Logging to: $LOGFILE"
echo "Target:     $TARGET"
echo "Press Ctrl+C to stop."
echo ""

# Clean exit on Ctrl+C
trap 'echo ""; echo "Stopped. Log saved to: $LOGFILE"; exit 0' INT

while true; do
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] Connecting to $TARGET..." | tee -a "$LOGFILE"
    adb connect "$TARGET" >> "$LOGFILE" 2>&1

    # Stream logcat; exits when connection is lost
    adb -s "$TARGET" logcat -v time >> "$LOGFILE" 2>&1

    echo "[$(date '+%Y-%m-%d %H:%M:%S')] Connection lost. Reconnecting in 3 seconds..." | tee -a "$LOGFILE"
    sleep 3
done
