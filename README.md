# users-api
Backend (API) application form users app 

JDK: 17
Spring boot: 3.1.5

Steps to setup project:

1. create database & tables and insert Admin user in MySQL (see initial.sql in database-scripts directory)
2. Install lombok in your IDE if it is not installed already
3. Import as Maven project
4. change datasource properties in application.properties file if required
5. Run project as Spring boot application or as Java application (Main class UsersApplication.java)

**API endpoints:**  
Register new User: /register	POST  
User Login: /login	POST  
Send Reset Password Link: /resetpassword/{username}	GET
Reset Password: /resetpassword	POST  
List user: /users	GET (Authorization header)  
Get user by id: /users/{userId}	GET (Authorization header)  
Update user: /users	POST (Authorization header)  
Delete user: /users/{userId}	DELETE (Authorization header)



Some APIs require Authorization header, we can pass JWT token returned in login API as Bearer token


**Run from command line:**

Commands to run application from terminal:  
`mvn clean install -DskipTests`  
`mvn spring-boot:run`

Commands to execute test cases from terminal:  
`mvn clean test`


**Run with Docker:**  

Pull MySQL Image: `docker pull mysql`

Create network: `docker network create users-network`

Run MySQL container:  
`docker run --name mysql-container --network users-network -e MYSQL_ROOT_PASSWORD=Mysql8@admin -d  mysql`

Create Docker Image for users-api:  (Change JDBC url in application.properties, update localhost with mysql-container (name of the mysql container))
`docker build -t users-api .`

Run container from image:  
`docker run --network users-network --name users-api -d -p 8080:8080 users-api`
