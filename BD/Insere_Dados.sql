/*Criacao dos Utilizadores*/
INSERT INTO Utilizador VALUES (user_seq.nextval,'hakuna@matata.com','Hakuna','91968f0580a96a8467addb44038a7154', 10000, to_date('2013.10.12', 'yyyy.mm.dd'), null);
INSERT INTO Utilizador VALUES (user_seq.nextval,'joca@joca.com', 'Joca', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);
INSERT INTO Utilizador VALUES (user_seq.nextval,'joca2@joca.com', 'Joca2', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);
INSERT INTO Utilizador VALUES (user_seq.nextval,'joca3@joca.com', 'Joca3', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);
INSERT INTO Utilizador VALUES (user_seq.nextval,'joca4@joca.com', 'Joca4', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);

/*Criacao dos Topicos*/
INSERT INTO Topico VALUES (topic_seq.nextval,'Joao Sousa fecha top50 ATP!',1);
INSERT INTO Topico VALUES (topic_seq.nextval,'Topico de discussao de futebol! Enjoy!',1);
INSERT INTO Topico VALUES (topic_seq.nextval,'Latada comecça na quarta-feira dia 17',1);

/*Criacao das Ideias:*/
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Joao Sousa', 'A descida do Joao Sousa no ranking ATP nao desvaloriza o trabalho que ele tem vindo a realizar este ano',1,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Academica', 'A Academica vai revalidar o titulo da taça, que ganhou em 1939 e 2012',1,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Cortejo Latada', 'O Cortejo da Latada e um excelente evento',1,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Atletas Portugueses', 'Se o Joao Sousa fosse bom ja tinha chamado a atencao no ranking ATP a muito tempo. A sua descida nao e nada mais do que um processo descendente normal na sua carreira',2,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Tenis Portugal', 'O Joao Sousa tem feito um trabalho notavel. O facto de ter conseguido entrar em todos os torneios do Grand Slam esta temporada mostra uma melhoria significativa e um excelente salto para a sua carreira tenistica',1,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Futebol Clube do Porto', 'O Futebol Clube do Porto vai ser o próximo campeão nacional',1,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Benfica', 'O Benfica este ano tem uma equipa muito mais equilibrada e competitiva do que o Porto!',2,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Festa das Latas', 'O Cortejo da Festa das Latas este ano teve uma menor adesao do que o esperado devido a chuva',2,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Gastao Elias', 'O Gastao Elias e bem capaz de ter muito mais potencial do que o Frederico Gil ou o Joao Sousa. Poderia perfeitamente ser o melhor tenista Portugues da actualidade e de sempre',3,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Mario Jardel', 'Sempre gostei de ver jogadores como Mario Jardel a jogar a bola. Gostava que ele ainda jogasse, e de preferencia no Porto',2,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Super Mario', 'O Mario Jardel foi de facto um jogador estupendo, quer no Porto, quer no Sporting',1,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Taca de Portugal', 'A Taca de Portugal este ano vai ser entregue ao Benfica, que perdeu o ano passado para o Guimaraes',2,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Cartaz Latada','O Cartaz da Festa das Latas deste ano estava bem preenchido. Sempre com bons convidados em todos os dias',4,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Latada Coimbra', 'Eu por acaso discordo dessa opinao de que o cartaz da Latada estava bom. Nao gostei dos artistas convidados este ano',1,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Queima das Fitas', 'Eu regra geral frequento as noites do parque na Latada e na Queima, ms este ano nao tive oportunidade de aparecer no recinto da Latada. Espero ter mais disponibilidade para o fazer na Queima das Fitas',5,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Queima','User 5, se me permite uma questao: Porque e que nao teve disponibilidade? Muito trabalho na faculdade? Se essa for a razao, compreendo perfeitamente',3,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Resposta User 3','Sim, foi exactamente por ter trabalho na faculdade. Mas quanto a isso nao ha nada a fazer, temos que trabalhar e cara alegre :P',5,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Topico Tenis','Este topico anda muito vazio ultimamente....',1,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Topico Futebol','Gostava de ver aqui mais discussoes sobre os jogos de futebol estrangeiros e nacionais, quer da primeira divisao, quer da segunda divisao',2,1,null,null,null);
INSERT INTO Ideia VALUES (idea_seq.nextval, 'Sporting','Novamente o Sporting aposta em jovens e mostra ao Benfica e ao Porto por onde vai passar o futuro de Portugal',4,1,null,null,null);



/*Colocacao das ideias nos diferentes topicos*/
INSERT INTO TopicoIdeia VALUES (1,1);
INSERT INTO TopicoIdeia VALUES (1,4);
INSERT INTO TopicoIdeia VALUES (1,5);
INSERT INTO TopicoIdeia VALUES (2,2);
INSERT INTO TopicoIdeia VALUES (3,3);
INSERT INTO TopicoIdeia VALUES (2,6);
INSERT INTO TopicoIdeia VALUES (2,7);
INSERT INTO TopicoIdeia VALUES (3,8);
INSERT INTO TopicoIdeia VALUES (1,9);
INSERT INTO TopicoIdeia VALUES (2,10);
INSERT INTO TopicoIdeia VALUES (2,11);
INSERT INTO TopicoIdeia VALUES (2,12);
INSERT INTO TopicoIdeia VALUES (3,13);
INSERT INTO TopicoIdeia VALUES (3,14);
INSERT INTO TopicoIdeia VALUES (3,15);
INSERT INTO TopicoIdeia VALUES (3,16);
INSERT INTO TopicoIdeia VALUES (3,17);
INSERT INTO TopicoIdeia VALUES (1,18);
INSERT INTO TopicoIdeia VALUES (2,19);
INSERT INTO TopicoIdeia VALUES (2,20);

/*Inserir Shares*/
INSERT INTO Share VALUES (1,1,50,2);
INSERT INTO Share VALUES (1,5,25,2);
INSERT INTO Share VALUES (1,3,20,2);
INSERT INTO Share VALUES (1,4,20,2);
INSERT INTO Share VALUES (2,1,100,3);
INSERT INTO Share VALUES (3,2,100,40);
INSERT INTO Share VALUES (4,1,100,6);
INSERT INTO Share VALUES (5,1,50,10);
INSERT INTO Share VALUES (5,2,50,66);
INSERT INTO Share VALUES (6,1,50,77);
INSERT INTO Share VALUES (6,2,10,54);
INSERT INTO Share VALUES (7,2,50,45);
INSERT INTO Share VALUES (8,2,70,97);
INSERT INTO Share VALUES (9,3,50,23);
INSERT INTO Share VALUES (10,2,60,10);
INSERT INTO Share VALUES (11,1,10,100);
INSERT INTO Share VALUES (11,3,50,1);
INSERT INTO Share VALUES (12,2,23,6);
INSERT INTO Share VALUES (13,4,78,8);
INSERT INTO Share VALUES (14,1,100,6);
INSERT INTO Share VALUES (15,5,195,3);
INSERT INTO Share VALUES (16,3,80,4);
INSERT INTO Share VALUES (17,5,69,8);
INSERT INTO Share VALUES (18,1,43,5);
INSERT INTO Share VALUES (19,2,96,63);
INSERT INTO Share VALUES (19,4,102,65);
INSERT INTO Share VALUES (20,4,50,1);