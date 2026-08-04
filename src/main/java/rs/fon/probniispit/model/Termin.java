package rs.fon.probniispit.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "termin")
public class Termin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "datum", nullable = false)
    private LocalDate datum;

    @Column(name = "vreme_pocetka", nullable = false)
    private LocalTime vremePocetka;

    @Enumerated(EnumType.STRING)
    @Column(name = "vrsta_ispita", nullable = false)
    private VrstaIspita vrstaIspita;

    @Column(name = "adresa", nullable = false)
    private String adresa;

    public Termin() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public LocalTime getVremePocetka() {
        return vremePocetka;
    }

    public void setVremePocetka(LocalTime vremePocetka) {
        this.vremePocetka = vremePocetka;
    }

    public VrstaIspita getVrstaIspita() {
        return vrstaIspita;
    }

    public void setVrstaIspita(VrstaIspita vrstaIspita) {
        this.vrstaIspita = vrstaIspita;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }
}