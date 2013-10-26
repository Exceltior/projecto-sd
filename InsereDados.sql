/*Criacao dos Utilizadores*/
INSERT INTO Utilizadores VALUES (1,'hakuna@matata.com','Hakuna','91968f0580a96a8467addb44038a7154', 10000, to_date('2013.10.12', 'yyyy.mm.dd'), null);
INSERT INTO Utilizadores VALUES (2,'joca@joca.com', 'Joca', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);
INSERT INTO Utilizadores VALUES (3,'joca2@joca.com', 'Joca2', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);
INSERT INTO Utilizadores VALUES (4,'joca3@joca.com', 'Joca3', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);
INSERT INTO Utilizadores VALUES (5,'joca4@joca.com', 'Joca4', 'aa1bf4646de67fd9086cf6c79007026c', 10000, to_date('2013.10.14', 'yyyy.mm.dd'), null);

/*Criacao dos Topicos*/
INSERT INTO Topicos VALUES (1,'Tenis','Joao Sousa fecha top50 ATP!',1);
INSERT INTO Topicos VALUES (2,'Hello, Ladies','Hakuna Matata',1);
INSERT INTO Topicos VALUES (3,'Latada','Latada comecça na quarta-feira dia 17',1);

/*Criacao das Ideias:
Ideias 1,4 e 5 vao para o topico 1
Ideia 2 vai para o topico 2
Ideia 3 vai para o topico 3*/ 
INSERT INTO Ideias VALUES (1,'Joao Sousa', 'A descida do Joao Sousa no ranking ATP nao desvaloriza o trabalho que ele tem vindo a realizar este ano',1,1);

INSERT INTO Ideias VALUES (2,'Ladies', 'Hello Ladies, my name is Maxi... Super Maxi!',1,1);

INSERT INTO Ideias VALUES (3, 'Cortejo Latada', 'O Cortejo da Latada e um excelente evento',1,1);

INSERT INTO Ideias VALUES (4,'Atletas Portugueses', 'Se o Joao Sousa fosse bom ja tinha chamado a atencao no ranking ATP a muito tempo. A sua descida nao e nada mais do que um processo descendente normal na sua carreira',2,1);

INSERT INTO Ideias VALUES (5, 'Tenis Portugal', 'O Joao Sousa tem feito um trabalho notavel. O facto de ter conseguido entrar em todos os torneios do Grand Slam esta temporada mostra uma melhoria significativa e um excelente salto para a sua carreira tenistica',1,1);


/*Colocacao das ideias nos diferentes topicos*/
INSERT INTO TopicosIdeias VALUES (1,1);
INSERT INTO TopicosIdeias VALUES (1,4);
INSERT INTO TopicosIdeias VALUES (1,5);
INSERT INTO TopicosIdeias VALUES (2,2);
INSERT INTO TopicosIdeias VALUES (3,3);

/*Tratar da relacao entre as ideias:
Ideia 4 contraria a ideia 1
Ideia 5 contraria a ideia 4
Ideia 5 apoia a ideia 1*/
INSERT INTO RelacaoIdeias VALUES (1,4,-1);
INSERT INTO RelacaoIdeias VALUES (4,5,-1);
INSERT INTO RelacaoIdeias VALUES (1,5,1);

/*Inserir Shares*/
INSERT INTO Shares VALUES (1,1,50,2,10);/*Podes comprar aqui ate 40 shares*/
INSERT INTO Shares VALUES (1,5,25,2,2);/*Podes comprar aqui ate 23 shares*/
INSERT INTO Shares VALUES (1,3,20,2,1);/*Podes comprar aqui ate 19 shares*/
INSERT INTO Shares VALUES (1,4,5,2,0);/*Podes comprar aqui ate 5 shares*/
INSERT INTO Shares VALUES (2,1,100,3,50);
INSERT INTO Shares VALUES (3,2,100,4,50);
INSERT INTO Shares VALUES (4,1,100,6,50);
INSERT INTO Shares VALUES (5,1,50,1,25);
INSERT INTO Shares VALUES (5,2,50,1,25);