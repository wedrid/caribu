DROP TABLE cache;

CREATE TABLE cache (
        id_commessa VARCHAR NOT NULL, 
        id_quotes VARCHAR NOT NULL,
        operativo VARCHAR(10),
        lunghezza INTEGER,
        larghezza INTEGER,
        profondità INTEGER,
        id_fornitore VARCHAR,
        id_tratta VARCHAR,
        costo INTEGER,
        destination_geom geometry(Point, 4326),
        origin_geom geometry(Point, 4326),
        PRIMARY KEY (id_commessa, id_quotes),
        FOREIGN KEY (id_quotes) REFERENCES quotes(id_quotes),
        FOREIGN KEY (id_fornitore) REFERENCES fornitore(id_fornitore)
        );