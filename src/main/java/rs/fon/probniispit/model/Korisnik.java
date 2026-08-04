package rs.fon.probniispit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "korisnik")
public class Korisnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ime", nullable = false, length = 100)
    private String ime;

    @Column(name = "prezime", nullable = false, length = 100)
    private String prezime;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "lozinka_hash", nullable = false)
    private String lozinkaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "uloga", nullable = false)
    private Uloga uloga;

    public Korisnik() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLozinkaHash() {
        return lozinkaHash;
    }

    public void setLozinkaHash(String lozinkaHash) {
        this.lozinkaHash = lozinkaHash;
    }

    public Uloga getUloga() {
        return uloga;
    }

    public void setUloga(Uloga uloga) {
        this.uloga = uloga;
    }
}
