# COMP6231_Project

## Introduction

This project is a software failure tolerant/highly available distributed player status system. There are three locations in this system: North-America (NA), Europe (EU) and Asia (AS). Each location has a server, which can perform some operations and maintain player accounts. Player accounts contain the information of players in this location. Also, the server contains some logs about the history of all operations that have been performed on this server.

There are two types of users: Players and Administrators. A player or administrator can be identified by his username, password, and IP address. When a person performs operations on the client, his location can be identified by his IP address and his operation can be executed on this server. In this project, the player and the administrator have their client, and all administrators use the same username and password. All operations of players and administrators can be recorded in logs.

#### Player specific operations:
1. Create a player account
2. Player sign in
3. Player sign out
4. Player transfer account
#### Administrator specific operations:
1. Get player status
2. Suspend player account

## Architecture and Process
The project contains two clients, a front end and three replicas. Each replica consists of a leader, a replica manager, and three servers. One of the leaders is assigned as the main leader. Players and administrators send a CORBA request to the front end through the client, and then the front end forwards the request to the main leader. The main leader forwards this message to the other two leaders using reliable UDP. The three replicas independently perform operations upon request and return the results to the main leader. The main leader analyzes the three results and sends the single correct result back to the front end. At the same time, for the replica that returns the incorrect result, the main leader will remind its corresponding replica manager. After receiving the response returned by the leaders, the front end returns the result to the client and complete the entire process.

## Active Replication
## Reliable UDP
## CORBA
## Concurrency
## Use the Project
