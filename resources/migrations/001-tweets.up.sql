CREATE TABLE IF NOT EXISTS tweets (
  id VARCHAR(25) NOT NULL UNIQUE,
  author VARCHAR(40) NOT NULL,
  hashtags VARCHAR(200),
  text VARCHAR(300),
  datecreated VARCHAR(40),
  primary key(id)
)
