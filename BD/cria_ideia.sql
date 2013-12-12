DROP FUNCTION getCurrentIid;
DROP PROCEDURE Create_Idea;
DROP PROCEDURE Idea_Topic;

Create or Replace Function getCurrentIid RETURN NUMBER IS

  idea_id NUMBER := -1;
Begin
  Select idea_seq.currval INTO idea_id from dual;
  return idea_id;
END;

CREATE OR REPLACE PROCEDURE Create_Idea (titulo IN Varchar2, descricao IN Varchar2, user_id IN NUMBER, preco IN Number) IS  
BEGIN
	INSERT INTO Ideia Values (idea_seq.nextval,titulo,descricao,user_id,1,null,null,preco,null);
	INSERT INTO "Share" VALUES (idea_seq.currval,user_id,100000,preco);
  UPDATE Utilizador set dinheiro = dinheiro - (Select dinheiro From Utilizador where userid = user_id) where userid = user_id;  
END;

CREATE OR REPLACE PROCEDURE Idea_Topic (iid IN NUMBER, titulo IN VARCHAR2,user_id IN NUMBER) IS
  X NUMBER := 0;
BEGIN
  Select Count(*) INTO X From Topico where nome like titulo;
  IF X = 0 THEN
    INSERT INTO Topico VALUES (topic_seq.nextval,titulo,user_id);
  END IF;
  Insert into TopicoIdeia VALUES ( (Select tid From Topico where nome like titulo) ,iid);
END;

