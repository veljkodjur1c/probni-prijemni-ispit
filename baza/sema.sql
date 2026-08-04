CREATE DATABASE IF NOT EXISTS probni_ispit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE probni_ispit;

CREATE TABLE korisnik (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ime VARCHAR(100) NOT NULL,
    prezime VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    lozinka_hash VARCHAR(255) NOT NULL,
    uloga ENUM('KANDIDAT', 'ADMIN') NOT NULL DEFAULT 'KANDIDAT'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE termin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    datum DATE NOT NULL,
    vreme_pocetka TIME NOT NULL,
    vrsta_ispita ENUM('MATEMATIKA', 'OPSTA_INFORMISANOST') NOT NULL,
    adresa VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cenovnik (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vrsta_ispita ENUM('MATEMATIKA', 'OPSTA_INFORMISANOST') NOT NULL,
    cena DECIMAL(10,2) NOT NULL,
    vazi_od DATE NOT NULL,
    UNIQUE KEY uq_cenovnik (vrsta_ispita, vazi_od)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE prijava (
    id INT AUTO_INCREMENT PRIMARY KEY,
    korisnik_id INT NOT NULL,
    datum_prijave TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ukupna_cena DECIMAL(10,2) NOT NULL,
    status ENUM('NA_CEKANJU', 'PRIJAVLJEN', 'OTKAZANA') NOT NULL DEFAULT 'NA_CEKANJU',
    FOREIGN KEY (korisnik_id) REFERENCES korisnik(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE prijava_termin (
    prijava_id INT NOT NULL,
    termin_id INT NOT NULL,
    cena_u_trenutku_prijave DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (prijava_id, termin_id),
    FOREIGN KEY (prijava_id) REFERENCES prijava(id) ON DELETE CASCADE,
    FOREIGN KEY (termin_id) REFERENCES termin(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE uplata (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prijava_id INT NOT NULL UNIQUE,
    iznos DECIMAL(10,2) NOT NULL,
    datum_evidentiranja TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    admin_id INT NOT NULL,
    FOREIGN KEY (prijava_id) REFERENCES prijava(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES korisnik(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO cenovnik (vrsta_ispita, cena, vazi_od) VALUES
    ('MATEMATIKA', 1500.00, '2026-01-01'),
    ('OPSTA_INFORMISANOST', 1200.00, '2026-01-01');

INSERT INTO termin (datum, vreme_pocetka, vrsta_ispita, adresa) VALUES
    ('2026-09-12', '10:00:00', 'MATEMATIKA', 'Jove Ilića 154, Beograd'),
    ('2026-09-12', '13:00:00', 'OPSTA_INFORMISANOST', 'Jove Ilića 154, Beograd'),
    ('2026-09-19', '10:00:00', 'MATEMATIKA', 'Jove Ilića 154, Beograd'),
    ('2026-09-19', '13:00:00', 'OPSTA_INFORMISANOST', 'Jove Ilića 154, Beograd');