CREATE TABLE bbs_footfix(
  bbs_no INT(8) PRIMARY KEY , -- 게시물번호 자동증가
  bbs_title varchar2(200) not null,
  bbs_writer varchar2(50) not null,
  bbs_cont varchar2(4000) not null,
  bbs_regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  bbs_editdate TIMESTAMP,
  bbs_fileName varchar2(200),
  bbs_filePath varchar2(500),
  bbs_hit INT(8) DEFAULT 0,
  bbs_ref INT(8) , -- 원본글과 답변글을 묶어주는 그룹번호
  bbs_step INT(8) DEFAULT 0, -- 첫번째 답글이면1, 두번째 답글이면2.... 원본글이면 0
  bbs_depth INT(8) DEFAULT 0, -- 답변글 정렬순서(=level)
);

-- 테이블 살리고 데이터만삭제하고 게시물번호 1부터 다시시작
TRUNCATE TABLE bbs_footfix;

-- 게시물번호 1부터 다시 시작, 하지만 기존 데이터와 번호중복
ALTER TABLE bbs_footfix ALTER COLUMN bbs_no RESTART WITH 1;
ALTER TABLE bbs_footfix ALTER COLUMN bbs_ref RESTART WITH 1;
ALTER TABLE bbs_footfix ALTER COLUMN bbs_cont SET NOT NULL ;
ALTER TABLE bbs_footfix ADD COLUMN bbs_editdate TIMESTAMP;
ALTER TABLE bbs_footfix ALTER COLUMN bbs_fileName RENAME TO bbs_filePath;
-- 테이블까지 전부삭제하고 게시물번호 1부터 다시시작
DROP TABLE bbs_footfix;
DELETE FROM bbs_footfix WHERE bbs_no = 101;
SELECT * FROM bbs_footfix ORDER BY bbs_no DESC ;

CREATE SEQUENCE bbs_noRef_seq
START WITH 1
INCREMENT BY 1
MAXVALUE NULL -- 시퀀스 최대값
NO CACHE;

SELECT NEXT VALUE FOR bbs_noRef_seq; -- h2 DB에서 시퀀스값 가져오기
DROP SEQUENCE bbs_noRef_seq;
ALTER SEQUENCE bbs_noRef_seq
RESTART WITH 1;

CREATE TABLE ff_dates(
    jobId VARCHAR2(64) ,
    date TIMESTAMP NOT NULL ,
    homeName VARCHAR2(64) NOT NULL ,
    awayName VARCHAR2(64) NOT NULL ,
    userId BIGINT ,
    FOREIGN KEY (userId) REFERENCES `USER`(id)
);

SELECT * FROM ff_dates;

DROP TABLE ff_dates;

SELECT * FROM FCMNOTIFICATION_TOKEN;
DROP TABLE fcmnotification_token;

show columns from FCMNOTIFICATION_TOKEN;

SELECT column_name, referenced_table_name, referenced_column_name
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE table_name = 'ff_dates' AND referenced_table_name IS NOT NULL;





