CREATE OR REPLACE PROCEDURE removeFromWatchList (idea_id IN NUMBER, user_id IN NUMBER, temp OUT NUMBER) IS
BEGIN
  temp := -1;
  DELETE FROM IdeiaWatchList WHERE userid = user_id and iid = idea_id;
  temp := 1;
EXCEPTION
  WHEN OTHERS THEN
    temp := -1;
END;