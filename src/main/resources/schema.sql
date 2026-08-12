DROP TABLE IF EXISTS board;
DROP TABLE IF EXISTS users;

CREATE TABLE board (
   id          BIGINT AUTO_INCREMENT PRIMARY KEY,
   title       VARCHAR(200)  NOT NULL,
   content     TEXT          NOT NULL,
   writer      VARCHAR(50)   NOT NULL,
   view_count  INT           DEFAULT 0,
   created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
   updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)   NOT NULL UNIQUE,
    password    VARCHAR(200)  NOT NULL,   -- BCrypt로 암호화되어 저장됨
    nickname    VARCHAR(50)   NOT NULL,
    role        VARCHAR(20)   DEFAULT 'ROLE_USER',
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);