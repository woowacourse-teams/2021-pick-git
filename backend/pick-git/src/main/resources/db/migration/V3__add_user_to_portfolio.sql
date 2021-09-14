alter table portfolio add column user_id bigint not null;

alter table portfolio
    add constraint fk_portfolio_to_user
        foreign key (user_id)
            references user (id);
