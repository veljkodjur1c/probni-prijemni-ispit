package rs.fon.probniispit.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "stavka_kataloga")
public class StavkaKataloga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "katalog_id", nullable = false)
    private Katalog katalog;

    @Enumerated(EnumType.STRING)
    @Column(name = "vrsta_ispita", nullable = false)
    private VrstaIspita vrstaIspita;

    @Column(name = "cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal cena;

    public StavkaKataloga() {
    }

    public StavkaKataloga(VrstaIspita vrstaIspita, BigDecimal cena) {
        this.vrstaIspita = vrstaIspita;
        this.cena = cena;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Katalog getKatalog() {
        return katalog;
    }

    public void setKatalog(Katalog katalog) {
        this.katalog = katalog;
    }

    public VrstaIspita getVrstaIspita() {
        return vrstaIspita;
    }

    public void setVrstaIspita(VrstaIspita vrstaIspita) {
        this.vrstaIspita = vrstaIspita;
    }

    public BigDecimal getCena() {
        return cena;
    }

    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }
}