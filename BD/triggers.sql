Drop Trigger removeIdeia;

CREATE TRIGGER removeIdeia Before UPDATE OF activa ON Ideia FOR EACH ROW WHEN ( new.activa = 0)
DECLARE
BEGIN  
  DELETE FROM "Share" where iid = :NEW.iid;
  DELETE FROM TopicoIdeia where iid = :NEW.iid;
  :NEW.originalfile := null;
  :NEW.path := null;
END;