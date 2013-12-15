CREATE OR REPLACE PROCEDURE setSharePrice (idea_id IN NUMBER, user_id IN NUMBER, price IN NUMBER, temp OUT NUMBER) IS
BEGIN
  temp := 1;
  Update "Share" SET valor = price where iid = idea_id and userid = user_id;
EXCEPTION
  WHEN OTHERS THEN
    rollback;
    temp := -1;
END;