create table comment
(
    id      bigint   not null auto_increment,
    content longtext not null,
    post_id bigint,
    user_id bigint,
    primary key (id)
);

create table follow
(
    id        bigint not null auto_increment,
    source_id bigint,
    target_id bigint,
    primary key (id)
);

create table image
(
    id      bigint       not null auto_increment,
    url     varchar(255) not null,
    post_id bigint,
    primary key (id)
);

create table likes
(
    id      bigint not null auto_increment,
    post_id bigint,
    user_id bigint,
    primary key (id)
);

create table post
(
    id              bigint not null auto_increment,
    content         longtext,
    created_at      timestamp,
    github_repo_url varchar(255),
    updated_at      timestamp,
    user_id         bigint,
    primary key (id)
);

create table post_tag
(
    id      bigint not null auto_increment,
    post_id bigint not null,
    tag_id  bigint not null,
    primary key (id)
);

create table tag
(
    id   bigint      not null auto_increment,
    name varchar(20) not null,
    primary key (id)
);

create table user
(
    id          bigint       not null auto_increment,
    description varchar(255),
    image       varchar(255),
    name        varchar(255) not null,
    company     varchar(255),
    github_url  varchar(255) not null,
    location    varchar(255),
    twitter     varchar(255),
    website     varchar(255),
    primary key (id)
);

alter table follow
    add constraint uk_follow_source_target unique (source_id, target_id);

alter table tag
    add constraint uk_tag_name unique (name);

alter table comment
    add constraint fk_comment_to_post
        foreign key (post_id)
            references post (id);

alter table comment
    add constraint fk_comment_to_user
        foreign key (user_id)
            references user (id);

alter table follow
    add constraint fk_follow_to_source
        foreign key (source_id)
            references user (id);

alter table follow
    add constraint fk_follow_to_target
        foreign key (target_id)
            references user (id);

alter table image
    add constraint fk_image_to_post
        foreign key (post_id)
            references post (id);

alter table likes
    add constraint fk_like_to_post
        foreign key (post_id)
            references post (id);

alter table likes
    add constraint fk_like_to_user
        foreign key (user_id)
            references user (id);

alter table post
    add constraint fk_post_to_user
        foreign key (user_id)
            references user (id);

alter table post_tag
    add constraint fk_post_tag_to_post
        foreign key (post_id)
            references post (id);

alter table post_tag
    add constraint fk_post_tag_to_tag
        foreign key (tag_id)
            references tag (id);
