CREATE TABLE ff_comment(
  cno INT PRIMARY KEY ,
  bno INT NOT NULL ,
  commenter VARCHAR(100) NOT NULL ,
  commentText VARCHAR(4000) NOT NULL,
  comm_regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  comm_editdate TIMESTAMP,
  comm_ref INT NOT NULL ,
  comm_step INT default 0,
  comm_depth INT default 0,
  FOREIGN KEY (bno) REFERENCES bbs_footfix(bbs_no)
);

CREATE TABLE comment_sequence (
  name VARCHAR(32),
  currval BIGINT UNSIGNED
) engine=InnoDB;

CREATE PROCEDURE `comment_sequence` (IN seq_name text)
  modifies sql data
  deterministic
  begin
    DELETE FROM comment_sequence WHERE seq_name = seq_name;
    INSERT INTO comment_sequence (name, currval) VALUES (seq_name, 0);
  end;

CREATE FUNCTION `next_commentval` (seq_name VARCHAR(32))
  RETURNS BIGINT UNSIGNED
  MODIFIES SQL DATA
  DETERMINISTIC
  begin
    DECLARE ret BIGINT UNSIGNED;
    UPDATE comment_sequence SET currval = currval + 1 WHERE seq_name = seq_name;
    SELECT currval INTO ret FROM comment_sequence WHERE seq_name = seq_name;
    RETURN ret;
  end;

CALL comment_sequence ('nextval');
SELECT next_commentval('nextval');

SELECT * FROM ff_comment ORDER BY cno DESC ;
DROP TABLE ff_comment;
DELETE FROM ff_comment;

