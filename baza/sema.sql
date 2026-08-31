-- ============================================================
--  Probni prijemni ispit — sema baze podataka
--  Pokrenuti u SQLyog-u sa Ctrl+F9 (izvrsi sve upite)
-- ============================================================

CREATE DATABASE IF NOT EXISTS probni_ispit
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE probni_ispit;

-- ------------------------------------------------------------
--  korisnik
--  Kandidati i administratori, razlikuju se ulogom.
--  Lozinka se cuva iskljucivo kao BCrypt hes.
-- ------------------------------------------------------------

CREATE TABLE korisnik (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    ime          VARCHAR(100) NOT NULL,
    prezime      VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    lozinka_hash VARCHAR(255) NOT NULL,
    uloga        ENUM('KANDIDAT', 'ADMIN') NOT NULL DEFAULT 'KANDIDAT'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
--  termin
--  Termin odrzavanja probnog prijemnog ispita.
-- ------------------------------------------------------------

CREATE TABLE termin (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    datum         DATE NOT NULL,
    vreme_pocetka TIME NOT NULL,
    vrsta_ispita  ENUM('MATEMATIKA', 'OPSTA_INFORMISANOST') NOT NULL,
    adresa        VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
--  katalog
--  Cenovnik kao celina, sa datumom od koga stupa na snagu.
--  Postojeci katalozi se ne menjaju — nova cena znaci nov katalog,
--  cime se cuva istorija.
-- ------------------------------------------------------------

CREATE TABLE katalog (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    naziv   VARCHAR(150) NOT NULL,
    vazi_od DATE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
--  stavka_kataloga
--  Jedna cena unutar kataloga, vezana za vrstu ispita.
--  Jedinstveno ogranicenje sprecava dve cene za istu vrstu
--  ispita u istom katalogu.
-- ------------------------------------------------------------

CREATE TABLE stavka_kataloga (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    katalog_id   INT NOT NULL,
    vrsta_ispita ENUM('MATEMATIKA', 'OPSTA_INFORMISANOST') NOT NULL,
    cena         DECIMAL(10,2) NOT NULL,
    UNIQUE KEY uq_stavka_kataloga (katalog_id, vrsta_ispita),
    FOREIGN KEY (katalog_id) REFERENCES katalog(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
--  prijava
--  Prijava kandidata na jedan ili vise termina.
--  Ukupna cena je zbir cena neotkazanih stavki.
-- ------------------------------------------------------------

CREATE TABLE prijava (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    korisnik_id   INT NOT NULL,
    datum_prijave TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ukupna_cena   DECIMAL(10,2) NOT NULL,
    status        ENUM('NA_CEKANJU', 'PRIJAVLJEN', 'OTKAZANA')
                  NOT NULL DEFAULT 'NA_CEKANJU',
    FOREIGN KEY (korisnik_id) REFERENCES korisnik(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
--  prijava_termin
--  Spojna tabela veze vise-prema-vise izmedju prijave i termina.
--  Nosi sopstveni podatak — cenu po kojoj je termin naplacen u
--  trenutku podnosenja prijave — pa je modelovana kao poseban
--  entitet, a ne kao prosta @ManyToMany veza.
--  Slozeni primarni kljuc sprecava da isti termin bude dva puta
--  izabran u okviru iste prijave.
-- ------------------------------------------------------------

CREATE TABLE prijava_termin (
    prijava_id              INT NOT NULL,
    termin_id               INT NOT NULL,
    cena_u_trenutku_prijave DECIMAL(10,2) NOT NULL,
    otkazana                BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (prijava_id, termin_id),
    FOREIGN KEY (prijava_id) REFERENCES prijava(id) ON DELETE CASCADE,
    FOREIGN KEY (termin_id)  REFERENCES termin(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
--  uplata
--  Evidentirana uplata i administrator koji ju je uneo.
--  Jedinstveno ogranicenje nad prijava_id obezbedjuje odnos 1:1.
-- ------------------------------------------------------------

CREATE TABLE uplata (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    prijava_id          INT NOT NULL UNIQUE,
    iznos               DECIMAL(10,2) NOT NULL,
    datum_evidentiranja TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    admin_id            INT NOT NULL,
    FOREIGN KEY (prijava_id) REFERENCES prijava(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id)   REFERENCES korisnik(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
--  Pocetni podaci
--
--  Administrator se ne kreira ovde, jer bi mu lozinka morala biti
--  unapred izracunat BCrypt hes. Postupak:
--    1. registrovati se kroz aplikaciju
--    2. UPDATE korisnik SET uloga = 'ADMIN' WHERE email = '...';
--    3. odjaviti se i ponovo prijaviti
--
--  Za popunjenu bazu sa nalozima i prijavama pokrenuti seed.sql.
-- ------------------------------------------------------------

INSERT INTO katalog (naziv, vazi_od) VALUES
    ('Cenovnik 2026', '2026-01-01');

INSERT INTO stavka_kataloga (katalog_id, vrsta_ispita, cena) VALUES
    (1, 'MATEMATIKA',          1000.00),
    (1, 'OPSTA_INFORMISANOST', 1400.00);

INSERT INTO termin (datum, vreme_pocetka, vrsta_ispita, adresa) VALUES
    ('2026-09-19', '10:00:00', 'MATEMATIKA',          'Jove Ilića 154, Beograd'),
    ('2026-09-19', '13:00:00', 'OPSTA_INFORMISANOST', 'Jove Ilića 154, Beograd'),
    ('2026-09-26', '10:00:00', 'MATEMATIKA',          'Jove Ilića 154, Beograd'),
    ('2026-09-26', '13:00:00', 'OPSTA_INFORMISANOST', 'Jove Ilića 154, Beograd');
