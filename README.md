## README

This was the final assignment for the Java Applications class at Algonquin College.

It is a pair of applications: a Server and a Client.

### Server
The server runs in the command line and accepts connections on a port. The server is 
multithreaded, and every connection has it's own thread. It accepts commands from the 
client and responds back to it.

### Client
The client is a Swing-based GUI application. It will connect to a server at a given location
and port, and users are able to type in console commands.
This application is also multithreaded, with the GUI view on one thread and server connections
on another.

#### This repo is for demo purposes only.
