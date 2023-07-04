insert into Users(id,
                 age,
                 blocked,
                 email,
                 first_name,
                 last_name,
                 password,
                 phone_number,
                  role)
values (1, 22, false, 'admin@gmail.com', 'admin', 'adminov', 'admin', '0770','ADMIN');

insert into user_roles(user_id,roles)
values (1,'USER'),(1,'ADMIN');