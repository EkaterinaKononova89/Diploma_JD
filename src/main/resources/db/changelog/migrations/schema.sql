create table netology_diploma.USERS
(
    username varchar(50) unique not null primary key,
    -- unique и not null содержатся в primary key, но их явное указание не является избыточным
    password varchar(70)        not null,
    role     varchar            not null
);

create table netology_diploma.FILES
(
    id            bigserial primary key,
    filename      varchar        not null,
    -- unique не проходит для одинаковых файлов разных пользователей
    file_size     int,
    hash          varchar unique not null,
    file_contents varchar(10000000),
    username_user varchar,
    foreign key (username_user) references netology_diploma.USERS (username)
);