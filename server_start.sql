-- Odstranění všech tabulek
DROP TABLE STOPA;
DROP TABLE DETEKOVATELNA_STOPA;
DROP TABLE DETEKTOR_STOP;
DROP TABLE TYP_ZANECHAVANE_STOPY;
DROP TABLE VLASTNICTVI;
DROP TABLE PREDMET;
DROP TABLE KOUZELNIK;
DROP TABLE TYP_MAGICKE_STOPY;
DROP TABLE STAV_DETEKTORU;
DROP TABLE NEBEZPECNOST;
DROP TABLE TYP_PREDMETU;
DROP TABLE KOUZELNICKA_UROVEN;

DROP SEQUENCE TYP_PREDMETU_SEQ;
DROP SEQUENCE STAV_DETEKTORU_SEQ;
DROP SEQUENCE TYP_MAGICKE_STOPY_SEQ;
DROP SEQUENCE DETEKTOR_STOP_SEQ;

-- Generátory automatických hodnot pro primární klíče
CREATE SEQUENCE TYP_PREDMETU_SEQ
	START WITH 1
	INCREMENT BY 1;

CREATE SEQUENCE STAV_DETEKTORU_SEQ
	START WITH 1
	INCREMENT BY 1;

CREATE SEQUENCE TYP_MAGICKE_STOPY_SEQ
	START WITH 1
	INCREMENT BY 1;

CREATE SEQUENCE DETEKTOR_STOP_SEQ
	START WITH 1
	INCREMENT BY 1;


-- Vytváření tabulek

CREATE TABLE KOUZELNICKA_UROVEN 
(
	id_urovne INTEGER PRIMARY KEY NOT NULL,
	nazev VARCHAR2(50) 
);

CREATE TABLE TYP_PREDMETU
(
	id_typu INTEGER DEFAULT TYP_PREDMETU_SEQ.NEXTVAL PRIMARY KEY,
	nazev VARCHAR2(50) 
);


CREATE TABLE NEBEZPECNOST
(
	stupen INTEGER PRIMARY KEY NOT NULL,
	popis VARCHAR2(100) 
);

CREATE TABLE STAV_DETEKTORU
(
	id_stavu INTEGER DEFAULT STAV_DETEKTORU_SEQ.NEXTVAL PRIMARY KEY,
	popis VARCHAR2(100)
);

CREATE TABLE TYP_MAGICKE_STOPY
(
	id_stopy INTEGER DEFAULT TYP_MAGICKE_STOPY_SEQ.NEXTVAL PRIMARY KEY,
	popis VARCHAR2(100)
);

CREATE TABLE PREDMET
(
	runovy_kod NVARCHAR2(50) PRIMARY KEY NOT NULL,
	nazev VARCHAR2(50) NOT NULL,
	velikost INTEGER,
	stupen_nebezpecnosti INTEGER,
	typ_predmetu INTEGER,	
	
	image NVARCHAR2(50),
	
	CONSTRAINT fk_stupen_nebezpecnosti FOREIGN KEY (stupen_nebezpecnosti) REFERENCES NEBEZPECNOST (stupen),
	CONSTRAINT fk_typ_premetu FOREIGN KEY (typ_predmetu) REFERENCES TYP_PREDMETU (id_typu),

    CONSTRAINT chk_runovy_kod CHECK (LENGTH(runovy_kod) = 8 
    	AND REGEXP_LIKE(runovy_kod, '^[A-Za-z]{3}_[0-9]{4}$'))
);

CREATE TABLE KOUZELNIK
(
	runove_jmeno NVARCHAR2(50) PRIMARY KEY NOT NULL,
	obcanske_jmeno NVARCHAR2(50),
	adresa_pobytu NVARCHAR2(100),
	id_urovne INTEGER,

	specializace NVARCHAR2(50),
	
	kancelar NVARCHAR2(50),
	pracovni_areal NVARCHAR2(50),

	image NVARCHAR2(50),
	
	CONSTRAINT chk_runove_jmeno CHECK (REGEXP_LIKE(runove_jmeno, '^[A-Za-z]+_[A-Za-z]+$')),
	
	CONSTRAINT fk_kouzelnik_id_urovne FOREIGN KEY (id_urovne) REFERENCES KOUZELNICKA_UROVEN (id_urovne)
);


CREATE TABLE VLASTNICTVI
(
	datum_ziskani DATE NOT NULL,
	datum_ztraty DATE,

	runovy_kod NVARCHAR2(50) NOT NULL,
	runove_jmeno NVARCHAR2(50) NOT NULL,
	
	zpusob_ziskani VARCHAR2(100),
	zpusob_ztraty VARCHAR2(100),
	
    CONSTRAINT pk_vlastnictvi PRIMARY KEY (datum_ziskani, runovy_kod, runove_jmeno),
	
	CONSTRAINT fk_vlastnictvi_runovy_kod FOREIGN KEY (runovy_kod) REFERENCES PREDMET (runovy_kod),
	CONSTRAINT fk_vlastnictvi_runove_jmeno FOREIGN KEY (runove_jmeno) REFERENCES KOUZELNIK (runove_jmeno)	
);

CREATE TABLE TYP_ZANECHAVANE_STOPY
(
	runovy_kod NVARCHAR2(50),
	id_stopy INTEGER,
	
    CONSTRAINT pk_typ_zanechavane_stopy PRIMARY KEY (runovy_kod, id_stopy),
	
	CONSTRAINT fk_typ_stopy_runovy_kod FOREIGN KEY (runovy_kod) REFERENCES PREDMET (runovy_kod),
	CONSTRAINT fk_typ_stopy_id_stopy FOREIGN KEY (id_stopy) REFERENCES TYP_MAGICKE_STOPY (id_stopy)
);

CREATE TABLE DETEKTOR_STOP
(
	id_detektoru INTEGER DEFAULT DETEKTOR_STOP_SEQ.NEXTVAL PRIMARY KEY,
	nazev NVARCHAR2(40) NOT NULL,
	id_stavu INTEGER,
	
	CONSTRAINT fk_id_stavu FOREIGN KEY(id_stavu) REFERENCES STAV_DETEKTORU (id_stavu)
);


CREATE TABLE DETEKOVATELNA_STOPA
(
	id_detektoru INTEGER NOT NULL,
	id_stopy INTEGER NOT NULL,
	
	citlivost INTEGER,

    CONSTRAINT pk_detekovatelna_stopa PRIMARY KEY (id_detektoru, id_stopy),
	
	CONSTRAINT fk_id_detektoru FOREIGN KEY (id_detektoru) REFERENCES DETEKTOR_STOP (id_detektoru) ON DELETE CASCADE,	
	CONSTRAINT fk_id_stopy FOREIGN KEY (id_stopy) REFERENCES TYP_MAGICKE_STOPY (id_stopy) ON DELETE CASCADE
);

CREATE TABLE STOPA
(
	datum_vyvolani DATE PRIMARY KEY NOT NULL,
	id_detektoru INTEGER NOT NULL,
	id_stopy INTEGER NOT NULL,
	runove_jmeno NVARCHAR2(50) NOT NULL,
	runovy_kod NVARCHAR2(50),

	CONSTRAINT fk_stopa_id_detektoru FOREIGN KEY(id_detektoru) REFERENCES DETEKTOR_STOP (id_detektoru),
	CONSTRAINT fk_stopa_id_stopy FOREIGN KEY(id_stopy) REFERENCES TYP_MAGICKE_STOPY (id_stopy),
	CONSTRAINT fk_stopa_runove_jmeno FOREIGN KEY(runove_jmeno) REFERENCES KOUZELNIK (runove_jmeno),
	CONSTRAINT fk_stopa_runovy_kod FOREIGN KEY(runovy_kod) REFERENCES PREDMET (runovy_kod)
);


-- Vkládání ukázkových dat
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (1, 'Moták');
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (2, 'Začátečník');
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (3, 'Pokročilý');
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (4, 'Profesionál');
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (5, 'Mistr');
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (6, 'Mistr kouzelných řemesel');
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (7, 'Velký mistr');
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (8, 'Archiřeěitel');
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (9, 'Mág');
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (10, 'Velký mág');
INSERT INTO KOUZELNICKA_UROVEN (id_urovne, nazev) VALUES (11, 'Arcimág');

INSERT INTO TYP_PREDMETU (nazev) VALUES ('Amulet');
INSERT INTO TYP_PREDMETU (nazev) VALUES ('Hůlka');
INSERT INTO TYP_PREDMETU (nazev) VALUES ('Koště');
INSERT INTO TYP_PREDMETU (nazev) VALUES ('Kniha zaklínačských lektvarů');
INSERT INTO TYP_PREDMETU (nazev) VALUES ('Kámen změny');
INSERT INTO TYP_PREDMETU (nazev) VALUES ('Feromagický artefakt');
INSERT INTO TYP_PREDMETU (nazev) VALUES ('Plášť');
INSERT INTO TYP_PREDMETU (nazev) VALUES ('Mapa');

INSERT INTO NEBEZPECNOST (stupen, popis) VALUES (0, 'Zcela bezpečný');
INSERT INTO NEBEZPECNOST (stupen, popis) VALUES (1, 'Mírně nebezpečný');
INSERT INTO NEBEZPECNOST (stupen, popis) VALUES (3, 'Středně nebezpečný');
INSERT INTO NEBEZPECNOST (stupen, popis) VALUES (5, 'Nebezpečný');
INSERT INTO NEBEZPECNOST (stupen, popis) VALUES (7, 'Velmi nebezpečný');
INSERT INTO NEBEZPECNOST (stupen, popis) VALUES (10, 'Pohledem zabíjející');

INSERT INTO STAV_DETEKTORU (popis) VALUES ('Funkční');
INSERT INTO STAV_DETEKTORU (popis) VALUES ('Nefunkční');
INSERT INTO STAV_DETEKTORU (popis) VALUES ('Očarovaný');
INSERT INTO STAV_DETEKTORU (popis) VALUES ('Zachovalý');

INSERT INTO TYP_MAGICKE_STOPY (popis) VALUES ('Nevysvětlitelné zmizení');
INSERT INTO TYP_MAGICKE_STOPY (popis) VALUES ('Zkoumání mysli');
INSERT INTO TYP_MAGICKE_STOPY (popis) VALUES ('Temný hluk');
INSERT INTO TYP_MAGICKE_STOPY (popis) VALUES ('Prokletí');
INSERT INTO TYP_MAGICKE_STOPY (popis) VALUES ('Kletba');

-- KOUZELNIK
INSERT INTO KOUZELNIK (runove_jmeno, obcanske_jmeno, adresa_pobytu, id_urovne, image) VALUES ('harry_potter', 'Harry Potter', 'Příčná ulice 4, Godrikův Důl', 9, 'harry_potter.png');
INSERT INTO KOUZELNIK (runove_jmeno, obcanske_jmeno, adresa_pobytu, id_urovne, image) VALUES ('hermiona_grangerova', 'Hermiona Grangerová', 'Výsluní 6, Kneazlev', 10, 'hermione_granger.png');
INSERT INTO KOUZELNIK (runove_jmeno, obcanske_jmeno, adresa_pobytu, id_urovne, image) VALUES ('ron_weasley', 'Ron Weasley', 'Křivá ulice 3, Slizolin', 9, 'ron_weasley.png');
INSERT INTO KOUZELNIK (runove_jmeno, obcanske_jmeno, adresa_pobytu, id_urovne, specializace, kancelar, image) VALUES ('albus_dumbledore', 'Albus Dumbledore', 'Bradavická 13, Bradavice', 10, 'Ministr', 'Ředitelova pracovna', 'albus_dumbledor.png');

-- HAGRID
INSERT INTO KOUZELNIK (runove_jmeno, obcanske_jmeno, adresa_pobytu, id_urovne, specializace, pracovni_areal, image) VALUES ('rubeus_hagrid', 'Rubeus Hagrid', 'Pumpkin Drive 4, Godrikův Důl', 8, 'Správce', 'Oddělení pro starověké magické předměty', 'hagrid.png');

-- SEVERUS SNAPE
INSERT INTO KOUZELNIK (runove_jmeno, obcanske_jmeno, adresa_pobytu, id_urovne, image) VALUES ('severus_snape', 'Severus Snape', 'Spinners End 2, Spinners End, Spinners End', 10, 'severus_snape.png');

-- SIRIUS BLACK
INSERT INTO KOUZELNIK (runove_jmeno, obcanske_jmeno, adresa_pobytu, id_urovne, image) VALUES ('sirius_black', 'Sirius Black', 'Grimmauldovo náměstí 12, Londýn', 9, 'sirius_black.png');


INSERT INTO PREDMET (runovy_kod, nazev, velikost, stupen_nebezpecnosti, typ_predmetu, image) VALUES ('AML_0001', 'Amulet neviditelnosti', 2, 5, 1, 'amulet_neviditelnosti.png');
INSERT INTO PREDMET (runovy_kod, nazev, velikost, stupen_nebezpecnosti, typ_predmetu, image) VALUES ('HLK_3000', 'Hůlka srdcí', 1, 3, 2, 'hulka_srdci.png');
INSERT INTO PREDMET (runovy_kod, nazev, velikost, stupen_nebezpecnosti, typ_predmetu, image) VALUES ('KST_0024', 'Koště Firebolt', 5, 7, 3, 'firebolt.png');
INSERT INTO PREDMET (runovy_kod, nazev, velikost, stupen_nebezpecnosti, typ_predmetu, image) VALUES ('KNH_1000', 'Kniha zaklínačských lektvarů', 3, 3, 4, 'kniha_lektvaru.png');
INSERT INTO PREDMET (runovy_kod, nazev, velikost, stupen_nebezpecnosti, typ_predmetu, image) VALUES ('KMN_1234', 'Kámen změny', 2, 7, 5, 'kamen_zmeny.png');
INSERT INTO PREDMET (runovy_kod, nazev, velikost, stupen_nebezpecnosti, typ_predmetu, image) VALUES ('FRM_7315', 'Feromagnetický artefakt', 4, 10, 6, 'feromagneticky_artefakt.png');

INSERT INTO PREDMET (runovy_kod, nazev, velikost, stupen_nebezpecnosti, typ_predmetu, image) VALUES ('PLS_0542', 'Neviditelný plášť', 9, 10, 7, 'neviditelny_plast.png');
INSERT INTO PREDMET (runovy_kod, nazev, velikost, stupen_nebezpecnosti, typ_predmetu, image) VALUES ('MAP_4207', 'Mapa zlodějů', 3, 3, 8, 'mapa_zlodeju.png');
INSERT INTO PREDMET (runovy_kod, nazev, velikost, stupen_nebezpecnosti, typ_predmetu, image) VALUES ('HLK_9578', 'Hůlka z jantarového dřeva', 1, 7, 2, 'jantarova_hulka.png');


INSERT INTO TYP_ZANECHAVANE_STOPY (runovy_kod, id_stopy) VALUES ('AML_0001', 2);
INSERT INTO TYP_ZANECHAVANE_STOPY (runovy_kod, id_stopy) VALUES ('HLK_3000', 3);
INSERT INTO TYP_ZANECHAVANE_STOPY (runovy_kod, id_stopy) VALUES ('HLK_3000', 4);
INSERT INTO TYP_ZANECHAVANE_STOPY (runovy_kod, id_stopy) VALUES ('KST_0024', 1);
INSERT INTO TYP_ZANECHAVANE_STOPY (runovy_kod, id_stopy) VALUES ('KNH_1000', 4);
INSERT INTO TYP_ZANECHAVANE_STOPY (runovy_kod, id_stopy) VALUES ('KMN_1234', 5);
INSERT INTO TYP_ZANECHAVANE_STOPY (runovy_kod, id_stopy) VALUES ('FRM_7315', 3);
INSERT INTO TYP_ZANECHAVANE_STOPY (runovy_kod, id_stopy) VALUES ('FRM_7315', 5);

INSERT INTO DETEKTOR_STOP (id_stavu, nazev) VALUES (1, 'Přízrakový Podezřívač');
INSERT INTO DETEKTOR_STOP (id_stavu, nazev) VALUES (2, 'Záznamovač Zvuků');
INSERT INTO DETEKTOR_STOP (id_stavu, nazev) VALUES (1, 'Stopař Šepotů');
INSERT INTO DETEKTOR_STOP (id_stavu, nazev) VALUES (3, 'Senzor Slyšeností');

INSERT INTO DETEKOVATELNA_STOPA (id_detektoru, id_stopy, citlivost) VALUES (1, 1, 5);
INSERT INTO DETEKOVATELNA_STOPA (id_detektoru, id_stopy, citlivost) VALUES (1, 2, 3);
INSERT INTO DETEKOVATELNA_STOPA (id_detektoru, id_stopy, citlivost) VALUES (2, 4, 4);
INSERT INTO DETEKOVATELNA_STOPA (id_detektoru, id_stopy, citlivost) VALUES (3, 3, 5);
INSERT INTO DETEKOVATELNA_STOPA (id_detektoru, id_stopy, citlivost) VALUES (4, 5, 5);

INSERT INTO VLASTNICTVI (datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani, zpusob_ztraty, datum_ztraty) VALUES (TO_DATE('2024-03-16', 'YYYY-MM-DD'), 'AML_0001', 'harry_potter', 'Dar od přítele Ron Weasleyho', 'Prodej', TO_DATE('2024-04-18', 'YYYY-MM-DD'));
INSERT INTO VLASTNICTVI (datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani, zpusob_ztraty) VALUES (TO_DATE('2024-04-18', 'YYYY-MM-DD'), 'AML_0001', 'hermiona_grangerova', 'Nákup', NULL);

INSERT INTO VLASTNICTVI (datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani, zpusob_ztraty) VALUES (TO_DATE('2023-05-21', 'YYYY-MM-DD'), 'HLK_3000', 'harry_potter', 'Nákup v obchodě s kouzelnickými potřebami', NULL);
INSERT INTO VLASTNICTVI (datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani, zpusob_ztraty) VALUES (TO_DATE('2022-12-01', 'YYYY-MM-DD'), 'KST_0024', 'albus_dumbledore', 'Vlastní výroba', NULL);
INSERT INTO VLASTNICTVI (datum_ziskani, runovy_kod, runove_jmeno, zpusob_ziskani, zpusob_ztraty) VALUES (TO_DATE('2023-08-10', 'YYYY-MM-DD'), 'KNH_1000', 'hermiona_grangerova', 'Dar od profesora Albusa Dumbledorea', NULL);

INSERT INTO STOPA (datum_vyvolani, id_detektoru, id_stopy, runove_jmeno, runovy_kod) 
VALUES (TO_DATE('2024-01-05', 'YYYY-MM-DD'), 1, 1, 'albus_dumbledore', 'AML_0001');
INSERT INTO STOPA (datum_vyvolani, id_detektoru, id_stopy, runove_jmeno, runovy_kod) 
VALUES (TO_DATE('2024-02-15', 'YYYY-MM-DD'), 1, 2, 'albus_dumbledore', 'AML_0001');
INSERT INTO STOPA (datum_vyvolani, id_detektoru, id_stopy, runove_jmeno, runovy_kod) 
VALUES (TO_DATE('2024-03-10', 'YYYY-MM-DD'), 3, 4, 'albus_dumbledore', 'HLK_3000');