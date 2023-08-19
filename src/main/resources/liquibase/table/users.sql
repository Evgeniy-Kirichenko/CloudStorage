create table users
(
    id       bigint auto_increment
        primary key,
    password varchar(255) null,
    username varchar(30)  null,
    constraint UK_r43af9ap4edm43mmtq01oddj6
        unique (username)
);