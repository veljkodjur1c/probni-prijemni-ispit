package rs.fon.probniispit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.probniispit.dto.StatistikaDTO;
import rs.fon.probniispit.dto.StatistikaDTO.StavkaStatistike;
import rs.fon.probniispit.model.Prijava;
import rs.fon.probniispit.model.PrijavaTermin;
import rs.fon.probniispit.model.StatusPrijave;
import rs.fon.probniispit.model.Termin;
import rs.fon.probniispit.model.VrstaIspita;
import rs.fon.probniispit.repository.PrijavaRepository;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatistikaServis {

    private static final DateTimeFormatter DATUM = DateTimeFormatter.ofPattern("dd.MM.");

    private final PrijavaRepository prijavaRepository;

    public StatistikaServis(PrijavaRepository prijavaRepository) {
        this.prijavaRepository = prijavaRepository;
    }

    @Transactional(readOnly = true)
    public StatistikaDTO statistika() {
        List<Prijava> sve = prijavaRepository.findAll();

        long naCekanju = sve.stream()
                .filter(p -> p.getStatus() == StatusPrijave.NA_CEKANJU).count();
        long prijavljenih = sve.stream()
                .filter(p -> p.getStatus() == StatusPrijave.PRIJAVLJEN).count();
        long otkazanih = sve.stream()
                .filter(p -> p.getStatus() == StatusPrijave.OTKAZANA).count();

        BigDecimal naplaceno = sve.stream()
                .filter(p -> p.getStatus() == StatusPrijave.PRIJAVLJEN)
                .map(Prijava::getUkupnaCena)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ocekivano = sve.stream()
                .filter(p -> p.getStatus() == StatusPrijave.NA_CEKANJU)
                .map(Prijava::getUkupnaCena)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new StatistikaDTO(
                sve.size(),
                naCekanju,
                prijavljenih,
                otkazanih,
                naplaceno,
                ocekivano,
                poTerminima(sve),
                poVrstiIspita(sve)
        );
    }

    private List<StavkaStatistike> poTerminima(List<Prijava> sve) {
        Map<Integer, long[]> brojac = new LinkedHashMap<>();
        Map<Integer, String> nazivi = new LinkedHashMap<>();
        Map<Integer, BigDecimal> iznosi = new LinkedHashMap<>();

        for (Prijava p : sve) {
            if (p.getStatus() == StatusPrijave.OTKAZANA) continue;

            for (PrijavaTermin s : p.getStavke()) {
                Termin t = s.getTermin();
                Integer id = t.getId();

                nazivi.putIfAbsent(id, t.getDatum().format(DATUM) + " "
                        + t.getVremePocetka().toString().substring(0, 5));
                brojac.computeIfAbsent(id, k -> new long[1])[0]++;
                iznosi.merge(id, s.getCenaUTrenutkuPrijave(), BigDecimal::add);
            }
        }

        List<StavkaStatistike> rezultat = new ArrayList<>();
        nazivi.forEach((id, naziv) -> rezultat.add(new StavkaStatistike(
                naziv, brojac.get(id)[0], iznosi.get(id))));

        return rezultat;
    }

    private List<StavkaStatistike> poVrstiIspita(List<Prijava> sve) {
        Map<VrstaIspita, long[]> brojac = new LinkedHashMap<>();
        Map<VrstaIspita, BigDecimal> iznosi = new LinkedHashMap<>();

        for (Prijava p : sve) {
            if (p.getStatus() == StatusPrijave.OTKAZANA) continue;

            for (PrijavaTermin s : p.getStavke()) {
                VrstaIspita vrsta = s.getTermin().getVrstaIspita();
                brojac.computeIfAbsent(vrsta, k -> new long[1])[0]++;
                iznosi.merge(vrsta, s.getCenaUTrenutkuPrijave(), BigDecimal::add);
            }
        }

        List<StavkaStatistike> rezultat = new ArrayList<>();
        brojac.forEach((vrsta, broj) -> rezultat.add(new StavkaStatistike(
                vrsta == VrstaIspita.MATEMATIKA ? "Matematika" : "Opšta informisanost",
                broj[0],
                iznosi.get(vrsta))));

        return rezultat;
    }
}