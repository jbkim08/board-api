DROP TABLE IF EXISTS board;

CREATE TABLE board (
   id          BIGINT AUTO_INCREMENT PRIMARY KEY,
   title       VARCHAR(200)  NOT NULL,
   content     TEXT          NOT NULL,
   writer      VARCHAR(50)   NOT NULL,
   view_count  INT           DEFAULT 0,
   created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
   updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);