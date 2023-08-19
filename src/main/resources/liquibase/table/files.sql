create table files
(
    id        bigint auto_increment
        primary key,
    bytes     longblob     not null,
    file_name varchar(255) null,
    file_type varchar(255) not null,
    created   datetime(6)  not null,
    size      bigint       not null,
    owner     varchar(255) null,
    constraint FK7smd1t0j0srej7e4e1wnfmrhc
        foreign key (owner) references cloud.users (username)
);