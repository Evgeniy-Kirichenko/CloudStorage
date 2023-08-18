create table users_roles
(
    user_id bigint not null,
    role_id bigint not null,
    constraint FK2o0jvgh89lemvvo17cbqvdxaa
        foreign key (user_id) references cloud.users (id),
    constraint FKj6m8fwv7oqv74fcehir1a9ffy
        foreign key (role_id) references cloud.roles (id)
);