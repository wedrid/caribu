DROP TABLE client.requests;
DROP TABLE client.clients;

-- Create the Clients table
CREATE TABLE clients (
  client_id SERIAL PRIMARY KEY,
  ragione_sociale VARCHAR(255),
  date_added TIMESTAMP
);

-- Create the Requests table
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
  FOREIGN KEY (client_id) REFERENCES Clients(client_id)
);
