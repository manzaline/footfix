CREATE TABLE ff_comment(
  cno INT PRIMARY KEY ,
  bno INT NOT NULL ,
  commenter VARCHAR2(100) NOT NULL ,
  commentText VARCHAR2(4000) NOT NULL,
  comm_regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  comm_editdate TIMESTAMP,
  comm_ref INT NOT NULL ,
  comm_step INT default 0,
  comm_depth INT default 0,
  FOREIGN KEY (bno) REFERENCES bbs_footfix(bbs_no)
);

SELECT * FROM ff_comment ORDER BY cno DESC ;
DROP TABLE ff_comment;
DELETE FROM ff_comment;

CREATE SEQUENCE cnoRef_seq
    START WITH 1
    INCREMENT BY 1
    MAXVALUE NULL
    NO CACHE;

SELECT



