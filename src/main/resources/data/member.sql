show tables ;

CREATE TABLE ff_member(
  userid VARCHAR(50) PRIMARY KEY
  ,password VARCHAR(100) NOT NULL
  ,username VARCHAR(50) NOT NULL
  ,regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
  ,updatedate TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
  ,enabled CHAR(1) DEFAULT '1'
);

CREATE TABLE ff_member_auth(
    userid VARCHAR(50) NOT NULL
    ,auth VARCHAR(50) NOT NULL
    ,CONSTRAINT ff_member_auth_userid_fk FOREIGN KEY(userid) references ff_member(userid)
);

-- userid와 auth조합이 유일해야한다. 예) user01,ROLE_USER 행과 user01,ROLE_MEMBER 행은 가능
-- user01,ROLE_USER 이 있으면 user01,ROLE_USER는 불가능
CREATE UNIQUE INDEX ix_auth_username ON ff_member_auth (userid,auth);

INSERT INTO ff_member (userid, password, username) VALUES ('admin', '1234', )
insert into ff_member (userid, password, username) values ('manager89','pw89','미나미 아이자와');
insert into ff_member_auth (userid, auth) values ('manager89','ROLE_MANAGER');

SELECT * FROM ff_member;
SELECT * FROM ff_member_auth;

DELETE FROM FF_MEMBER;
DELETE FROM ff_member_auth;

-- 스프링 시큐리티 자동 로그인 정보를 저장 유지하는 테이블 생성
CREATE TABLE persistent_logins(
      username VARCHAR(64) NOT NULL , -- 회원 아이디
      series VARCHAR(64) PRIMARY KEY , -- 비번
      token VARCHAR(64) NOT NULL , -- 토큰정보
      last_used TIMESTAMP NOT NULL -- 로그인 한 날짜 시간
);

SELECT * FROM persistent_logins;
DELETE FROM persistent_logins;
DROP TABLE persistent_logins;

SELECT mem.userid,password,username,enabled,regdate,updatedate,auth
FROM ff_member mem left outer join ff_member_auth auth
ON mem.userid = auth.userid;

SELECT * FROM users;

