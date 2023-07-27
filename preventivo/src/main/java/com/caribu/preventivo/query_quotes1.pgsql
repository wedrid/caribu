
-- Sample 1: Florence (Tuscany) to Modena (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('FIMO', ST_SetSRID(ST_Point(43.7696, 11.2558), 4326), ST_SetSRID(ST_Point(44.6471, 10.9252), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 2: Prato (Tuscany) to Parma (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('PRPA', ST_SetSRID(ST_Point(43.8777, 11.1022), 4326), ST_SetSRID(ST_Point(44.8015, 10.3279), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 3: Pistoia (Tuscany) to Reggio Emilia (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('PTRE', ST_SetSRID(ST_Point(43.9333, 10.9171), 4326), ST_SetSRID(ST_Point(44.6983, 10.6312), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- More samples as per your requirement follow same syntax. For other city in Tuscany and Emilia Romagna, replace the city name abbreviation, coordinate points accordingly.
-- Sample 4: Lucca (Tuscany) to Bologna (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('LUBO', ST_SetSRID(ST_Point(43.843, 10.5062), 4326), ST_SetSRID(ST_Point(44.4949, 11.3426), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 5: Massa (Tuscany) to Ferrara (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('MAFE', ST_SetSRID(ST_Point(44.0254, 10.1445), 4326), ST_SetSRID(ST_Point(44.8379, 11.6204), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 6: Carrara (Tuscany) to Ravenna (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('CARA', ST_SetSRID(ST_Point(44.0793, 10.0971), 4326), ST_SetSRID(ST_Point(44.4167, 11.9833), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 7: Livorno (Tuscany) to Forlì (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('LIFO', ST_SetSRID(ST_Point(43.5485, 10.3106), 4326), ST_SetSRID(ST_Point(44.2227, 12.0407), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 8: Pisa (Tuscany) to Cesena (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('PICE', ST_SetSRID(ST_Point(43.7167, 10.4000), 4326), ST_SetSRID(ST_Point(44.1365, 12.2414), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 9: Arezzo (Tuscany) to Rimini (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('ARRI', ST_SetSRID(ST_Point(43.4712, 11.8818), 4326), ST_SetSRID(ST_Point(44.0589, 12.5635), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 10: Siena (Tuscany) to Piacenza (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('SIPA', ST_SetSRID(ST_Point(43.3188, 11.3308), 4326), ST_SetSRID(ST_Point(45.0500, 9.7000), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 11: Grosseto (Tuscany) to Parma (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('GRPA', ST_SetSRID(ST_Point(42.7603, 11.1136), 4326), ST_SetSRID(ST_Point(44.8015, 10.3279), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 12: Florence (Tuscany) to Reggio Emilia (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('FIRE', ST_SetSRID(ST_Point(43.7696, 11.2558), 4326), ST_SetSRID(ST_Point(44.6983, 10.6312), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');

-- Sample 13: Pistoia (Tuscany) to Modena (Emilia Romagna)
INSERT INTO schema.quotes(id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ('PTMO', ST_SetSRID(ST_Point(43.9333, 10.9171), 4326), ST_SetSRID(ST_Point(44.6471, 10.9252), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12');



-- Verona to Padua
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'VEPA', ST_SetSRID(ST_Point(10.9916, 45.4383), 4326), ST_SetSRID(ST_Point(11.8768, 45.4064), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Trieste to Taranto
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'TRTA', ST_SetSRID(ST_Point(13.7768, 45.6495), 4326), ST_SetSRID(ST_Point(17.2470, 40.4644), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Parma to Prato
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'PRPR', ST_SetSRID(ST_Point(10.3279, 44.8015), 4326), ST_SetSRID(ST_Point(11.1022, 43.8777), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Modena to Reggio Emilia
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'MORE', ST_SetSRID(ST_Point(10.9252, 44.6471), 4326), ST_SetSRID(ST_Point(10.6312, 44.6981), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Perugia to Livorno
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'PELI', ST_SetSRID(ST_Point(12.3908, 43.1107), 4326), ST_SetSRID(ST_Point(10.3106, 43.5485), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Ravenna to Cagliari
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'RACA', ST_SetSRID(ST_Point(12.1994, 44.4178), 4326), ST_SetSRID(ST_Point(9.1192, 39.2305), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Foggia to Salerno
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'FOSA', ST_SetSRID(ST_Point(15.5446, 41.4628), 4326), ST_SetSRID(ST_Point(14.7699, 40.6804), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Monza to Verona
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'MOVE', ST_SetSRID(ST_Point(9.2748, 45.5845), 4326), ST_SetSRID(ST_Point(10.9916, 45.4383), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Padua to Trieste
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'PATR', ST_SetSRID(ST_Point(11.8768, 45.4064), 4326), ST_SetSRID(ST_Point(13.7768, 45.6495), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Taranto to Parma
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'TAPA', ST_SetSRID(ST_Point(17.2470, 40.4644), 4326), ST_SetSRID(ST_Point(10.3279, 44.8015), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Prato to Modena
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'PRMO', ST_SetSRID(ST_Point(11.1022, 43.8777), 4326), ST_SetSRID(ST_Point(10.9252, 44.6471), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Reggio Emilia to Perugia
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'REPE', ST_SetSRID(ST_Point(10.6312, 44.6981), 4326), ST_SetSRID(ST_Point(12.3908, 43.1107), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Livorno to Ravenna
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'LIRA', ST_SetSRID(ST_Point(10.3106, 43.5485), 4326), ST_SetSRID(ST_Point(12.1994, 44.4178), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Cagliari to Foggia
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'CAFO', ST_SetSRID(ST_Point(9.1192, 39.2305), 4326), ST_SetSRID(ST_Point(15.5446, 41.4628), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

-- Salerno to Monza
INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'SALM', ST_SetSRID(ST_Point(14.7699, 40.6804), 4326), ST_SetSRID(ST_Point(9.2748, 45.5845), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );


INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'PRALB', ST_SetSRID(ST_Point(11.0969900, 43.8805000), 4326), ST_SetSRID(ST_Point(10.6102400, 44.6285900), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'PRRUB', ST_SetSRID(ST_Point(11.0969900, 43.8805000), 4326), ST_SetSRID(ST_Point(10.7794000, 44.6515800), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );

INSERT INTO schema.quotes (id_quotes, origin_geom, destination_geom, lunghezza, larghezza, profondità, id_fornitore, costo, operativo) 
VALUES ( 'IMPRUB', ST_SetSRID(ST_Point(11.2543400, 43.6845300), 4326), ST_SetSRID(ST_Point(10.6312, 44.6981), 4326), 10, 10, 10, 'AAAA', 100, 'QWER12' );
