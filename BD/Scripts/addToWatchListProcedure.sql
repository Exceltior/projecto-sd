CREATE OR REPLACE PROCEDURE addToWatchList (idea_id IN NUMBER, user_id IN NUMBER, temp OUT NUMBER) IS
BEGIN
  temp := -1;
  INSERT INTO IdeiaWatchList VALUES (user_id,idea_id);
  temp := 1;
EXCEPTION
  WHEN OTHERS THEN
    temp := -1;
END;
