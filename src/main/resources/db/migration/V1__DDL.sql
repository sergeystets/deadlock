CREATE TABLE `image`
(
    `id`          int          NOT NULL AUTO_INCREMENT,
    `name`        varchar(500)          DEFAULT NULL,
    `style_id`    int                   DEFAULT NULL,
    `status`      varchar(100) NOT NULL DEFAULT 'Submitted',
    `upload_date` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `image_statistics`
(
    `style_id`         int       NOT NULL,
    `num_of_submitted` int       NOT NULL DEFAULT 0,
    `max_upload_date`  timestamp NULL     DEFAULT NULL,
    UNIQUE (`style_id`)
);

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


DELIMITER //
CREATE TRIGGER demo.image_created
    AFTER INSERT
    ON demo.image
    FOR EACH ROW
BEGIN
    CALL demo.update_image_statistics_by_style_id(NEW.style_id);
END//
DELIMITER ;