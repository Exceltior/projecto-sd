DROP FUNCTION getCurrentIid;

Create or Replace Function getCurrentIid RETURN NUMBER IS

  idea_id NUMBER := 1;
Begin
  Select idea_seq.currval INTO idea_id from dual;
  return idea_id;
EXCEPTION
  WHEN OTHERS THEN
    rollback;
    return -1;  
END;