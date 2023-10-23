 create table users
(
    user_id      int auto_increment
        primary key,
    username     varchar(20)                   not null,
    password     varchar(20)                   not null,
    first_name   varchar(20)                   not null,
    last_name    varchar(20)                   not null,
    email        varchar(50)                   not null,
    is_admin     tinyint(1)                    not null,
    phone_number varchar(20) default 'Unknown' not null,
    is_Blocked   tinyint(1)                    not null,
    constraint users_pk2
        unique (username),
    constraint users_pk3
        unique (email)
);

create table posts
(
    post_id int auto_increment
        primary key,
    title   varchar(64) not null,
    content text        not null,
    user_id int         null,
    constraint posts_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table comments
(
    comment_id int auto_increment
        primary key,
    content    text not null,
    post_id    int  null,
    user_id    int  null,
    constraint comments_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint comments_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table liked_posts
(
    post_id int null,
    user_id int null,
    constraint liked_posts_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint liked_posts_users_user_id_fk
        foreign key (user_id) references users (user_id)
);
    create table admin_phones
 (
     phone_number varchar(12) not null
         primary key,
     user_id      int         null,
     constraint phone_number_users_user_id_fk
         foreign key (user_id) references users (user_id)
 );
 create table tags
 (
     tag_id   int auto_increment
         primary key,
     tag_name varchar(20) not null,
     constraint tags_pk2
         unique (tag_name)
 );

 create table posts_tags
 (
     post_id int not null,
     tag_id  int not null,
     constraint posts_tags_posts_post_id_fk
         foreign key (post_id) references posts (post_id),
     constraint posts_tags_tags_tag_id_fk
         foreign key (tag_id) references tags (tag_id)
 );
