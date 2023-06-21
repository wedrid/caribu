DROP TABLE client.requests;
DROP TABLE client.clients;

-- Create the Clients table
CREATE TABLE clients (
  client_id SERIAL PRIMARY KEY,
  ragione_sociale VARCHAR(255),
  created_at TIMESTAMP
);

-- Create the Requests table
-- TODO: constraints on the fields
CREATE TABLE requests (
  request_id SERIAL PRIMARY KEY,
  client_id SERIAL,
  request_date NUMERIC,
  filiale_id NUMERIC,
  depth NUMERIC,
  width NUMERIC,
  height NUMERIC,
  weight NUMERIC,
  tratta_id NUMERIC,
  due_date TIMESTAMP,
  created_at TIMESTAMP,
  FOREIGN KEY (client_id) REFERENCES Clients(client_id)
);

-- Schema at: https://dbdiagram.io/d/6492e92402bd1c4a5ed69e1e 