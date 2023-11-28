DROP TABLE cache;

DROP TABLE quotes;

CREATE TABLE
    quotes (
        id_quotes INTEGER PRIMARY KEY,
        id_operativo INTEGER,
        id_fornitore INTEGER,
        lunghezza INTEGER,
        larghezza INTEGER,
        profondità INTEGER,
        costo INTEGER,
        origin_geom geometry(Point, 4326),
        destination_geom geometry(Point, 4326)
    );

CREATE TABLE
    cache (
        id_commessa INTEGER NOT NULL,
        id_quotes INTEGER NOT NULL,
        id_operativo INTEGER,
        lunghezza INTEGER,
        larghezza INTEGER,
        profondità INTEGER,
        id_fornitore INTEGER,
        costo INTEGER,
        destination_geom geometry(Point, 4326),
        origin_geom geometry(Point, 4326),
        PRIMARY KEY (id_commessa, id_quotes),
        FOREIGN KEY (id_quotes) REFERENCES quotes(id_quotes)
    );