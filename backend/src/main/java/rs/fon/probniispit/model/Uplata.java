package rs.fon.probniispit.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "uplata")
public class Uplata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prijava_id", nullable = false, unique = true)
    private Prijava prijava;

    @Column(name = "iznos", nullable = false, precision = 10, scale = 2)
    private BigDecimal iznos;

    @Column(name = "datum_evidentiranja", nullable = false)
    private LocalDateTime datumEvidentiranja;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private Korisnik admin;

    public Uplata() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Prijava getPrijava() {
        return prijava;
    }

    public void setPrijava(Prijava prijava) {
        this.prijava = prijava;
    }

    public BigDecimal getIznos() {
        return iznos;
    }

    public void setIznos(BigDecimal iznos) {
        this.iznos = iznos;
    }

    public LocalDateTime getDatumEvidentiranja() {
        return datumEvidentiranja;
    }

    public void setDatumEvidentiranja(LocalDateTime datumEvidentiranja) {
        this.datumEvidentiranja = datumEvidentiranja;
    }

    public Korisnik getAdmin() {
        return admin;
    }

    public void setAdmin(Korisnik admin) {
        this.admin = admin;
    }
}