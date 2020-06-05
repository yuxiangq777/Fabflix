- # General
    - #### Team#: 165
    
    - #### Names: Yuxiang Qian (only 1 member); 
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment: 
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

    - #### Collaborations and Work Distribution: I do not have a teammate, there is only one member in this team. I contribute to everything in the project.


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
        - [context.xml](Webapp/WebContent/META-INF/context.xml)<br />
        [AddMovieServlet.java](Webapp/src/AddMovieServlet.java)<br />
        [AddStarServlet.java](Webapp/src/AddStarServlet.java)<br />
        [AutoCompleteServlet.java](Webapp/src/AutoCompleteServlet.java)<br />
        [CartServlet.java](Webapp/src/CartServlet.java)<br />
        [GenreServlet.java](Webapp/src/GenreServlet.java)<br />
        [LoginServlet.java](Webapp/src/LoginServlet.java)<br />
        [MetadataServlet.java](Webapp/src/MetadataServlet.java)<br />
        [MovieListServlet.java](Webapp/src/MovieListServlet.java)<br />
        [PayServlet.java](Webapp/src/PayServlet.java)<br />
        [SingleMovieServlet.java](Webapp/src/SingleMovieServlet.java)<br />
        [SingleStarServlet.java](Webapp/src/SingleStarServlet.java)
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
        - To utilize connection pooling, we add these configurations to context.xml:
            ```
            maxTotal="100" maxIdle="30" maxWaitMillis="10000"
            ```
          and we add 
            ```
            "autoReconnect=true&amp;useSSL=false&amp;cachePrepStmts=true"
            ```
          to the url. Then in every servlet that need connections to the database, we add the following code:
            ```
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();
            ```
    - #### Explain how Connection Pooling works with two backend SQL.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
