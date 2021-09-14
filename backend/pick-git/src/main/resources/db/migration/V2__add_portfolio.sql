create table contact
(
    id           bigint       not null auto_increment,
    category     varchar(255) not null,
    value        varchar(255) not null,
    portfolio_id bigint       not null,
    primary key (id)
);

create table description
(
    id      bigint not null auto_increment,
    value   varchar(255),
    item_id bigint,
    primary key (id)
);

create table item
(
    id         bigint not null auto_increment,
    category   varchar(255),
    section_id bigint,
    primary key (id)
);

create table portfolio
(
    id                  bigint       not null auto_increment,
    introduction        varchar(255),
    profile_image_shown boolean      not null,
    profile_image_url   varchar(255) not null,
    primary key (id)
);

create table project
(
    id           bigint       not null auto_increment,
    content      varchar(255),
    end_date     timestamp,
    image_url    varchar(255) not null,
    name         varchar(255) not null,
    start_date   timestamp,
    type         integer      not null,
    portfolio_id bigint       not null,
    primary key (id)
);

create table project_tag
(
    id         bigint not null auto_increment,
    project_id bigint,
    tag_id     bigint,
    primary key (id)
);

create table section
(
    id           bigint       not null auto_increment,
    name         varchar(255) not null,
    portfolio_id bigint,
    primary key (id)
);

alter table contact
    add constraint fk_contact_to_portfolio
        foreign key (portfolio_id)
            references portfolio (id);

alter table description
    add constraint fk_description_to_item
        foreign key (item_id)
            references item (id);

alter table item
    add constraint fk_item_to_section
        foreign key (section_id)
            references section (id);

alter table project
    add constraint fk_project_to_portfolio
        foreign key (portfolio_id)
            references portfolio (id);

alter table project_tag
    add constraint fk_project_tag_to_project
        foreign key (project_id)
            references project (id);

alter table project_tag
    add constraint fk_project_tag_to_tag
        foreign key (tag_id)
            references tag (id);

alter table section
    add constraint fk_section_to_portfolio
        foreign key (portfolio_id)
            references portfolio (id);
