package rs.fon.probniispit.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prijava")
public class Prijava {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "korisnik_id", nullable = false)
    private Korisnik korisnik;

    @Column(name = "datum_prijave", nullable = false)
    private LocalDateTime datumPrijave;

    @Column(name = "ukupna_cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal ukupnaCena;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPrijave status;

    @OneToMany(mappedBy = "prijava", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrijavaTermin> stavke = new ArrayList<>();

    public Prijava() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }

    public LocalDateTime getDatumPrijave() {
        return datumPrijave;
    }

    public void setDatumPrijave(LocalDateTime datumPrijave) {
        this.datumPrijave = datumPrijave;
    }

    public BigDecimal getUkupnaCena() {
        return ukupnaCena;
    }

    public void setUkupnaCena(BigDecimal ukupnaCena) {
        this.ukupnaCena = ukupnaCena;
    }

    public StatusPrijave getStatus() {
        return status;
    }

    public void setStatus(StatusPrijave status) {
        this.status = status;
    }

    public List<PrijavaTermin> getStavke() {
        return stavke;
    }

    public void setStavke(List<PrijavaTermin> stavke) {
        this.stavke = stavke;
    }

    public void dodajStavku(PrijavaTermin stavka) {
        stavke.add(stavka);
        stavka.setPrijava(this);
    }
}