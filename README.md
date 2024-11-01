# SQL Deadlock Demo

This is a demo application to demonstrate SQL deadlocks.

## Prerequisites

You will need the following for the app to start:

- Java 17
- Docker

## Java

The app is running on `Java 17`, so make sure you have the proper version installed.

> #### Tip 💡
>  If you use [sdk man](https://sdkman.io/), do the following:
> - run `install` if Java 17 is not yet installed.
> ```shell
>  sdk install java 17.0.13-amzn
> ```
>- run `use` command to make Java 17 active for the current session:
>
>```shell
>sdk use java 17.0.13-amzn
>```

## Docker
Use the [official documentation](https://docs.docker.com/engine/install/) to install Docker. 
Alternatively you may not use Docker at all and install/run MySQL DB server by your own.

## Build:
The app is configured to use Maven and Maven Wrapper in particular. Please run the command below to install the project.
Note it will build up the project while skip running tests (remove `-DskipTests` if you wish to include test run as well).

```shell
./mvnw clean install -DskipTests
```

## Configuration
All properties are stored in `application.properties`. Please remember to either 
set proper value for `spring.datasource.password` directly or (better) set `MYSQL_PASSWORD`environment 
variable with a valid SQL password.



> #### Tip 💡
> You can set the env variable by running the following command:
>```shell
>export MYSQL_PASSWORD=password
>```

## DB

#### Docker
The project is shipped with `docker-compose.yml` to run MySQL DB inside Docker container.
If you wish to not use Docker, you may skip this part and install/run MySQL DB server by your own.

To spin up the MySQL DB inside the Docker container, use:

```shell
docker compose up
```

#### SQL
The project is configured to use [Flyway](https://github.com/flyway/flyway),
so no additional steps are needed to initialize the DB.
Please see `resources/db/migration` folder to find all the necessary SQL scripts.
Flyway configuration could be found in `resources/application.properties` prefixed with `spring.flyway.`.


## Run
Use your IDE to run the app, or if you wish to run the app from the console, use:

```shell
./mvnw spring-boot:run
```

The app should be accessible by the following link: http://localhost:8084
