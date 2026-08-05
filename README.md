# Probni prijemni ispit — veb aplikacija

Aplikacija za elektronsku prijavu kandidata na probne prijemne ispite iz matematike i testa opšte informisanosti.

Realizovana kao višeslojna klijent-server aplikacija sa potpuno odvojenim klijentskim i serverskim delom, koji komuniciraju preko REST servisa.

---

## Tehnologije

**Backend**
- Java 21
- Spring Boot 4.0.7
- Spring Data JPA / Hibernate
- Spring Security + JWT
- Maven
- MySQL / MariaDB

**Frontend**
- React 19 (Vite)
- React Router
- Axios
- Bootstrap 5

---

## Struktura projekta

```
probni-ispit/
  backend/          Spring Boot aplikacija
  frontend/         React aplikacija
  baza/
    sema.sql        Skripta za kreiranje baze
  README.md
```

---

## Preduslovi

Pre pokretanja je potrebno instalirati:

- **JDK 21** ili noviji
- **Node.js 18** ili noviji (dolazi sa npm)
- **XAMPP** (zbog MySQL/MariaDB servera) ili zaseban MySQL server
- **SQLyog** ili drugi klijent za rad sa bazom

Maven nije potrebno instalirati — projekat sadrži Maven Wrapper (`mvnw`).

---

## Pokretanje

### 1. Baza podataka

Pokrenite MySQL kroz XAMPP Control Panel (dugme **Start** pored `MySQL`).

Otvorite SQLyog i povežite se:

| Polje | Vrednost |
|---|---|
| Host | `localhost` |
| User | `root` |
| Password | *(prazno)* |
| Port | `3306` |

Otvorite novi SQL editor, učitajte sadržaj fajla `baza/sema.sql` i izvršite ga sa **Ctrl+F9** (izvrši sve upite).

Skripta kreira bazu `probni_ispit` sa šest tabela i unosi početne podatke — cenovnik i nekoliko termina.

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

Na Windows-u:

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

Aplikacija se pokreće na `http://localhost:8080`.

Ako je baza uspešno povezana, u konzoli će se pojaviti red `Started ProbniIspitApplication`.

### 3. Frontend

U zasebnom terminalu:

```bash
cd frontend
npm install
npm run dev
```

Aplikacija se pokreće na `http://localhost:5173`.

Oba dela moraju raditi istovremeno.

---

## Kreiranje administratorskog naloga

Registracija kroz aplikaciju uvek kreira nalog sa ulogom kandidata. Administrator se dobija naknadnom izmenom uloge u bazi:

1. Registrujte se kroz aplikaciju na `http://localhost:5173/registracija`
2. U SQLyog-u izvršite:

```sql
UPDATE korisnik SET uloga = 'ADMIN' WHERE email = 'vas@email.rs';
```

3. Odjavite se i ponovo prijavite — nova uloga se upisuje u token pri prijavljivanju

---

## Funkcionalnosti

### Kandidat
- Registracija i prijava na sistem
- Pregled raspoloživih termina sa cenama
- Prijava jednog ili više termina odjednom, sa prikazom ukupne cene
- Pregled sopstvenih prijava i njihovog statusa
- Otkazivanje prijave dok uplata nije evidentirana

### Administrator
- Upravljanje terminima (kreiranje, izmena, brisanje)
- Definisanje cenovnika sa datumom od kada cena važi
- Pregled svih prijava, sa filtriranjem po statusu i pretragom po kandidatu
- Evidentiranje izvršene uplate
- Pregled spiska prijavljenih kandidata po terminu
- Otkazivanje prijave dok uplata nije evidentirana

---

## Poslovna pravila

- Prijava se može otkazati isključivo dok je u statusu `NA_CEKANJU`. Nakon evidentirane uplate otkazivanje nije moguće ni kandidatu ni administratoru.
- Termin na koji postoje prijave ne može biti obrisan.
- Cena se pamti u trenutku prijave. Naknadna izmena cenovnika ne utiče na već podnete prijave.
- Isti termin ne može biti izabran više puta u okviru jedne prijave.

---

## Arhitektura

### Backend — troslojna arhitektura

| Sloj | Paket | Odgovornost |
|---|---|---|
| Prezentacioni | `controller` | REST endpointi, HTTP statusi, validacija ulaza |
| Poslovni | `service` | Poslovna logika, transakcije, pravila domena |
| Sloj pristupa podacima | `repository` | Spring Data JPA repozitorijumi |

Pomoćni paketi: `model` (JPA entiteti), `dto` (objekti za komunikaciju sa klijentom), `mapper` (konverzija entitet ↔ DTO), `exception` (sopstveni izuzeci i globalna obrada grešaka), `security` (JWT), `config` (konfiguracija).

Entiteti se nikad ne izlažu klijentu — komunikacija ide isključivo preko DTO objekata.

### Model podataka

| Tabela | Opis |
|---|---|
| `korisnik` | Kandidati i administratori, razlikovani ulogom |
| `termin` | Termin ispita (datum, vreme, vrsta, adresa) |
| `cenovnik` | Cena po vrsti ispita, sa datumom od kada važi |
| `prijava` | Prijava kandidata sa ukupnom cenom i statusom |
| `prijava_termin` | Spojna tabela sa zapamćenom cenom po stavci |
| `uplata` | Evidentirana uplata i administrator koji ju je uneo |

Veza između prijave i termina je M:N, razložena kroz spojnu tabelu koja nosi sopstveni podatak — cenu u trenutku prijave.

---

## REST API

Sve rute imaju prefiks `/api`.

### Autentifikacija

| Metoda | Putanja | Pristup |
|---|---|---|
| POST | `/auth/registracija` | javno |
| POST | `/auth/prijava` | javno |

### Termini

| Metoda | Putanja | Pristup |
|---|---|---|
| GET | `/termini` | javno |
| GET | `/termini?samoBuduci=true` | javno |
| GET | `/termini/{id}` | javno |
| POST | `/termini` | admin |
| PUT | `/termini/{id}` | admin |
| DELETE | `/termini/{id}` | admin |

### Prijave

| Metoda | Putanja | Pristup |
|---|---|---|
| POST | `/prijave` | kandidat |
| GET | `/prijave/moje` | kandidat |
| DELETE | `/prijave/{id}` | kandidat |
| GET | `/prijave?status=` | admin |
| DELETE | `/prijave/{id}/admin` | admin |

### Uplate

| Metoda | Putanja | Pristup |
|---|---|---|
| POST | `/uplate/prijava/{prijavaId}` | admin |
| GET | `/uplate/prijava/{prijavaId}` | admin |

### Cenovnik

| Metoda | Putanja | Pristup |
|---|---|---|
| GET | `/cenovnik` | javno |
| POST | `/cenovnik` | admin |

Zaštićene rute zahtevaju zaglavlje:

```
Authorization: Bearer <token>
```

---

## Obrada grešaka

Sve greške vraćaju se u jedinstvenom formatu:

```json
{
  "vreme": "2026-08-05T14:22:10",
  "status": 400,
  "poruka": "Neispravni podaci",
  "greskePolja": {
    "lozinka": "Lozinka mora imati bar 6 karaktera"
  }
}
```

| Status | Značenje |
|---|---|
| 400 | Neispravan zahtev ili neuspela validacija |
| 403 | Nedovoljna prava pristupa |
| 404 | Traženi resurs ne postoji |
| 409 | Zahtev je u konfliktu sa trenutnim stanjem |

---

## Napomene

- XAMPP isporučuje MariaDB, koja se predstavlja kao MySQL verzija 5.5.5. Upozorenje o nepodržanom dijalektu pri pokretanju je očekivano i ne utiče na rad aplikacije.
- Hibernate je podešen na `ddl-auto=validate` — šema se ne menja automatski, već se pri pokretanju proverava poklapanje entiteta sa postojećim tabelama.
- Lozinke se čuvaju kao BCrypt heš. Korisnici uneti direktno u bazu preko SQL-a ne mogu se prijaviti na sistem.
