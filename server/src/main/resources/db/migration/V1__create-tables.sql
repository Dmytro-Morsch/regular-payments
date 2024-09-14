create table payment
(
    id                bigint primary key generated always as identity,
    payer_name        varchar(100)   not null,
    payer_inn         varchar(10) not null,
    payer_card_number varchar(16) not null,
    payee_account     varchar(29) not null,
    payee_mfo         varchar(6)  not null,
    payee_okpo        varchar(8)  not null,
    payee_name        varchar(100)   not null,
    payment_period    bigint      not null,
    payment_amount    decimal(12, 2) not null
);

create table payment_transaction
(
    id           bigint primary key generated always as identity,
    tx_timestamp timestamp      not null,
    amount       decimal(12, 2) not null,
    status       char(1)        not null,
    payment_id   bigint         not null references payment (id) on delete cascade
);