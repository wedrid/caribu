
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
SELECT * FROM broker.operator;