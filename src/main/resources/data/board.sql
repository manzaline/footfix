CREATE TABLE bbs_footfix(
  bbs_no INT(8) PRIMARY KEY , -- 게시물번호 자동증가
  bbs_title VARCHAR(200) not null,
  bbs_writer VARCHAR(50) not null,
  bbs_cont VARCHAR(4000) not null,
  bbs_regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  bbs_editdate TIMESTAMP,
  bbs_fileName VARCHAR(200),
  bbs_filePath VARCHAR(500),
  bbs_hit INT(8) DEFAULT 0,
  bbs_ref INT(8) , -- 원본글과 답변글을 묶어주는 그룹번호
  bbs_step INT(8) DEFAULT 0, -- 첫번째 답글이면1, 두번째 답글이면2.... 원본글이면 0
  bbs_depth INT(8) DEFAULT 0 -- 답변글 정렬순서(=level)
);

CREATE TABLE sequences (
  name VARCHAR(32),
  currval BIGINT UNSIGNED -- UNSIGNED 음수가 불가능. 0과 양수만 가능
) engine=InnoDB;

-- CREATE PROCEDURE: 저장프로시저 생성,
CREATE PROCEDURE `create_sequence` (IN seq_name TEXT) -- 이런 개 싯팔 쉼표아니고 백틱, IN: 입력매개변수, seq_name: 매개변수의 이름. 프로시저 내부에서 이 이름을 통해 해당 값을 참조,, TEXT: 매개변수의 데이터 타입
  modifies sql data -- 프로시저가 데이터베이스의 데이터를 수정할 것임을 나타냅니다. MySQL에서는 프로시저가 데이터를 읽거나 수정하는지 여부를 명시적으로 지정해야 합니다. 이 경우, 프로시저가 데이터를 수정하는 작업을 수행한다는 것을 명시합니다.
  deterministic -- 이 구문은 프로시저가 동일한 입력값에 대해 항상 동일한 결과를 반환함을 나타냅니다. 즉, 프로시저가 입력값에 따라 결정론적으로 동작한다는 것을 의미합니다. 이는 프로시저의 예측 가능성을 높이고, 최적화 및 캐싱에 유리합니다.
BEGIN
  DELETE FROM sequences WHERE name = seq_name;
  INSERT INTO sequences (name, currval) VALUES (seq_name, 0);
END;

CREATE FUNCTION `nextval` (seq_name varchar(32)) -- 매개변수로 최대32자의 문자열 seq_name을 받는다
  RETURNS BIGINT UNSIGNED -- 함수가 반환할 데이터타입
  MODIFIES SQL DATA
  DETERMINISTIC
BEGIN
  DECLARE ret BIGINT UNSIGNED; -- 함수 내에서 사용할 변수선언(declare)
  UPDATE sequences SET currval = currval + 1 WHERE name = seq_name;
  SELECT currval INTO ret FROM sequences WHERE name = seq_name LIMIT 1; -- 일치하는 currval값을 ret에 저장, LIMIT 1 은 일치하는 행이 여러개일경우 첫번째 행만 선택
  RETURN ret;
end;

call create_sequence('nextval'); -- 반드시 한번은 실행. 다시 실행하면 currval 이 0이 되므로 시퀀스를 0부터 다시시작할때도 쓰인다.
select nextval('nextval');

show tables;
DROP TABLE sequences;
DROP PROCEDURE;



-- 테이블 살리고 데이터만삭제하고 게시물번호 1부터 다시시작
TRUNCATE TABLE bbs_footfix;

-- 게시물번호 1부터 다시 시작, 하지만 기존 데이터와 번호중복
ALTER TABLE bbs_footfix ALTER COLUMN bbs_no RESTART WITH 1;
ALTER TABLE bbs_footfix ALTER COLUMN bbs_ref RESTART WITH 1;
ALTER TABLE bbs_footfix ALTER COLUMN bbs_cont SET NOT NULL ;
ALTER TABLE bbs_footfix ADD COLUMN bbs_editdate TIMESTAMP;
ALTER TABLE bbs_footfix ALTER COLUMN bbs_fileName RENAME TO bbs_filePath;
-- 테이블까지 전부삭제하고 게시물번호 1부터 다시시작
DELETE FROM bbs_footfix WHERE bbs_no = 101;
SELECT * FROM bbs_footfix ORDER BY bbs_no DESC ;

-- 시퀀스
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
    jobId VARCHAR(64) ,
    date TIMESTAMP NOT NULL ,
    homeName VARCHAR(64) NOT NULL ,
    awayName VARCHAR(64) NOT NULL ,
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





