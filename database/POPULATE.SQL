delete from user_roles;
delete from employees;
delete from roles;
delete from users;

/* 
 * ID, EMAIL, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATED_AT, PERSONAL_EMAIL
 * password is 123 
 */

insert into users values (1, 'marisa.aleixo@academiapolarising.onmicrosoft.com', 'Marisa', 'Aleixo', '$2a$10$8fwlDX.CcKedUMk4D3RbA.an8e6uh8YnR8UaxHCvWvsgsJQUOfoaK', 'marisa.aleixo', curdate(), 'marisa.personal@gmail.com', '0');
insert into users values (2, 'eliana.casaca@academiapolarising.onmicrosoft.com', 'Eliana', 'Casaca', '$2a$10$8fwlDX.CcKedUMk4D3RbA.an8e6uh8YnR8UaxHCvWvsgsJQUOfoaK', 'eliana.casaca', curdate(), 'eliana.personal@gmail.com', '0');

/* 
 * ID, ROLE_NAME 
 */
insert into roles values (1, 'ROLE_ADMIN');
insert into roles values (2, 'ROLE_USER');

/* 
 * USER_ID, RPï¿½E_ID 
 */
insert into user_roles values (1, 1);
insert into user_roles values (2, 2);

/* 
 * ID, EMAIL, FIRST_NAME, LAST_NAME, USERNAME, INTRANET, SLACK, OFFICE_365, OFFICE_VIBE, CREATED_AT, PERSONAL_EMAIL, STARTED_AT
 * password is 123
 */

insert into employees values (1, 'marisa.aleixo@academiapolarising.onmicrosoft.com', 'Marisa', 'Aleixo', 'marisa.aleixo', 1, 1, 1, curdate(), 'marisa.personal@gmail.com', '2019/07/08', 'ACTIVE');
insert into employees values (2, 'eliana.casaca@academiapolarising.onmicrosoft.com', 'Eliana', 'Casaca', 'eliana.casaca', 1, 1, 1, curdate(), 'eliana.personal@gmail.com', '2019/07/08', 'ACTIVE');
insert into employees values (3, 'goncalo.valentim@academiapolarising.onmicrosoft.com', 'Gonçalo', 'Valentim', 'goncalo.valentim', 1, 1, 1, curdate(), 'goncalo.personal@gmail.com', '2019/07/08', 'ACTIVE');
insert into employees values (4, 'filipe.amaral@academiapolarising.onmicrosoft.com', 'Filipe', 'Amaral', 'filipe.amaral', 1, 1, 1, curdate(), 'filipe.personal@gmail.com', '2019/07/08', 'ACTIVE');
insert into employees values (5, 'bernardo.nunes@academiapolarising.onmicrosoft.com', 'Bernardo', 'Nunes', 'bernardo.nunes', 0, 0, 0, curdate(), 'bernardo.personal@gmail.com', '2019/07/08', 'ACTIVE');
insert into employees values (6, 'rui.santos@academiapolarising.onmicrosoft.com', 'Rui', 'Santos', 'rui.santos', 0, 0, 0, curdate(), 'rui.personal@gmail.com', '2019/07/08', 'INACTIVE');
insert into employees values (7, 'andre.mendonca@academiapolarising.onmicrosoft.com', 'André', 'Mendonça', 'andre.mendonca', 0, 0, 0, curdate(), 'andre.personal@gmail.com', '2019/07/08', 'INACTIVE');

INSERT INTO intranet.employees (first_name,last_name,username,email,start_date) VALUES 
('Marisa','Aleixo','marisa.aleixo','marisa.personal@gmail.com','2019-07-08')
,('Eliana','Casaca','eliana.casaca','eliana.personal@gmail.com','2019-07-08')
,('Gonçalo','Valentim','goncalo.valentim','goncalo.personal@gmail.com','2019-07-08')
,('Filipe','Amaral','filipe.amaral','filipe.personal@gmail.com','2019-07-08')
;
