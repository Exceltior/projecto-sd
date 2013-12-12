/*Criacao dos Utilizadores*/
INSERT INTO Utilizador VALUES (user_seq.nextval,'hakuna@matata.com','Hakuna','91968f0580a96a8467addb44038a7154', 1000000, to_date('2013.10.12', 'yyyy.mm.dd'), null,1,'100003701544271');
INSERT INTO Utilizador VALUES (user_seq.nextval,'joca@joca.com', 'Joca', 'aa1bf4646de67fd9086cf6c79007026c', 1000000, to_date('2013.10.14', 'yyyy.mm.dd'), null,0,'100000015266864');
INSERT INTO Utilizador VALUES (user_seq.nextval,'root@ideabroker.com', 'root', '63a9f0ea7bb98050796b649e85481845', 1000000, to_date('2013.10.14', 'yyyy.mm.dd'), null,1,null);
INSERT INTO Utilizador VALUES (user_seq.nextval,'joca3@joca.com', 'Joca3', 'aa1bf4646de67fd9086cf6c79007026c', 1000000, to_date('2013.10.14', 'yyyy.mm.dd'), null,0,null);
INSERT INTO Utilizador VALUES (user_seq.nextval,'joca4@joca.com', 'Joca4', 'aa1bf4646de67fd9086cf6c79007026c', 1000000, to_date('2013.10.14', 'yyyy.mm.dd'), null,0,null);

/*Criacao dos Topicos*/
INSERT INTO Topico VALUES (topic_seq.nextval,'ATP!',1);
INSERT INTO Topico VALUES (topic_seq.nextval,'Futebol',1);
INSERT INTO Topico VALUES (topic_seq.nextval,'DEI',1);

/*Criacao das Ideias:*/
INSERT INTO Ideia VALUES (idea_seq.nextval, 'DEAL WITH IT', 'Hey! This is our project! This is our life! Come on Joao Sousa!!! Win for us!!!!',1,1,null,null,100,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Academica', 'A Academica vai revalidar o titulo da taça, que ganhou em 1939 e 2012',1,1,null,null,10,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'DEI SUGA ALMAS', 'Ah pois e, o DEI SUGA ALMAS e nao e pouco!!!! Ja sao 3h30 e ainda estamos nesta vida!!!',1,1,null,null,2,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Atletas Portugueses', 'Se o Joao Sousa fosse bom ja tinha chamado a atencao no ranking ATP a muito tempo. A sua descida nao e nada mais do que um processo descendente normal na sua carreira',2,1,null,null,5,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Tenis Portugal', 'O Joao Sousa tem feito um trabalho notavel. O facto de ter conseguido entrar em todos os torneios do Grand Slam esta temporada mostra uma melhoria significativa e um excelente salto para a sua carreira tenistica',1,1,null,null,4,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Futebol Clube do Porto', 'O Futebol Clube do Porto vai ser o próximo campeão nacional',1,1,null,null,90,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Benfica', 'O Benfica este ano tem uma equipa muito mais equilibrada e competitiva do que o Porto!',2,1,null,null,50,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Festa das Latas', 'O Cortejo da Festa das Latas este ano teve uma menor adesao do que o esperado devido a chuva',2,1,null,null,11,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Gastao Elias', 'O Gastao Elias e bem capaz de ter muito mais potencial do que o Frederico Gil ou o Joao Sousa. Poderia perfeitamente ser o melhor tenista Portugues da actualidade e de sempre',3,1,null,null,13,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Mario Jardel', 'Sempre gostei de ver jogadores como Mario Jardel a jogar a bola. Gostava que ele ainda jogasse, e de preferencia no Porto',2,1,null,null,16,null);


/*Colocacao das ideias nos diferentes topicos*/
INSERT INTO TopicoIdeia VALUES (3,1);
INSERT INTO TopicoIdeia VALUES (2,2);
INSERT INTO TopicoIdeia VALUES (3,3);
INSERT INTO TopicoIdeia VALUES (1,4);
INSERT INTO TopicoIdeia VALUES (1,5);
INSERT INTO TopicoIdeia VALUES (2,6);
INSERT INTO TopicoIdeia VALUES (2,7);
INSERT INTO TopicoIdeia VALUES (3,8);
INSERT INTO TopicoIdeia VALUES (1,9);
INSERT INTO TopicoIdeia VALUES (2,10);

/*Inserir Shares*/
INSERT INTO "Share" VALUES (1,1,50000,4);
INSERT INTO "Share" VALUES (1,2,25000,1);
INSERT INTO "Share" VALUES (1,3,25000,2);
INSERT INTO "Share" VALUES (2,1,80000,6);
INSERT INTO "Share" VALUES (2,3,20000,5);
INSERT INTO "Share" VALUES (3,1,50000,7);
INSERT INTO "Share" VALUES (3,2,40000,7);
INSERT INTO "Share" VALUES (3,3,10000,7);
INSERT INTO "Share" VALUES (4,1,100000,6);
INSERT INTO "Share" VALUES (5,1,50000,1);
INSERT INTO "Share" VALUES (5,2,50000,6);
INSERT INTO "Share" VALUES (6,1,55000,2);
INSERT INTO "Share" VALUES (6,2,45000,5);
INSERT INTO "Share" VALUES (7,2,85000,4);
INSERT INTO "Share" VALUES (7,4,15000,3);
INSERT INTO "Share" VALUES (8,1,100,9);
INSERT INTO "Share" VALUES (8,3,49800,3);
INSERT INTO "Share" VALUES (8,4,100,4);
INSERT INTO "Share" VALUES (8,5,50000,2);
INSERT INTO "Share" VALUES (9,1,10000,2);
INSERT INTO "Share" VALUES (9,4,10000,2);
INSERT INTO "Share" VALUES (9,5,80000,2);
INSERT INTO "Share" VALUES (10,2,50000,1);
INSERT INTO "Share" VALUES (10,2,50000,1);

/*Inserir ideias na watchlist*/
INSERT INTO IdeiaWatchList VALUES (1,2);
INSERT INTO IdeiaWatchList VALUES (1,4);
INSERT INTO IdeiaWatchList VALUES (2,5);
INSERT INTO IdeiaWatchList VALUES (2,10);
INSERT INTO IdeiaWatchList VALUES (3,6);
INSERT INTO IdeiaWatchList VALUES (3,7);
INSERT INTO IdeiaWatchList VALUES (4,1);
INSERT INTO IdeiaWatchList VALUES (4,8);
INSERT INTO IdeiaWatchList VALUES (5,3);
INSERT INTO IdeiaWatchList VALUES (5,9);

/*Inserir Transações*/
INSERT INTO Transacao VALUES (transaction_seq.nextval,1,2,100,5,1,to_date('2013.10.24','yyyy.mm.dd'));