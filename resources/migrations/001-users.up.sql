CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(25) NOT NULL UNIQUE,
  email VARCHAR(25) NOT NULL UNIQUE,
  password VARCHAR(25) NOT NULL,
  primary key(id)
)