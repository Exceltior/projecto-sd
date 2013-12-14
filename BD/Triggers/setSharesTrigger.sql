DROP TRIGGER setShares;

CREATE OR REPLACE TRIGGER setShares AFTER UPDATE OF numshares ON "Share" FOR EACH ROW WHEN ( new.numshares = 0)
DECLARE
BEGIN
  DELETE FROM "Share" WHERE iid = :OLD.iid and userid = :OLD.userid;
END;