/*Drop Sequences*/
DROP SEQUENCE user_seq;
DROP SEQUENCE topic_seq;
DROP SEQUENCE idea_seq;
DROP SEQUENCE transaction_seq;
DROP SEQUENCE fila_seq;
DROP SEQUENCE fame_seq;

/*Drop Triggers*/
DROP TRIGGER setShares;
DROP TRIGGER removeIdeia;
DROP TRIGGER addToHallofFame;

/*Drop Tables*/
drop table Transacao;
drop table TopicoIdeia;
drop table "Share";
drop table IdeiaWatchList;
drop table Compra;
drop table HallFame;
drop table Ideia;
drop table Topico;
drop table Utilizador;

/*Drop Procedures*/
DROP PROCEDURE setSharePrice;
DROP PROCEDURE removeFromWatchList;
DROP PROCEDURE addToWatchList;

/*Drop Functions*/
DROP FUNCTION createIdea;
DROP FUNCTION associateIdeaToTopic;