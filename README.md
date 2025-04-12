XPerience Event Management System
A client-server application for managing events with secure password authentication.
Overview
XPerience is a Java-based event management system that allows clients to register events through a client-server architecture. The system implements a robust validation mechanism for event data and uses a one-time password authentication system for security.
Features

Client-Server Architecture: TCP/IP socket-based communication protocol
Secure Authentication: One-time use passwords loaded from a file
Data Validation: Comprehensive validation for event properties (name, date, time, description)
Persistence Options:

In-memory storage (EventStoreMemory)
Database storage (EventStoreDB) using DonaBase MySQL connection


Concurrent Client Handling: Multi-threaded server implementation with virtual threads
Logging: Detailed system logging for debugging and monitoring

Components

Event: Represents an event with name, date, time, and description
EventStore: Interface defining the event storage contract

EventStoreMemory: Thread-safe in-memory implementation
EventStoreDB: MySQL database implementation using DonaBase


PasswordList: Manages one-time passwords loaded from a file
ClientHandler: Processes client requests, authenticates, and validates event data
XPerienceServer: Single-threaded server implementation with in-memory storage
XPerienceServerDB: Multi-threaded server with database storage

Protocol
The XPerience communication protocol uses a simple text-based format with fields separated by # characters:
