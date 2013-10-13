-- Generated by Oracle SQL Developer Data Modeler 3.3.0.747
--   at:        2013-10-13 21:09:00 WEST
--   site:      Oracle Database 11g
--   type:      Oracle Database 11g




CREATE TABLE Ideias
  (
    iid       NUMBER (7) NOT NULL ,
    descricao VARCHAR2 (150) ,
    activa    NUMBER (1) ,
    userid    NUMBER (7) NOT NULL
  )
  LOGGING ;
ALTER TABLE Ideias ADD CONSTRAINT Ideias_PK PRIMARY KEY
(
  iid
)
;

CREATE TABLE RelacaoIdeias
  (
    iid1         NUMBER (7) NOT NULL ,
    iid2         NUMBER (7) NOT NULL ,
    tipo_relacao NUMBER (1)
  )
  LOGGING ;
ALTER TABLE RelacaoIdeias ADD CONSTRAINT RelacaoIdeias_PK PRIMARY KEY
(
  iid1, iid2
)
;

CREATE TABLE Shares
  (
    iid        NUMBER (7) NOT NULL ,
    userid     NUMBER (7) NOT NULL ,
    num_shares NUMBER (7) ,
    valor      NUMBER (10)
  )
  LOGGING ;
ALTER TABLE Shares ADD CONSTRAINT Shares_PK PRIMARY KEY
(
  iid, userid
)
;

CREATE TABLE Topicos
  (
    tid       NUMBER (7) NOT NULL ,
    nome      VARCHAR2 (20) ,
    descricao VARCHAR2 (100) ,
    userid    NUMBER (7) NOT NULL
  )
  LOGGING ;
ALTER TABLE Topicos ADD CONSTRAINT Topicos_PK PRIMARY KEY
(
  tid
)
;

CREATE TABLE TopicosIdeias
  (
    iid NUMBER (7) NOT NULL ,
    tid NUMBER (7) NOT NULL
  )
  LOGGING ;
ALTER TABLE TopicosIdeias ADD CONSTRAINT TopicosIdeias_PK PRIMARY KEY
(
  iid, tid
)
;

CREATE TABLE Transacoes
  (
    uid1  NUMBER (7) NOT NULL ,
    uid2  NUMBER (7) NOT NULL ,
    valor NUMBER (10) NOT NULL ,
    iid   NUMBER (7) NOT NULL
  )
  LOGGING ;
ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_PK PRIMARY KEY
(
  uid1, uid2
)
;

CREATE TABLE Utilizadores
  (
    userid       NUMBER (7) NOT NULL ,
    email        VARCHAR2 (50) ,
    username     VARCHAR2 (20) ,
    pass         VARCHAR2 (20) ,
    dinheiro     NUMBER (7) ,
    data_registo DATE
  )
  LOGGING ;
ALTER TABLE Utilizadores ADD CONSTRAINT Utilizadores_PK PRIMARY KEY
(
  userid
)
;

ALTER TABLE RelacaoIdeias ADD CONSTRAINT RelacaoIdeias_Ideias_FK FOREIGN KEY ( iid1 ) REFERENCES Ideias ( iid ) NOT DEFERRABLE ;

ALTER TABLE RelacaoIdeias ADD CONSTRAINT RelacaoIdeias_Ideias_FKv2 FOREIGN KEY ( iid2 ) REFERENCES Ideias ( iid ) NOT DEFERRABLE ;

ALTER TABLE Shares ADD CONSTRAINT Shares_Ideias_FK FOREIGN KEY ( iid ) REFERENCES Ideias ( iid ) NOT DEFERRABLE ;

ALTER TABLE Shares ADD CONSTRAINT Shares_Utilizadores_FK FOREIGN KEY ( userid ) REFERENCES Utilizadores ( userid ) NOT DEFERRABLE ;

ALTER TABLE TopicosIdeias ADD CONSTRAINT TopicosIdeias_Ideias_FK FOREIGN KEY ( iid ) REFERENCES Ideias ( iid ) NOT DEFERRABLE ;

ALTER TABLE Topicos ADD CONSTRAINT Topicos_Utilizadores_FK FOREIGN KEY ( userid ) REFERENCES Utilizadores ( userid ) NOT DEFERRABLE ;

ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_Ideias_FK FOREIGN KEY ( iid ) REFERENCES Ideias ( iid ) NOT DEFERRABLE ;

ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_Utilizadores_FK FOREIGN KEY ( uid1 ) REFERENCES Utilizadores ( userid ) NOT DEFERRABLE ;

ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_Utilizadores_FKv2 FOREIGN KEY ( uid2 ) REFERENCES Utilizadores ( userid ) NOT DEFERRABLE ;

ALTER TABLE Ideias ADD CONSTRAINT userid FOREIGN KEY ( userid ) REFERENCES Utilizadores ( userid ) NOT DEFERRABLE ;


-- Oracle SQL Developer Data Modeler Summary Report: 
-- 
-- CREATE TABLE                             7
-- CREATE INDEX                             0
-- ALTER TABLE                             17
-- CREATE VIEW                              0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           0
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                          0
-- CREATE MATERIALIZED VIEW                 0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0
