-- ============================================================
--  Probni prijemni ispit — podaci za testiranje
--  Pokrenuti u SQLyog-u sa Ctrl+F9 (izvrsi sve upite)
--  Pretpostavlja da su tabele vec kreirane (baza/sema.sql)
-- ============================================================

USE probni_ispit;

-- ------------------------------------------------------------
-- 1. Ciscenje postojecih podataka
--    Redosled je bitan zbog stranih kljuceva
-- ------------------------------------------------------------

DELETE FROM uplata;
DELETE FROM prijava_termin;
DELETE FROM prijava;
DELETE FROM stavka_kataloga;
DELETE FROM katalog;
DELETE FROM termin;
DELETE FROM korisnik;

ALTER TABLE uplata AUTO_INCREMENT = 1;
ALTER TABLE prijava AUTO_INCREMENT = 1;
ALTER TABLE stavka_kataloga AUTO_INCREMENT = 1;
ALTER TABLE katalog AUTO_INCREMENT = 1;
ALTER TABLE termin AUTO_INCREMENT = 1;
ALTER TABLE korisnik AUTO_INCREMENT = 1;

-- ------------------------------------------------------------
-- 2. Korisnici
--    Lozinke su BCrypt hesevi. Cist oblik lozinke:
--      administrator ...... veljko
--      kandidati .......... ime + prezime malim slovima
--                           (npr. markomarkovic)
-- ------------------------------------------------------------

INSERT INTO korisnik (id, ime, prezime, email, lozinka_hash, uloga) VALUES
(1,  'Veljko',  'Đurić',      'veljkodjuric11@gmail.com', '$2b$10$nPBhcUBXV3DkO/Kf47DF.u5GGCe9SimmMtM/5qpMg9DhjSIgV3HC2', 'ADMIN'),
(2,  'Marko',   'Marković',   'marko.markovic@test.com',   '$2b$10$ZPNunSUZ5YpPuAnIFC2XzexOG/Md2dVOnBsbZVk3W9Zft.6SpPBuy', 'KANDIDAT'),
(3,  'Ana',     'Jovanović',  'ana.jovanovic@test.com',    '$2b$10$v50t19DAP6eZngzR0hTAs.MYjLYUIO9GmDPJJ2Skr5VX8hmPyZEfi', 'KANDIDAT'),
(4,  'Stefan',  'Petrović',   'stefan.petrovic@test.com',  '$2b$10$KWPGot5Q35uy8846SWZBluVLYQ8VVxGM1myJLcIBYf2AAgYty5U0K', 'KANDIDAT'),
(5,  'Milica',  'Nikolić',    'milica.nikolic@test.com',   '$2b$10$vYSvFlla215nPuc6ZYtRj.E04WYpo4xG18PVifa2Ni5248AlcFcQ.', 'KANDIDAT'),
(6,  'Nikola',  'Ilić',       'nikola.ilic@test.com',      '$2b$10$uo76QkZ1O6At4.TiMwE9i.vdkNB9sE6LfuSvnMn8oDpSE4lZGO5Du', 'KANDIDAT'),
(7,  'Jovana',  'Stanković',  'jovana.stankovic@test.com', '$2b$10$RRxUhM/Xx2m4n88L8FxhAeFzxrFKZ043yIGFY8wiWiQxK8SEQA7iC', 'KANDIDAT'),
(8,  'Luka',    'Pavlović',   'luka.pavlovic@test.com',    '$2b$10$F7af/iZmG9KauphHWMUPGOHr8hj3kBUrbQ.lla.gXQczd.2BPjy9a', 'KANDIDAT'),
(9,  'Teodora', 'Ristić',     'teodora.ristic@test.com',   '$2b$10$j8F/MZp/hmEwPjfuunkSdedvwSr2FutX4RPE/0tERD.dUj08S1lnC', 'KANDIDAT'),
(10, 'Petar',   'Simić',      'petar.simic@test.com',      '$2b$10$M2bujxayXWNWglor3ndRcOSTUv3TsXWxSqYtqZD/uNqanGfren3q.', 'KANDIDAT'),
(11, 'Sara',    'Todorović',  'sara.todorovic@test.com',   '$2b$10$BFEWFTOygKwSawBZ8BTP3OBkGv5Nx8MHT.eygdOltMOq4DvMx5kWy', 'KANDIDAT');

-- ------------------------------------------------------------
-- 3. Katalozi cena
--    Katalog 1 vazi od pocetka godine (trenutno na snazi)
--    Katalog 2 stupa na snagu 1.12.2026. (buduci)
-- ------------------------------------------------------------

INSERT INTO katalog (id, naziv, vazi_od) VALUES
(1, 'Cenovnik 2026',        '2026-01-01'),
(2, 'Cenovnik zima 2026/27', '2026-12-01');

INSERT INTO stavka_kataloga (id, katalog_id, vrsta_ispita, cena) VALUES
(1, 1, 'MATEMATIKA',          1000.00),
(2, 1, 'OPSTA_INFORMISANOST', 1400.00),
(3, 2, 'MATEMATIKA',          1200.00),
(4, 2, 'OPSTA_INFORMISANOST', 1600.00);

-- ------------------------------------------------------------
-- 4. Termini
-- ------------------------------------------------------------

INSERT INTO termin (id, datum, vreme_pocetka, vrsta_ispita, adresa) VALUES
(1, '2026-09-19', '10:00:00', 'MATEMATIKA',          'Jove Ilića 154, Beograd'),
(2, '2026-09-19', '13:00:00', 'OPSTA_INFORMISANOST', 'Jove Ilića 154, Beograd'),
(3, '2026-09-26', '10:00:00', 'MATEMATIKA',          'Jove Ilića 154, Beograd'),
(4, '2026-09-26', '13:00:00', 'OPSTA_INFORMISANOST', 'Jove Ilića 154, Beograd'),
(5, '2026-10-10', '10:00:00', 'MATEMATIKA',          'Sajam, hala 4, Beograd'),
(6, '2026-10-10', '13:00:00', 'OPSTA_INFORMISANOST', 'Sajam, hala 4, Beograd'),
(7, '2026-10-24', '09:00:00', 'MATEMATIKA',          'Jove Ilića 154, Beograd'),
(8, '2026-11-07', '11:00:00', 'OPSTA_INFORMISANOST', 'Jove Ilića 154, Beograd');

-- ------------------------------------------------------------
-- 5. Prijave
--    Cene su zapamcene iz kataloga 1 (1000 / 1400)
--    Statusi: PRIJAVLJEN (placeno), NA_CEKANJU, OTKAZANA
-- ------------------------------------------------------------

INSERT INTO prijava (id, korisnik_id, datum_prijave, ukupna_cena, status) VALUES
(1,  2,  '2026-08-10 09:14:22', 2400.00, 'PRIJAVLJEN'),
(2,  3,  '2026-08-11 11:03:47', 3400.00, 'PRIJAVLJEN'),
(3,  4,  '2026-08-12 16:41:05', 1000.00, 'PRIJAVLJEN'),
(4,  5,  '2026-08-14 08:22:39', 2800.00, 'PRIJAVLJEN'),
(5,  6,  '2026-08-17 13:55:11', 2400.00, 'PRIJAVLJEN'),
(6,  7,  '2026-08-20 10:07:33', 1400.00, 'PRIJAVLJEN'),
(7,  8,  '2026-08-22 15:30:58', 2000.00, 'NA_CEKANJU'),
(8,  9,  '2026-08-25 12:18:44', 3400.00, 'NA_CEKANJU'),
(9,  10, '2026-08-27 09:45:02', 1400.00, 'NA_CEKANJU'),
(10, 11, '2026-08-28 17:12:26', 1000.00, 'NA_CEKANJU'),
(11, 2,  '2026-08-29 14:36:50', 1400.00, 'NA_CEKANJU'),
(12, 3,  '2026-08-30 11:20:15', 0.00,    'OTKAZANA'),
(13, 5,  '2026-08-31 10:05:41', 1000.00, 'NA_CEKANJU');

-- ------------------------------------------------------------
-- 6. Stavke prijava
--    Prijava 13 ima jednu otkazanu stavku (delimicno otkazivanje)
-- ------------------------------------------------------------

INSERT INTO prijava_termin (prijava_id, termin_id, cena_u_trenutku_prijave, otkazana) VALUES
-- Marko Marković, placeno
(1, 1, 1000.00, FALSE),
(1, 2, 1400.00, FALSE),
-- Ana Jovanović, placeno
(2, 1, 1000.00, FALSE),
(2, 2, 1400.00, FALSE),
(2, 3, 1000.00, FALSE),
-- Stefan Petrović, placeno
(3, 1, 1000.00, FALSE),
-- Milica Nikolić, placeno
(4, 2, 1400.00, FALSE),
(4, 4, 1400.00, FALSE),
-- Nikola Ilić, placeno
(5, 3, 1000.00, FALSE),
(5, 4, 1400.00, FALSE),
-- Jovana Stanković, placeno
(6, 2, 1400.00, FALSE),
-- Luka Pavlović, na cekanju
(7, 1, 1000.00, FALSE),
(7, 3, 1000.00, FALSE),
-- Teodora Ristić, na cekanju
(8, 5, 1000.00, FALSE),
(8, 6, 1400.00, FALSE),
(8, 7, 1000.00, FALSE),
-- Petar Simić, na cekanju
(9, 6, 1400.00, FALSE),
-- Sara Todorović, na cekanju
(10, 5, 1000.00, FALSE),
-- Marko Marković, druga prijava, na cekanju
(11, 8, 1400.00, FALSE),
-- Ana Jovanović, druga prijava, otkazana u celosti
(12, 7, 1000.00, TRUE),
(12, 8, 1400.00, TRUE),
-- Milica Nikolić, druga prijava, jedan termin otkazan
(13, 5, 1000.00, FALSE),
(13, 6, 1400.00, TRUE);

-- ------------------------------------------------------------
-- 7. Uplate
--    Sve evidentirao administrator (id = 1)
-- ------------------------------------------------------------

INSERT INTO uplata (id, prijava_id, iznos, datum_evidentiranja, admin_id) VALUES
(1, 1, 2400.00, '2026-08-13 10:22:14', 1),
(2, 2, 3400.00, '2026-08-14 09:41:07', 1),
(3, 3, 1000.00, '2026-08-17 11:15:33', 1),
(4, 4, 2800.00, '2026-08-19 14:08:52', 1),
(5, 5, 2400.00, '2026-08-21 08:37:19', 1),
(6, 6, 1400.00, '2026-08-24 16:02:45', 1);

-- ------------------------------------------------------------
-- 8. Provera
-- ------------------------------------------------------------

SELECT 'korisnika' AS tabela, COUNT(*) AS broj FROM korisnik
UNION ALL SELECT 'termina',    COUNT(*) FROM termin
UNION ALL SELECT 'kataloga',   COUNT(*) FROM katalog
UNION ALL SELECT 'stavki kataloga', COUNT(*) FROM stavka_kataloga
UNION ALL SELECT 'prijava',    COUNT(*) FROM prijava
UNION ALL SELECT 'stavki prijava',  COUNT(*) FROM prijava_termin
UNION ALL SELECT 'uplata',     COUNT(*) FROM uplata;
