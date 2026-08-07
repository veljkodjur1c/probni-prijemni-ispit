package rs.fon.probniispit.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record StranicaDTO<T>(
        List<T> sadrzaj,
        int brojStranice,
        int velicinaStranice,
        long ukupnoElemenata,
        int ukupnoStranica,
        boolean prva,
        boolean poslednja
) {
    public static <E, D> StranicaDTO<D> od(Page<E> stranica, Function<E, D> mapper) {
        return new StranicaDTO<>(
                stranica.getContent().stream().map(mapper).toList(),
                stranica.getNumber(),
                stranica.getSize(),
                stranica.getTotalElements(),
                stranica.getTotalPages(),
                stranica.isFirst(),
                stranica.isLast()
        );
    }
}