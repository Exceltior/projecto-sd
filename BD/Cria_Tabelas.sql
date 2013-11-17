-- Generated by Oracle SQL Developer Data Modeler 3.3.0.747
--   at:        2013-11-17 16:45:18 WET
--   site:      Oracle Database 11g
--   type:      Oracle Database 11g




CREATE TABLE Ideias
  (
    iid       NUMBER (7) NOT NULL ,
    titulo    VARCHAR2 (80) NOT NULL ,
    descricao VARCHAR2 (1000) NOT NULL ,
    userid    NUMBER (7) NOT NULL ,
    activa    NUMBER (1) NOT NULL
  ) ;
ALTER TABLE Ideias ADD CONSTRAINT Ideias_PK PRIMARY KEY
(
  iid
)
;

CREATE TABLE IdeiasFicheiros
  (
    iid          NUMBER (7) NOT NULL ,
    path         VARCHAR2 (70) NOT NULL ,
    OriginalFile VARCHAR2 (100) NOT NULL
  ) ;
ALTER TABLE IdeiasFicheiros ADD CONSTRAINT IdeiasFicheiros_PK PRIMARY KEY
(
  iid, path
)
;

CREATE TABLE Shares
  (
    iid       NUMBER (7) NOT NULL ,
    userid    NUMBER (7) NOT NULL ,
    numshares NUMBER (7) NOT NULL ,
    valor     NUMBER (10) NOT NULL ,
    numMin    NUMBER (7) NOT NULL
  ) ;
ALTER TABLE Shares ADD CONSTRAINT Shares_PK PRIMARY KEY
(
  iid, userid, numshares, valor, numMin
)
;

CREATE TABLE Topicos
  (
    tid       NUMBER NOT NULL ,
    nome      VARCHAR2 (20) NOT NULL ,
    descricao VARCHAR2 (150) NOT NULL ,
    userid    NUMBER (7) NOT NULL
  ) ;
ALTER TABLE Topicos ADD CONSTRAINT Topicos_PK PRIMARY KEY
(
  tid
)
;

CREATE TABLE TopicosIdeias
  ( tid NUMBER NOT NULL , iid NUMBER (7) NOT NULL
  ) ;
ALTER TABLE TopicosIdeias ADD CONSTRAINT TopicosIdeias_PK PRIMARY KEY
(
  iid, tid
)
;

CREATE TABLE Transacoes
  (
    transactionId NUMBER (7) NOT NULL ,
    comprador     NUMBER (7) NOT NULL ,
    vendedor      NUMBER (7) NOT NULL ,
    valor         NUMBER (10) NOT NULL ,
    numShares     NUMBER NOT NULL ,
    iid           NUMBER (7) NOT NULL ,
    data          DATE NOT NULL
  ) ;
ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_PK PRIMARY KEY
(
  transactionId
)
;

CREATE TABLE Utilizadores
  (
    userid          NUMBER (7) NOT NULL ,
    email           VARCHAR2 (50) NOT NULL ,
    username        VARCHAR2 (20) NOT NULL ,
    pass            VARCHAR2 (100) NOT NULL ,
    dinheiro        NUMBER (7) NOT NULL ,
    dataregisto     DATE NOT NULL ,
    dataUltimoLogin DATE
  ) ;
ALTER TABLE Utilizadores ADD CONSTRAINT Utilizadores_PK PRIMARY KEY
(
  userid
)
;

ALTER TABLE IdeiasFicheiros ADD CONSTRAINT IdeiasFicheiros_Ideias_FK FOREIGN KEY ( iid ) REFERENCES Ideias ( iid ) NOT DEFERRABLE ;

ALTER TABLE Shares ADD CONSTRAINT Shares_Ideias_FK FOREIGN KEY ( iid ) REFERENCES Ideias ( iid ) NOT DEFERRABLE ;

ALTER TABLE Shares ADD CONSTRAINT Shares_Utilizadores_FK FOREIGN KEY ( userid ) REFERENCES Utilizadores ( userid ) NOT DEFERRABLE ;

ALTER TABLE TopicosIdeias ADD CONSTRAINT TopicosIdeias_Ideias_FK FOREIGN KEY ( iid ) REFERENCES Ideias ( iid ) NOT DEFERRABLE ;

ALTER TABLE TopicosIdeias ADD CONSTRAINT TopicosIdeias_Topicos_FK FOREIGN KEY ( tid ) REFERENCES Topicos ( tid ) NOT DEFERRABLE ;

ALTER TABLE Topicos ADD CONSTRAINT Topicos_Topicos_FK FOREIGN KEY ( userid ) REFERENCES Utilizadores ( userid ) NOT DEFERRABLE ;

ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_Utilizadores_FK FOREIGN KEY ( comprador ) REFERENCES Utilizadores ( userid ) NOT DEFERRABLE ;

ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_Utilizadores_FKv2 FOREIGN KEY ( vendedor ) REFERENCES Utilizadores ( userid ) NOT DEFERRABLE ;

ALTER TABLE Ideias ADD CONSTRAINT userid FOREIGN KEY ( userid ) REFERENCES Utilizadores ( userid ) NOT DEFERRABLE ;


-- Oracle SQL Developer Data Modeler Summary Report: 
-- 
-- CREATE TABLE                             7
-- CREATE INDEX                             0
-- ALTER TABLE                             16
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
