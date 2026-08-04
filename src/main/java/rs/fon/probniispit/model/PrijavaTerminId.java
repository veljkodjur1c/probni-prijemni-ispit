package rs.fon.probniispit.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PrijavaTerminId implements Serializable {

    @Column(name = "prijava_id")
    private Integer prijavaId;

    @Column(name = "termin_id")
    private Integer terminId;

    public PrijavaTerminId() {
    }

    public PrijavaTerminId(Integer prijavaId, Integer terminId) {
        this.prijavaId = prijavaId;
        this.terminId = terminId;
    }

    public Integer getPrijavaId() {
        return prijavaId;
    }

    public void setPrijavaId(Integer prijavaId) {
        this.prijavaId = prijavaId;
    }

    public Integer getTerminId() {
        return terminId;
    }

    public void setTerminId(Integer terminId) {
        this.terminId = terminId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrijavaTerminId that = (PrijavaTerminId) o;
        return Objects.equals(prijavaId, that.prijavaId)
                && Objects.equals(terminId, that.terminId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prijavaId, terminId);
    }
}