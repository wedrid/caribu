
-- Creo tabell --
CREATE TABLE opTab( name varchar(10),
                id varchar(5), 
                cognome varchar(15),
                numReq INTEGER);


INSERT INTO broker.operator(id, nameop, surnameop, numReq, isavailable) 
        VALUES ('157872', 'Caterina', 'Viola', 0, TRUE);

-- Aggiungo elementi --
INSERT INTO opTab(id, name, cognome, numReq) 
        VALUES ('A3CCB', 'Luca', 'Neri', 0, TRUE);

-- Seleziono elementi --
SELECT * FROM schema.tratta;


CREATE TABLE tratta (
                id varchar(10),
                origine varchar(10),
                destinazione varchar(10),
                km INTEGER, 
                costo INTEGER);


INSERT INTO broker.tratta( id, origine, destinazione, km, costo, numReq) 
        VALUES ('A3CCB', 'Firenze', 'Bari',670, 0, 10);


ALTER TABLE schema.tratta ADD COLUMN geolocation geography(POINT, 4326);

INSERT INTO schema.tratta (id_tratta, latitudine, longitudine, origin_geom, destination_geom)
VALUES (
  'QB999Q',
  0,
  10, 
  ST_SetSRID(ST_Point(13.36667, 38.10833), 4326),
  ST_SetSRID(ST_Point(15.09028, 37.50000), 4326) 
);


INSERT INTO schema.fornitore(id_fornitore, nome, paese)
VALUES (
    'AAAA',
    'Pippo',
    'Italia'
);

INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, 
        lunghezza, larghezza, profondità, id_fornitore, costo, operativo)
VALUES (
  'FIRO',
  ST_SetSRID(ST_Point(11.2462600, 43.7792500), 4326),
  ST_SetSRID(ST_Point(12.5113300, 41.8919300), 4326),
   10,
   10,
   10,
   'AAAA',
   100,
   'QWER12' 
);

INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, 
        lunghezza, larghezza, profondità, id_fornitore, costo, operativo)
VALUES (
  'FIPI',
  ST_SetSRID(ST_Point(11.2462600, 43.7792500), 4326),
  ST_SetSRID(ST_Point(10.4036000, 43.7085300), 4326),
   10,
   10,
   10,
   'AAAA',
   100,
   'QWER12' 
  
);
