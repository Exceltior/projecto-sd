DROP FUNCTION associateIdeaToTopic;

CREATE OR REPLACE FUNCTION associateIdeaToTopic (iid IN NUMBER, titulo IN VARCHAR2,user_id IN NUMBER) RETURN NUMBER IS
  X NUMBER := 0;
BEGIN
  Select Count(*) INTO X From Topico where nome like titulo;
  IF X = 0 THEN
    INSERT INTO Topico VALUES (topic_seq.nextval,titulo,user_id);
  END IF;
  Insert into TopicoIdeia VALUES ( (Select tid From Topico where nome like titulo) ,iid);
  return 1;
EXCEPTION
  WHEN OTHERS THEN
    rollback;
    return -1;
END associateIdeaToTopic;

