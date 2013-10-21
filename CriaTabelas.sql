-- Generated by Oracle SQL Developer Data Modeler 3.3.0.747
--   at:        2013-10-21 15:14:19 WEST
--   site:      Oracle Database 11g
--   type:      Oracle Database 11g




CREATE TABLE Ideias
  (
    iid       NUMBER (7) NOT NULL ,
    titulo    VARCHAR2 (80) ,
    descricao VARCHAR2 (1000) ,
    userid    NUMBER (7) NOT NULL ,
    activa    NUMBER (1)
  ) ;
ALTER TABLE Ideias ADD CONSTRAINT Ideias_PK PRIMARY KEY
(
  iid
)
;

CREATE TABLE IdeiasFicheiros
  (
    iid  NUMBER (7) NOT NULL ,
    path VARCHAR2 (70) NOT NULL
  ) ;
ALTER TABLE IdeiasFicheiros ADD CONSTRAINT IdeiasFicheiros_PK PRIMARY KEY
(
  iid, path
)
;

CREATE TABLE RelacaoIdeias
  (
    iidpai       NUMBER (7) NOT NULL ,
    iidfilho     NUMBER (7) NOT NULL ,
    tipo_relacao NUMBER (1)
  ) ;
ALTER TABLE RelacaoIdeias ADD CONSTRAINT RelacaoIdeias_PK PRIMARY KEY
(
  iidpai, iidfilho
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
    nome      VARCHAR2 (20) ,
    descricao VARCHAR2 (150) ,
    userid    NUMBER (7) NOT NULL
  ) ;
-- Error - Index Topicos_Topicos_FK has no columns
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
    comprador NUMBER (7) NOT NULL ,
    vendedor  NUMBER (7) NOT NULL ,
    valor     NUMBER (10) NOT NULL ,
    numShares NUMBER NOT NULL ,
    iid       NUMBER (7) NOT NULL ,
    data      DATE NOT NULL
  ) ;
ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_PK PRIMARY KEY
(
  comprador, vendedor, valor, numShares, iid, data
)
;

CREATE TABLE Utilizadores
  (
    userid          NUMBER (7) NOT NULL ,
    email           VARCHAR2 (50) ,
    username        VARCHAR2 (20) ,
    pass            VARCHAR2 (20) ,
    dinheiro        NUMBER (7) ,
    dataregisto     DATE ,
    dataUltimoLogin DATE
  ) ;
ALTER TABLE Utilizadores ADD CONSTRAINT Utilizadores_PK PRIMARY KEY
(
  userid
)
;

ALTER TABLE IdeiasFicheiros ADD CONSTRAINT IdeiasFicheiros_Ideias_FK FOREIGN KEY ( iid ) REFERENCES Ideias ( iid ) ;

ALTER TABLE RelacaoIdeias ADD CONSTRAINT RelacaoIdeias_Ideias_FK FOREIGN KEY ( iidpai ) REFERENCES Ideias ( iid ) ;

ALTER TABLE RelacaoIdeias ADD CONSTRAINT RelacaoIdeias_Ideias_FKv2 FOREIGN KEY ( iidfilho ) REFERENCES Ideias ( iid ) ;

ALTER TABLE Shares ADD CONSTRAINT Shares_Ideias_FK FOREIGN KEY ( iid ) REFERENCES Ideias ( iid ) ;

ALTER TABLE Shares ADD CONSTRAINT Shares_Utilizadores_FK FOREIGN KEY ( userid ) REFERENCES Utilizadores ( userid ) ;

ALTER TABLE TopicosIdeias ADD CONSTRAINT TopicosIdeias_Ideias_FK FOREIGN KEY ( iid ) REFERENCES Ideias ( iid ) ;

ALTER TABLE TopicosIdeias ADD CONSTRAINT TopicosIdeias_Topicos_FK FOREIGN KEY ( tid ) REFERENCES Topicos ( tid ) ;

ALTER TABLE Topicos ADD CONSTRAINT Topicos_Topicos_FK FOREIGN KEY ( userid ) REFERENCES Utilizadores ( userid ) ;

ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_Ideias_FK FOREIGN KEY ( iid ) REFERENCES Ideias ( iid ) ;

ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_Utilizadores_FK FOREIGN KEY ( comprador ) REFERENCES Utilizadores ( userid ) ;

ALTER TABLE Transacoes ADD CONSTRAINT Transacoes_Utilizadores_FKv2 FOREIGN KEY ( vendedor ) REFERENCES Utilizadores ( userid ) ;

ALTER TABLE Ideias ADD CONSTRAINT userid FOREIGN KEY ( userid ) REFERENCES Utilizadores ( userid ) ;


-- Oracle SQL Developer Data Modeler Summary Report: 
-- 
-- CREATE TABLE                             8
-- CREATE INDEX                             0
-- ALTER TABLE                             20
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
-- ERRORS                                   1
-- WARNINGS                                 0
