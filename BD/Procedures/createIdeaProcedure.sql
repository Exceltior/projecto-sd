DROP PROCEDURE Create_Idea;

CREATE OR REPLACE PROCEDURE Create_Idea (titulo IN Varchar2, descricao IN Varchar2, user_id IN NUMBER, preco IN Number, temp OUT NUMBER) IS  
BEGIN
  temp := -1;
	INSERT INTO Ideia Values (idea_seq.nextval,titulo,descricao,user_id,1,null,null,preco,null);
	INSERT INTO "Share" VALUES (idea_seq.currval,user_id,100000,preco);
  UPDATE Utilizador set dinheiro = dinheiro - (Select dinheiro From Utilizador where userid = user_id) where userid = user_id;  
  temp := 1;  
EXCEPTION
  WHEN OTHERS THEN
    rollback;
    temp := -1;
END Create_Idea;