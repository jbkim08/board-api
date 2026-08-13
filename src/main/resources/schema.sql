DROP TABLE IF EXISTS attachment;
DROP TABLE IF EXISTS comment;  -- 댓글 삭제가 맨 위로 오게함 ( 외래키 제약조건 )
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
    password    VARCHAR(200)  NOT NULL,     -- BCrypt로 암호화되어 저장됨
    nickname    VARCHAR(50)   NOT NULL,
    refresh_token VARCHAR(500)  NULL,       -- Refresh Token
    role        VARCHAR(20)   DEFAULT 'ROLE_USER',
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comment (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id    BIGINT        NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    writer      VARCHAR(50)   NOT NULL,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES board(id)
);

-- 게시글에 첨부파일 테이블
CREATE TABLE attachment (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id      BIGINT        NOT NULL,
    original_name VARCHAR(255)  NOT NULL,
    stored_name   VARCHAR(255)  NOT NULL,
    file_path     VARCHAR(500)  NOT NULL,
    file_size     BIGINT        NOT NULL,
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES board(id) ON DELETE CASCADE
);
