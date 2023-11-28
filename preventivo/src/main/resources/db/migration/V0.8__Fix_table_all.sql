DROP TABLE cache;

DROP TABLE quotes;

CREATE TABLE
    quotes (
        id_quotes VARCHAR PRIMARY KEY,
        id_operativo VARCHAR,
        id_fornitore VARCHAR,
        lunghezza INTEGER,
        larghezza INTEGER,
        profondità INTEGER,
        costo INTEGER,
        origin_geom geometry(Point, 4326),
        destination_geom geometry(Point, 4326)
    );

CREATE TABLE
    cache (
        id_commessa VARCHAR NOT NULL,
        id_quotes VARCHAR NOT NULL,
        operativo VARCHAR,
        lunghezza INTEGER,
        larghezza INTEGER,
        profondità INTEGER,
        id_fornitore VARCHAR,
        id_tratta VARCHAR,
        costo INTEGER,
        destination_geom geometry(Point, 4326),
        origin_geom geometry(Point, 4326),
        PRIMARY KEY (id_commessa, id_quotes),
        FOREIGN KEY (id_quotes) REFERENCES quotes(id_quotes)
    );

