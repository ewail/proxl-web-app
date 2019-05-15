

USE proxl ;


--  This will create an initial user in the "" database
--  The initial user will have user id "initial_proxl_user"
--  The initial user will have password "FJS483792nzmv,xc4#&@(!VMKSDL"

--  This is required to have an 'id' of 1.  There is hard coding in the app for this id of 1.

--  If no GUI for disabling accounts, disable account "initial_proxl_user" using
--    UPDATE user_tbl SET enabled_app_specific = 0 WHERE id = 1;

--  To enable account "initial_proxl_user"
--    UPDATE user_tbl SET enabled_app_specific = 1 WHERE id = 1;


INSERT INTO user_mgmt_user (id, username, email, first_name, last_name, organization, enabled, created_date, global_admin_user) 
VALUES (1, 'initial_proxl_user', 'NONE', 'INITIAL USER', '', '', 1, NOW(), 1 );

INSERT INTO password_mgmt_user_password (user_id, password_hashed_new)
 VALUES (1, '$2a$13$/W2Tt0gHIBPBl6H4VLSCJ.A5Pks/krR1PawCOexL14S.i1jfaCUbi');
 
