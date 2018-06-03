drop table if exists orders;

create table orders(
   id INT NOT NULL,
   item VARCHAR(100),
   amount INT,
   description VARCHAR(100),
   processed BOOLEAN,
   PRIMARY KEY ( id )
);