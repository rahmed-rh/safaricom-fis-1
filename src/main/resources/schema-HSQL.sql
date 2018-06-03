drop table if exists orders;

create table orders (
  id INTEGER IDENTITY PRIMARY KEY,
  item varchar(100),
  amount integer,
  description varchar(100),
  processed boolean
);
