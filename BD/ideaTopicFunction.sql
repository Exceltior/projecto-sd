DROP PROCEDURE Idea_Topic;

CREATE OR REPLACE PROCEDURE Idea_Topic (iid IN NUMBER, titulo IN VARCHAR2,user_id IN NUMBER) IS
  X NUMBER := 0;
BEGIN
  Select Count(*) INTO X From Topico where nome like titulo;
  IF X = 0 THEN
    INSERT INTO Topico VALUES (topic_seq.nextval,titulo,user_id);
  END IF;
  Insert into TopicoIdeia VALUES ( (Select tid From Topico where nome like titulo) ,iid);
END Idea_Topic;

