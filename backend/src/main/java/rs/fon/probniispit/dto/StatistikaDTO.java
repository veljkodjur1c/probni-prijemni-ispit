package rs.fon.probniispit.dto;

import java.math.BigDecimal;
import java.util.List;

public record StatistikaDTO(
        long ukupnoPrijava,
        long naCekanju,
        long prijavljenih,
        long otkazanih,
        BigDecimal ukupanPrihod,
        BigDecimal ocekivanPrihod,
        List<StavkaStatistike> poTerminima,
        List<StavkaStatistike> poVrstiIspita
) {
    public record StavkaStatistike(String naziv, long broj, BigDecimal iznos) {}
}