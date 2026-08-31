package rs.fon.probniispit.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "prijava_termin")
public class PrijavaTermin {

    @EmbeddedId
    private PrijavaTerminId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("prijavaId")
    @JoinColumn(name = "prijava_id")
    private Prijava prijava;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("terminId")
    @JoinColumn(name = "termin_id")
    private Termin termin;

    @Column(name = "cena_u_trenutku_prijave", nullable = false, precision = 10, scale = 2)
    private BigDecimal cenaUTrenutkuPrijave;

    @Column(name = "otkazana", nullable = false)
    private boolean otkazana = false;

    public PrijavaTermin() {
    }

    public PrijavaTermin(Prijava prijava, Termin termin, BigDecimal cena) {
        this.prijava = prijava;
        this.termin = termin;
        this.cenaUTrenutkuPrijave = cena;
        this.id = new PrijavaTerminId();
        this.otkazana = false;
    }

    public PrijavaTerminId getId() {
        return id;
    }

    public void setId(PrijavaTerminId id) {
        this.id = id;
    }

    public Prijava getPrijava() {
        return prijava;
    }

    public void setPrijava(Prijava prijava) {
        this.prijava = prijava;
    }

    public Termin getTermin() {
        return termin;
    }

    public void setTermin(Termin termin) {
        this.termin = termin;
    }

    public BigDecimal getCenaUTrenutkuPrijave() {
        return cenaUTrenutkuPrijave;
    }

    public void setCenaUTrenutkuPrijave(BigDecimal cenaUTrenutkuPrijave) {
        this.cenaUTrenutkuPrijave = cenaUTrenutkuPrijave;
    }

    public boolean isOtkazana() {
        return otkazana;
    }

    public void setOtkazana(boolean otkazana) {
        this.otkazana = otkazana;
    }
}