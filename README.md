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
If you wish to not use Docker, you may skip this part and install/run MySQL DB server by your own and move straight to `SQL scritps` section.

To spin up the MySQL DB inside the Docker container, use:

```shell
docker compose up
```

#### SQL scripts
Run the following SQLs to create all the necessary tables/routines/triggers:

- image table

```sql
CREATE TABLE `image`
(
    `id`          int          NOT NULL AUTO_INCREMENT,
    `name`        varchar(500)          DEFAULT NULL,
    `style_id`    int                   DEFAULT NULL,
    `status`      varchar(100) NOT NULL DEFAULT 'Submitted',
    `upload_date` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
)
```

- image statistics table

```sql
CREATE TABLE `image_statistics`
(
    `style_id`         int       NOT NULL,
    `num_of_submitted` int       NOT NULL DEFAULT '0',
    `max_upload_date`  timestamp NULL     DEFAULT NULL
)
```

- routine to calculate statistics by images for each style

```sql
CREATE PROCEDURE update_image_statistics_by_style_id(IN style_id_param int)
BEGIN
    INSERT INTO demo.image_statistics (style_id, max_upload_date, num_of_submitted)
    SELECT img.style_id,
           (SELECT MAX(image.upload_date) as max_upload_date
            from demo.image image
            WHERE image.style_id)                  AS max_upload_date,
           SUM(IF(img.status = 'Submitted', 1, 0)) AS num_of_submitted
    FROM demo.image AS img
    WHERE img.style_id = style_id_param
    GROUP BY img.style_id
    ON DUPLICATE KEY UPDATE demo.image_statistics.max_upload_date  = VALUES(max_upload_date),
                            demo.image_statistics.num_of_submitted = VALUES(num_of_submitted);
END;
```

- trigger to activate each time the image is inserted

```sql
DELIMITER //
CREATE TRIGGER demo.image_created
    AFTER INSERT
    ON demo.image
    FOR EACH ROW
BEGIN
    CALL demo.update_image_statistics_by_style_id(NEW.style_id);
END//
DELIMITER ;
```

## Run
Use your IDE to run the app or if you wish to run the app from the console, use:

```shell
./mvnw spring-boot:run
```

The app should be accessible by the following link: http://localhost:8084
