DROP FUNCTION createIdea;

CREATE OR REPLACE FUNCTION createIdea (titulo IN Varchar2, descricao IN Varchar2, user_id IN NUMBER, preco IN Number, invested IN NUMBER) RETURN NUMBER IS  
PRAGMA AUTONOMOUS_TRANSACTION;
temp NUMBER := -1;
BEGIN
  temp := -1;
  INSERT INTO Ideia Values (idea_seq.nextval,titulo,descricao,user_id,1,null,null,preco,null);
  INSERT INTO "Share" VALUES (idea_seq.currval,user_id,100000,preco);
  UPDATE Utilizador set dinheiro = dinheiro - invested where userid = user_id;  
  SELECT idea_seq.currval INTO temp FROM dual;
  COMMIT;
  return temp;
EXCEPTION
  WHEN OTHERS THEN
    rollback;
    return -1;
END createIdea;