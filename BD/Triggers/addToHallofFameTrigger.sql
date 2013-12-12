DROP TRIGGER addToHallofFameTrigger;

CREATE OR REPLACE TRIGGER addToHallofFameTrigger BEFORE INSERT ON HallFame FOR EACH ROW
DECLARE
BEGIN
  UPDATE Ideia set activa = 0 where iid = :NEW.iid;
EXCEPTION
  WHEN OTHERS THEN
    rollback;
END;