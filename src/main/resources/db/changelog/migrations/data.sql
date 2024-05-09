insert into netology_diploma.USERS (username, password, role)
values ('user1@mail.ru', '$2a$12$w4P39vCP1S/Z5xJE6Kyzp.ps82raI.Y2OdWAJrXVSN6cSTkf8U3hu', 'ROLE_ADMIN');
-- password "123"

insert into netology_diploma.USERS (username, password, role)
values ('user2@mail.ru', '$2a$12$w4P39vCP1S/Z5xJE6Kyzp.ps82raI.Y2OdWAJrXVSN6cSTkf8U3hu', 'ROLE_USER');
-- password "123"

insert into netology_diploma.FILES (filename, file_size, hash, file_contents, username_user)
values ('Тест1.txt', 68, '-846934447', '0KLQtdGB0YIg0LTQu9GPIDEg0L/QvtC70YzQt9C+0LLQsNGC0LXQu9GPCtCk0LDQudC7INC30LDQs9GA0YPQttC10L0=', 'user1@mail.ru');

insert into netology_diploma.FILES (filename, file_size, hash, file_contents, username_user)
values ('Тест2.txt', 68, '873112940', '0KLQtdGB0YIg0LTQu9GPIDIg0L/QvtC70YzQt9C+0LLQsNGC0LXQu9GPCtCk0LDQudC7INC30LDQs9GA0YPQttC10L0=', 'user2@mail.ru');