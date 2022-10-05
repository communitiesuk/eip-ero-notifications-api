# eip-ero-notifications-api
Spring Boot microservice that :
- Provides an API for sending notifications using the [Government Notify](https://www.notifications.service.gov.uk/documentation) service.

## Developer Setup
### Kotlin API Developers

Configure your IDE with the code formatter (ktlint):
```
$ ./gradlew ktlintApplyToIdea
```
This only needs doing once to setup your IDE with the code styles.

#### Running Tests
In order to run the tests successfully, you will first need to set the `LOCALSTACK_API_KEY` environment variable (i.e.
within your .bash_profile or similar). Then run:
```
$ ./gradlew check
```
This will run the tests and ktlint. (Be warned, ktlint will hurt your feelings!)

#### Building docker images
```
$ ./gradlew check bootBuildImage
```
This will build a docker image for the Spring Boot application.

## Running the application
Either `./gradlew bootRun` or run the class `VoterCardApplicationsApiApplication`

### External Environment Variables
The following environment variables must be set in order to run the application:
* `AWS_ACCESS_KEY_ID` - the AWS access key ID
* `AWS_SECRET_ACCESS_KEY` - the AWS secret access key
* `AWS_REGION` - the AWS region
* `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` - the uri of the cognito ERO user pool JWT issuer.

#### MYSQL Configuration
For local setup refer to src/main/resources/db/readme.
* `MYSQL_HOST`
* `MYSQL_PORT`
* `MYSQL_USER`
* `MYSQL_PASSWORD` - only used locally or when running tests

#### Infrastructure overrides
The following are overridden by the task definition in AWS:
* `SPRING_DATASOURCE_URL` - This is set to the deployed RDS' URL.
* `SPRING_DATASOURCE_DRIVERCLASSNAME` - This is overridden to use the AWS Aurora MySQL JDBC Driver.
* `SPRING_LIQUIBASE_DRIVERCLASSNAME` - This is overridden to use the AWS Aurora MySQL JDBC Driver.
*
#### Liquibase Configuration
* `LIQUIBASE_CONTEXT` Contexts for liquibase scripts.
  For local setup use ddl.

### Authentication and authorisation
Requests are authenticated by the presence of a signed cognito JWT as a bearer token in the HTTP request `authorization` header.  
EG: `Authorization: Bearer xxxxxyyyyyyzzzzz.....`  
Requests are authorised by their membership of groups and roles carried on the JWT token.  
The UI application is expected to handle the authentication with cognito and pass the JWT token in the `authorization` header.

### Test Liquibase Rollbacks
To test liquibase rollbacks try the following steps.
1. build a docker image based on the previous git commit.
2. remove docker containers and the docker_mysql-data docker volume
3. run start-docker to create the database and apply all previous changesets
4. having defined your new databaseChangeLog file, edit db.changelog-master.xml to comment out all references to other databaseChangeLog files
5. cd to your src/main/resources/db/changelog directory
6. define a liquibase.properties file, as shown below
7. to apply your latest DB changes execute `$LIQUIBASE_HOME/liquibase --log-level debug --contexts=ddl update`
8. verify your DB changes are as expected
9. to rollback your latest DB changes execute `$LIQUIBASE_HOME/liquibase --log-level debug --contexts="ddl" rollback-to-date '2022-08-23 15:17:13'` filling in the appropriate date from the DATABASECHANGELOG.DATEEXECUTED column
10. verify your DB changes are again as expected

A sample liquibase.properties file

```shell
changelog-file: db.changelog-master.xml
driver: com.mysql.cj.jdbc.Driver
url: jdbc:mysql://localhost:3306/voter_card_application
username: root
password: rootPassword
classpath: /home/valtech/IdeaProjects/eip/eip-ero-voter-card-applications-api/src/main/resources/db/changelog/mysql-connector-java-8.0.29.jar
context=ddl
```