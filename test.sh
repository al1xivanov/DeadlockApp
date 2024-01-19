#!/bin/bash

# Function to get Java PID
getJavaPID() {
    local app_name="$1"
    # shellcheck disable=SC2155
    local java_process=$(jps | grep "$app_name" | awk '{print $1}')
    echo "$java_process"
}

# Function to check for deadlocks
checkDeadlock() {
    local java_pid=$1
    # shellcheck disable=SC2155
    # shellcheck disable=SC2126
    local deadlock_status=$(jstack "$java_pid" | grep -E 'Found one Java-level deadlock' | wc -l)
    if [[ $deadlock_status -gt 0 ]]; then
        echo "Failed: Application is deadlocked."
        # Showing results in logs
        # shellcheck disable=SC2034
        deadlock_log="logs/deadlock_$(date +'%Y%m%d_%H%M%S').log"
        jstack "$java_pid" | grep -E 'Found one Java-level deadlock' -A 9 > "$deadlock_log"
    else
        echo "Passed: Application is not deadlocked."
    fi
}

# Setting timeout
timeout_seconds=20
# Getting current time
start_time=$(date +%s)
# Calculating end time
# shellcheck disable=SC2034
end_time=$((start_time + timeout_seconds))

mkdir "logs"

# Loop until timeout is reached
while true; do
    sleep 1
    java_pid=$(getJavaPID "DeadlockApp")
    if [ -z "$java_pid" ]; then
        echo "Java process not found. Make sure the application 'DeadlockApp' is running."
        exit 1
    else
        echo "Java process found. PID: $java_pid - Checking for deadlocks..."
    fi

    # Check for deadlocks in the existing or new process
    checkDeadlock "$java_pid"

    # Sleep for a few seconds before the next check
    sleep 2

    current_time=$(date +%s)
    if [ "$current_time" -ge "$end_time" ]; then
        echo "Timeout reached. Exiting..."
        echo "Logs can be found in 'logs' directory. Check result."
        cat "$deadlock_log"
        exit
    fi
done