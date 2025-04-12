#!/bin/bash

# XPerience Test Client Script

if [ $# -ne 2 ]; then
    echo "Usage: $0 <host> <port>"
    exit 1
fi

HOST=$1
PORT=$2

echo "Testing XPerience server at $HOST:$PORT"

# Function to test an event
test_event() {
    local name=$1
    local date=$2
    local time=$3
    local desc=$4
    local pass=$5
    
    echo -e "\nTesting event: $name"
    echo "Date: $date, Time: $time"
    echo "Description: $desc"
    echo "Password: $pass"
    
    # Build the message
    local message="$name#$date#$time#$desc#$pass"
    
    # Use netcat to send the message and receive response
    response=$(echo "$message" | nc -w 2 $HOST $PORT)
    
    echo "Server response: $response"
    
    # Check if the response starts with "Accept"
    if [[ $response == Accept* ]]; then
        echo "✓ Event accepted"
    else
        echo "✗ Event rejected"
    fi
}

# Test cases with valid passwords from passwords.txt
# Make sure these passwords exist in your passwords.txt file
test_event "Conference" "2025-04-15" "09:00" "Annual tech conference" "password123"
test_event "Workshop" "2025-04-16" "14:30" "Hands-on coding session" "test456"
test_event "Networking" "2025-04-17" "18:00" "Meet and greet with industry professionals" "secure789"
test_event "Hackathon" "2025-04-18" "10:00" "24-hour coding competition" "xperience2025"

# Test duplicate event (should be rejected)
test_event "Conference" "2025-05-20" "10:00" "This is a duplicate and should be rejected" "testuser"

test_event "Invalid Date" "04-20-2025" "12:00" "Event with incorrect date format" "admin1234"

test_event "Invalid Time" "2025-04-20" "12:00pm" "Event with incorrect time format" "secretpass"

echo -e "\nAll tests completed"
