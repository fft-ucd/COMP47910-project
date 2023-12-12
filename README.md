# Read Me First

This is the project created for the COMP47910 module at UCD. It is a web application that allows users to search for hotels in a given city and book a room. The application is built using Spring Boot and Vue.js. 

The following was discovered as part of building this project:

The code that you will download from GitHub can be logically divided into two sections

	the frontend whibych yuo will find under the frontend folder
	the backend which you will find under the src folder
    the mysql database which find in the docker-compose.yml file, and the init script under the resources folder schema.sql and data.sql

For simplicity, you can run the BACKEND using the Tomcat embedded in Intellij Idea.
DemoApplication is located in src/main/java/com.marriott.webapp

Another option is to run the Gradle task bootRun from the command line

    ./gradlew bootRun

Please remember to use the correct Java version (17) and Gradle version (7.6.1) as specified in the build.gradle file.

The FRONTEND uses Vue.js, setup and run instructions are at the bottom of this document.

***** MySQL installation and configuration *****

You will also need a mysql database. For simplicity we used a Docker MySQL image. The caveat is that you may need to connect/expose port 3306 from Docker to your host computer.

To be able to expose port 3306 of the Docker image to Windows start Docker from the command line using the following command

	docker run -p 3306:3306 mysql/mysql-server:latest
    // or run the docker-compose.yml file facilitated in the project
    docker-compose -f docker-compose.yml up -d db 

This creates a new Container in your Docker Desktop (which can be stopped and started from the GUI, it maintains the correct config)
Docker will assign it a name and ID, for example
lucid_goldwasser
336cea850716fedb68c8dfbfd4d85007a3c7d086346b4882de516c258ad631a1

The startup command will also output a default password, for example ;41=OkvM_A2=r9TS7LtRm.:F6o,c#T27

At this point using netstat you should be able to see a process running on port 3306 but you will not be able to connect until you make the following two changes:

1) change root password
2) allow root user to connect from addresses other than localhost: by default it's only from localhost, but localhost refers to the docker image itself

To change the root password
Opened a new admin cmd window
Run: docker exec -it 336cea850716fedb68c8dfbfd4d85007a3c7d086346b4882de516c258ad631a1 bash
### note: substitute the example ID above with the one for your docker image
Issue (in mysql>): ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
### note: root is the new password
Issue (in mysql>): flush privileges;

To allow connections from outside docker localhost

	Verify only localhost allowed:
	SELECT host, user FROM mysql.user;
	+-----------+------------------+
	| host      | user             |
	+-----------+------------------+
	| localhost | healthchecker    |
	| localhost | mysql.infoschema |
	| localhost | mysql.session    |
	| localhost | mysql.sys        |
	| localhost | root             |
	+-----------+------------------+
	5 rows in set (0.00 sec)

then:

	UPDATE mysql.user SET host='%' WHERE user='root';

Now:

	mysql> SELECT host, user FROM mysql.user;
	+-----------+------------------+
	| host      | user             |
	+-----------+------------------+
	| %         | root             |
	| localhost | healthchecker    |
	| localhost | mysql.infoschema |
	| localhost | mysql.session    |
	| localhost | mysql.sys        |
	+-----------+------------------+
	5 rows in set (0.00 sec)

Now you can connect using MySQL Workbench which you can download and install separately if you wish so.

Create a new schema called db.

Note: The properties below can be changed in the application.properties file. They help to initialise the database with some data.

When running the application for the first time, you may need to change the following properties to create the schema and populate it with some data:

    spring.jpa.hibernate.ddl-auto=validate
    spring.sql.init.mode=always

After the first run, you can change them back to:

    spring.jpa.hibernate.ddl-auto=validate
    spring.sql.init.mode=never

Another option is to use the following properties:

    spring.jpa.hibernate.ddl-auto=create-drop
    spring.sql.init.mode=never
    spring.jpa.defer-datasource-initialization=true

This will recreate the schema every time you run the application.

### Environment Setup
Looking at the docker-compose.yml file and the application.properties file you will see that there many configuration that can be customized via environment variables.

    MYSQL_HOST
    SPRING_DATASOURCE_USERNAME
    SPRING_DATASOURCE_PASSWORD
    CORS_ALLOWED_ORIGINS
    ENCRY_SCRT
    ENCRY_IV
    VUE_APP_BACKEND_URL

Those are the most important ones. You can change them in the docker-compose.yml file or in the application.properties file.

***** To run the FRONTEND *****

1) Installed nodejs
2) opened the terminal in the frontend folder (C:\Users\Administrator\IdeaProjects\COMP47910-project\frontend)
3) run "npm install"
4) ran "npm install -g @vue/cli"
5) ran "vue serve"

***** Final comments *****

It's also possible to run the application using Docker. You can find the Dockerfile in the root folder. You can build the image and run using the following command:

    ./run_all.sh

Then you can run the image using the following command:

    ./stop_all.sh

We also facilitated more data in the db_scripts.zip file. You need to run them manually using a Sql client.

Please contact us if you have any questions.
