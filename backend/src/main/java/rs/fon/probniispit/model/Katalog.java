package rs.fon.probniispit.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "katalog")
public class Katalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "naziv", nullable = false, length = 150)
    private String naziv;

    @Column(name = "vazi_od", nullable = false)
    private LocalDate vaziOd;

    @OneToMany(mappedBy = "katalog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StavkaKataloga> stavke = new ArrayList<>();

    public Katalog() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public LocalDate getVaziOd() {
        return vaziOd;
    }

    public void setVaziOd(LocalDate vaziOd) {
        this.vaziOd = vaziOd;
    }

    public List<StavkaKataloga> getStavke() {
        return stavke;
    }

    public void setStavke(List<StavkaKataloga> stavke) {
        this.stavke = stavke;
    }

    public void dodajStavku(StavkaKataloga stavka) {
        stavke.add(stavka);
        stavka.setKatalog(this);
    }
}