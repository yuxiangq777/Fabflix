## CS 122B Project 4 by team 165 
```
Team member: Yuxiang Qian ID:57066565
Webapp folder contains the fabflix web application
MobileApp folder contains the Android version of fabflix
Parser folder contains code for XML parsing
Commits after demo video include adding the video URL and change the readme title
```

#### 1. Demo Video URL

<https://youtu.be/ly1Jrqz9HEY>

#### 2. how to deploy the application with Tomcat
 - create a test user and grant privileges:
    ```
    mysql> CREATE USER 'mytestuser'@'localhost' IDENTIFIED BY 'mypassword';
    mysql> GRANT ALL PRIVILEGES ON * . * TO 'mytestuser'@'localhost';
    mysql> quit;
    ```
- create the database moviedb (you can create your own movie-data.sql):
    ```
    shell>mysql -u mytestuser-p < create_table.sql 
    shell>mysql -u mytestuser -p --database=moviedb < movie-data.sql 
    ```
- On the development machine: Use maven to import the project, and make sure 
you have complete IntelliJ IDEA Tomcat configuration. Then run the project.
- On the AWS instance, in Webapp repo:
    ```
    shell>mvn package
    shell>cp ./target/*.war /home/ubuntu/tomcat/webapps
    Then refresh the tomcat manager page and click on project_2
    Then you should successfully see the website.
    ```

#### 3. Each member's contribution
I do not have a teammate, there is only one member in this team. I contribute to everything in the project.
