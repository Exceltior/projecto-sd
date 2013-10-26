/*Criacao dos Utilizadores*/
INSERT INTO Utilizadores VALUES (1,'hakuna@matata.com','Hakuna','91968f0580a96a8467addb44038a7154', 10000, to_date('2013.10.12', 'yyyy.mm.dd'), null);
INSERT INTO Utilizadores VALUES (2,'joca@joca.com', 'Joca', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);
INSERT INTO Utilizadores VALUES (3,'joca2@joca.com', 'Joca2', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);
INSERT INTO Utilizadores VALUES (4,'joca3@joca.com', 'Joca3', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);
INSERT INTO Utilizadores VALUES (5,'joca4@joca.com', 'Joca4', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);

/*Criacao dos Topicos*/
INSERT INTO Topicos VALUES (1,'Tenis','Joao Sousa fecha top50 ATP!',1);
INSERT INTO Topicos VALUES (2,'Futebol','Topico de discussao de futebol! Enjoy!',1);
INSERT INTO Topicos VALUES (3,'Latada','Latada comecça na quarta-feira dia 17',1);

/*Criacao das Ideias:*/
INSERT INTO Ideias VALUES (1, 'Joao Sousa', 'A descida do Joao Sousa no ranking ATP nao desvaloriza o trabalho que ele tem vindo a realizar este ano',1,1);
INSERT INTO Ideias VALUES (2, 'Academica', 'A Academica vai revalidar o titulo da taça, que ganhou em 1939 e 2012',1,1);
INSERT INTO Ideias VALUES (3, 'Cortejo Latada', 'O Cortejo da Latada e um excelente evento',1,1);
INSERT INTO Ideias VALUES (4, 'Atletas Portugueses', 'Se o Joao Sousa fosse bom ja tinha chamado a atencao no ranking ATP a muito tempo. A sua descida nao e nada mais do que um processo descendente normal na sua carreira',2,1);
INSERT INTO Ideias VALUES (5, 'Tenis Portugal', 'O Joao Sousa tem feito um trabalho notavel. O facto de ter conseguido entrar em todos os torneios do Grand Slam esta temporada mostra uma melhoria significativa e um excelente salto para a sua carreira tenistica',1,1);
INSERT INTO Ideias VALUES (6, 'Futebol Clube do Porto', 'O Futebol Clube do Porto vai ser o próximo campeão nacional',1,1);
INSERT INTO Ideias VALUES (7, 'Benfica', 'O Benfica este ano tem uma equipa muito mais equilibrada e competitiva do que o Porto!',2,1);
INSERT INTO Ideias VALUES (8, 'Festa das Latas', 'O Cortejo da Festa das Latas este ano teve uma menor adesao do que o esperado devido a chuva',2,1);
INSERT INTO Ideias VALUES (9, 'Gastao Elias', 'O Gastao Elias e bem capaz de ter muito mais potencial do que o Frederico Gil ou o Joao Sousa. Poderia perfeitamente ser o melhor tenista Portugues da actualidade e de sempre',3,1);
INSERT INTO Ideias VALUES (10, 'Mario Jardel', 'Sempre gostei de ver jogadores como Mario Jardel a jogar a bola. Gostava que ele ainda jogasse, e de preferencia no Porto',2,1);
INSERT INTO Ideias VALUES (11, 'Super Mario', 'O Mario Jardel foi de facto um jogador estupendo, quer no Porto, quer no Sporting',1,1)
INSERT INTO Ideias VALUES (12, 'Taca de Portugal', 'A Taca de Portugal este ano vai ser entregue ao Benfica, que perdeu o ano passado para o Guimaraes',2,1);
INSERT INTO Ideias VALUES (13, 'Cartaz Latada','O Cartaz da Festa das Latas deste ano estava bem preenchido. Sempre com bons convidados em todos os dias',4,1);
INSERT INTO Ideias VALUES (14, 'Latada Coimbra', 'Eu por acaso discordo dessa opinao de que o cartaz da Latada estava bom. Nao gostei dos artistas convidados este ano',1,1);
INSERT INTO Ideias VALUES (15, 'Queima das Fitas', 'Eu regra geral frequento as noites do parque na Latada e na Queima, ms este ano nao tive oportunidade de aparecer no recinto da Latada. Espero ter mais disponibilidade para o fazer na Queima das Fitas',5,1);
INSERT INTO Ideias VALUES (16, 'Queima','User 5, se me permite uma questao: Porque e que nao teve disponibilidade? Muito trabalho na faculdade? Se essa for a razao, compreendo perfeitamente',3,1);
INSERT INTO Ideias VALUES (17, 'Resposta User 3','Sim, foi exactamente por ter trabalho na faculdade. Mas quanto a isso nao ha nada a fazer, temos que trabalhar e cara alegre :P',5,1);
INSERT INTO Ideias VALUES (18, 'Topico Tenis','Este topico anda muito vazio ultimamente....',1,1);
INSERT INTO Ideias VALUES (19, 'Topico Futebol','Gostava de ver aqui mais discussoes sobre os jogos de futebol estrangeiros e nacionais, quer da primeira divisao, quer da segunda divisao',2,1);
INSERT INTO Ideias VALUES (20, 'Sporting','Novamente o Sporting aposta em jovens e mostra ao Benfica e ao Porto por onde vai passar o futuro de Portugal',4,1);



/*Colocacao das ideias nos diferentes topicos*/
INSERT INTO TopicosIdeias VALUES (1,1);
INSERT INTO TopicosIdeias VALUES (1,4);
INSERT INTO TopicosIdeias VALUES (1,5);
INSERT INTO TopicosIdeias VALUES (2,2);
INSERT INTO TopicosIdeias VALUES (3,3);
INSERT INTO TopicosIdeias VALUES (2,6);
INSERT INTO TopicosIdeias VALUES (2,7);
INSERT INTO TopicosIdeias VALUES (3,8);
INSERT INTO TopicosIdeias VALUES (1,9);
INSERT INTO TopicosIdeias VALUES (2,10);
INSERT INTO TopicosIdeias VALUES (2,11);
INSERT INTO TopicosIdeias VALUES (2,12);
INSERT INTO TopicosIdeias VALUES (3,13);
INSERT INTO TopicosIdeias VALUES (3,14);
INSERT INTO TopicosIdeias VALUES (3,15);
INSERT INTO TopicosIdeias VALUES (3,16);
INSERT INTO TopicosIdeias VALUES (3,17);
INSERT INTO TopicosIdeias VALUES (1,18);
INSERT INTO TopicosIdeias VALUES (2,19);
INSERT INTO TopicosIdeias VALUES (2,20);

/*Tratar da relacao entre as ideias:*/
INSERT INTO RelacaoIdeias VALUES (1,4,-1);
INSERT INTO RelacaoIdeias VALUES (4,5,-1);
INSERT INTO RelacaoIdeias VALUES (1,5,1);
INSERT INTO RelacaoIdeias VALUES (12,2,-1);
INSERT INTO RelacaoIdeias VALUES (14,13,-1);
INSERT INTO RelacaoIdeias VALUES (16,15,1);
INSERT INTO RelacaoIdeias VALUES (17,16,1);
INSERT INTO RelacaoIdeias VALUES (20,6,-1);
INSERT INTO RelacaoIdeias VALUES (20,7,-1);
INSERT INTO RelacaoIdeias VALUES (9,1,0);
INSERT INTO RelacaoIdeias VALUES (9,4,1);
INSERT INTO RelacaoIdeias VALUES (9,18,-1);

/*Inserir Shares*/
INSERT INTO Shares VALUES (1,1,50,2,10);
INSERT INTO Shares VALUES (1,5,25,2,2);
INSERT INTO Shares VALUES (1,3,20,2,1);
INSERT INTO Shares VALUES (1,4,20,2,5);
INSERT INTO Shares VALUES (2,1,100,3,50);
INSERT INTO Shares VALUES (3,2,100,40,50);
INSERT INTO Shares VALUES (4,1,100,6,50);
INSERT INTO Shares VALUES (5,1,50,10,25);
INSERT INTO Shares VALUES (5,2,50,66,25);
INSERT INTO Shares VALUES (6,1,50,77,20);
INSERT INTO Shares VALUES (6,2,10,54,0);
INSERT INTO Shares VALUES (7,2,50,45,10);
INSERT INTO Shares VALUES (8,2,70,97,40);
INSERT INTO Shares VALUES (9,3,50,23,25);
INSERT INTO Shares VALUES (10,2,60,10,45);
INSERT INTO Shares VALUES (11,1,10,100,3);
INSERT INTO Shares VALUES (11,3,50,1,10);
INSERT INTO Shares VALUES (12,2,23,6,14);
INSERT INTO Shares VALUES (13,4,78,8,10);
INSERT INTO Shares VALUES (14,1,100,6,5);
INSERT INTO Shares VALUES (15,5,195,3,78);
INSERT INTO Shares VALUES (16,3,80,4,65);
INSERT INTO Shares VALUES (17,5,69,8,1);
INSERT INTO Shares VALUES (18,1,43,5,78);
INSERT INTO Shares VALUES (19,2,96,63,36);
INSERT INTO Shares VALUES (19,4,102,65,52);
INSERT INTO Shares VALUES (20,4,50,1,25);