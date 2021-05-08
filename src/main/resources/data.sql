INSERT INTO ACCOUNT (id, name, password, activated)
VALUES (1, 'admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 1);

INSERT INTO AUTHORITY (authority_name) values ('ROLE_USER');
INSERT INTO AUTHORITY (authority_name) values ('ROLE_ADMIN');

INSERT INTO USER_AUTHORITY (id, authority_name) values (1, 'ROLE_USER');
INSERT INTO USER_AUTHORITY (id, authority_name) values (1, 'ROLE_ADMIN');
