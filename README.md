# MyMDB-Microservice1-Identity_Management_API

## About the Project

MyMDB is a project showcasing my knowledge of Web API Design and Implementation. MyMDB is a Movie Catalog web application where you can search for and buy movies.

The Identity Management API (IDM) is the first of three microservices that make up the backend of the application. It is the service used for authenticating identities and to grant appropriate access to the set of services within the system. Within the context of this project, the IDM API will also control which API calls users have access to by using session objects to establish identity.

### Built With
- [Java 8](https://www.java.com/en/download/help/java8.html) (Gradle, Jersey, Grizzly, JSON, JDBC)
- [MySQL](https://www.mysql.com/)

### API Documentaion
[Identity Management API Documentation](https://docs.google.com/document/d/1snbgpxpdNcJfWLGS4e2b38F7sD-myZpeCC3b5wdUONA/edit?usp=sharing)


## Getting Started

### Prerequisites
To run this project locally, you must be able to run [JDK 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) Java Projects, as well as have a MySQL database to connect to the backend. A [local MySQL database](https://www.mysql.com/downloads/) instance would suffice.


### Installation
```
git clone https://github.com/tsamonte/MyMDB-Microservice1-Identity_Management_API
```
After cloning this repo into your local system, no further library downloads  in Java are needed manually. Required packages and libraries are handled the build automation tool [Gradle](https://gradle.org/)


### MySQL Database Initialization
Within your MySQL database, initialize the database by running the files found in the [db_scripts directory](https://github.com/tsamonte/MyMDB-Microservice1-Identity_Management_API/tree/master/db_scripts) of the repository:
- CreateUsersDB.sql
- InitUsersDB.sql

Ensure these files are run in the order above.


### Java Configuration File
Before running the project, create a configuration file called "config.yaml" in the root of the Java project. The structure of "config.yaml" should be as follows:
```yaml
databaseConfig:
  dbUsername: 
  dbPassword: 
  dbHostname: 
  dbPort: 
  dbDriver: com.mysql.cj.jdbc.Driver
  dbName: MyMDB_USERS
  dbSettings: ?autoReconnect=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=PST

serviceConfig:
  scheme: http://
  hostName: 0.0.0.0
  port: 12345
  path: /api/idm

loggerConfig:
  outputDir: ./logs/
  outputFile: test.log

sessionConfig:
  timeout: 600000
  expiration: 1800000
```
Fill in the blank fields with your information.

## Usage
After completing all all prerequisites, you can build the project using the following command:
```
gradlew build
```

Run the project using the following command:
```
java -jar build/libs/tsamonte.service.idm.jar -c config.yaml
```
