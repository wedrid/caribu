DROP TABLE cache;

CREATE TABLE cache (
    id_row VARCHAR PRIMARY KEY,
    id_commessa VARCHAR,
    id_quotes VARCHAR,
    FOREIGN KEY (id_quotes) REFERENCES quotes(id_quotes)
);