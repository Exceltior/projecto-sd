DROP FUNCTION associateIdeaToTopic;

CREATE OR REPLACE FUNCTION associateIdeaToTopic (iid IN NUMBER, titulo IN VARCHAR2,user_id IN NUMBER) RETURN NUMBER IS
  PRAGMA AUTONOMOUS_TRANSACTION;
  X NUMBER := 0;
BEGIN
  SELECT tid INTO X FROM Topico WHERE nome LIKE titulo;
  INSERT INTO TopicoIdeia VALUES(X,iid);
  COMMIT;
  return 1;
EXCEPTION
  WHEN no_data_found THEN
    INSERT INTO Topico VALUES (topic_seq.nextval,titulo,user_id);
    INSERT INTO TopicoIdeia VALUES ( (SELECT tid FROM Topico where nome LIKE titulo),iid);
    COMMIT;
    RETURN 1;
  WHEN OTHERS THEN
    ROLLBACK;
    RETURN -1;
END associateIdeaToTopic;
