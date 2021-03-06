/*Create Sequences*/
CREATE SEQUENCE user_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE topic_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE idea_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE transaction_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE fila_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE fame_seq INCREMENT BY 1 START WITH 1;

/*Create Tables*/
CREATE TABLE Compra
  (
    compra_id        NUMBER (10) NOT NULL ,
    userid           NUMBER (7) NOT NULL ,
    iid              NUMBER (7) NOT NULL ,
    num              NUMBER (6) NOT NULL ,
    maxpricepershare NUMBER (14,4) NOT NULL ,
    targetSellPrice  NUMBER (14,4) NOT NULL
  ) ;
ALTER TABLE Compra ADD CONSTRAINT Compra_PK PRIMARY KEY
(
  compra_id
)
;

CREATE TABLE HallFame
  ( hfid NUMBER (7) NOT NULL , iid NUMBER (7) NOT NULL
  ) ;
ALTER TABLE HallFame ADD CONSTRAINT HallFame_PK PRIMARY KEY
(
  hfid
)
;

CREATE TABLE Ideia
  (
    iid             NUMBER (7) NOT NULL ,
    titulo          VARCHAR2 (80) NOT NULL ,
    descricao       VARCHAR2 (1000) NOT NULL ,
    userid          NUMBER (7) NOT NULL ,
    activa          NUMBER (1) NOT NULL ,
    path            VARCHAR2 (70) ,
    originalfile    VARCHAR2 (100) ,
    ultimatransacao NUMBER (14,4) ,
    facebook_id     VARCHAR2 (100)
  ) ;
ALTER TABLE Ideia ADD CONSTRAINT Ideia_PK PRIMARY KEY
(
  iid
)
;

CREATE TABLE IdeiaWatchList
  (
    userid NUMBER (7) NOT NULL ,
    iid    NUMBER (7) NOT NULL
  ) ;
ALTER TABLE IdeiaWatchList ADD CONSTRAINT IdeiaWatchList_PK PRIMARY KEY
(
  userid, iid
)
;

CREATE TABLE "Share"
  (
    iid       NUMBER (7) NOT NULL ,
    userid    NUMBER (7) NOT NULL ,
    numshares NUMBER (6) NOT NULL ,
    valor     NUMBER (14,4) NOT NULL
  ) ;
ALTER TABLE "Share" ADD CONSTRAINT Share_PK PRIMARY KEY
(
  iid, userid, valor, numshares
)
;

CREATE TABLE Topico
  (
    tid    NUMBER (7) NOT NULL ,
    nome   VARCHAR2 (100) NOT NULL ,
    userid NUMBER (7) NOT NULL
  ) ;
ALTER TABLE Topico ADD CONSTRAINT Topico_PK PRIMARY KEY
(
  tid
)
;

CREATE TABLE TopicoIdeia
  (
    tid NUMBER (7) NOT NULL ,
    iid NUMBER (7) NOT NULL
  ) ;
ALTER TABLE TopicoIdeia ADD CONSTRAINT TopicoIdeia_PK PRIMARY KEY
(
  tid, iid
)
;

CREATE TABLE Transacao
  (
    transactionid NUMBER (7) NOT NULL ,
    comprador     NUMBER (7) NOT NULL ,
    vendedor      NUMBER (7) NOT NULL ,
    valor         NUMBER (14,4) NOT NULL ,
    numshares     NUMBER (6) NOT NULL ,
    iid           NUMBER (7) NOT NULL ,
    data          DATE NOT NULL
  ) ;
ALTER TABLE Transacao ADD CONSTRAINT Transacao_PK PRIMARY KEY
(
  transactionid
)
;

CREATE TABLE Utilizador
  (
    userid          NUMBER (7) NOT NULL ,
    email           VARCHAR2 (50) ,
    username        VARCHAR2 (50) ,
    pass            VARCHAR2 (100) ,
    dinheiro        NUMBER (14,4) NOT NULL ,
    dataregisto     DATE NOT NULL ,
    dataultimologin DATE ,
    funcao          NUMBER (1) NOT NULL ,
    id_facebook     VARCHAR2 (100)
  ) ;
ALTER TABLE Utilizador ADD CONSTRAINT Utilizador_PK PRIMARY KEY
(
  userid
)
;

ALTER TABLE Compra ADD CONSTRAINT Compra_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE Compra ADD CONSTRAINT Compra_Utilizador_FK FOREIGN KEY ( userid ) REFERENCES Utilizador ( userid ) ;

ALTER TABLE IdeiaWatchList ADD CONSTRAINT IdeiaWatchList_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE IdeiaWatchList ADD CONSTRAINT IdeiaWatchList_Utilizador_FK FOREIGN KEY ( userid ) REFERENCES Utilizador ( userid ) ;

ALTER TABLE "Share" ADD CONSTRAINT Share_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE "Share" ADD CONSTRAINT Share_Utilizador_FK FOREIGN KEY ( userid ) REFERENCES Utilizador ( userid ) ;

ALTER TABLE HallFame ADD CONSTRAINT TABLE_10_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE Topico ADD CONSTRAINT TABLE_12_Utilizador_FK FOREIGN KEY ( userid ) REFERENCES Utilizador ( userid ) ;

ALTER TABLE TopicoIdeia ADD CONSTRAINT TopicoIdeia_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE TopicoIdeia ADD CONSTRAINT TopicoIdeia_Topico_FK FOREIGN KEY ( tid ) REFERENCES Topico ( tid ) ;

ALTER TABLE Transacao ADD CONSTRAINT Transacao_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE Transacao ADD CONSTRAINT Transacao_Utilizador_FK FOREIGN KEY ( comprador ) REFERENCES Utilizador ( userid ) ;

ALTER TABLE Transacao ADD CONSTRAINT Transacao_Utilizador_FKv2 FOREIGN KEY ( vendedor ) REFERENCES Utilizador ( userid ) ;
