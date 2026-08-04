package rs.fon.probniispit.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cenovnik")
public class Cenovnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "vrsta_ispita", nullable = false)
    private VrstaIspita vrstaIspita;

    @Column(name = "cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal cena;

    @Column(name = "vazi_od", nullable = false)
    private LocalDate vaziOd;

    public Cenovnik() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDate getVaziOd() {
        return vaziOd;
    }

    public void setVaziOd(LocalDate vaziOd) {
        this.vaziOd = vaziOd;
    }
}