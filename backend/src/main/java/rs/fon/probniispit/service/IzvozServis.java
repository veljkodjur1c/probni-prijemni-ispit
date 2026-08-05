package rs.fon.probniispit.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.probniispit.exception.NijePronadjenoException;
import rs.fon.probniispit.model.*;
import rs.fon.probniispit.repository.PrijavaRepository;
import rs.fon.probniispit.repository.TerminRepository;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class IzvozServis {

    private static final DateTimeFormatter DATUM = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
    private static final DateTimeFormatter VREME = DateTimeFormatter.ofPattern("HH:mm");

    private final TerminRepository terminRepository;
    private final PrijavaRepository prijavaRepository;

    public IzvozServis(TerminRepository terminRepository,
                       PrijavaRepository prijavaRepository) {
        this.terminRepository = terminRepository;
        this.prijavaRepository = prijavaRepository;
    }

    @Transactional(readOnly = true)
    public byte[] spisakKandidata(Integer terminId) {
        Termin termin = terminRepository.findById(terminId)
                .orElseThrow(() -> new NijePronadjenoException("Termin ne postoji"));

        List<Prijava> prijave = prijavaRepository.findByStatus(StatusPrijave.PRIJAVLJEN)
                .stream()
                .filter(p -> p.getStavke().stream()
                        .anyMatch(s -> s.getTermin().getId().equals(terminId)))
                .sorted(Comparator.comparing(p -> p.getKorisnik().getPrezime()))
                .toList();

        try (Workbook radnaSveska = new XSSFWorkbook();
             ByteArrayOutputStream izlaz = new ByteArrayOutputStream()) {

            Sheet list = radnaSveska.createSheet("Spisak kandidata");

            CellStyle stilNaslova = radnaSveska.createCellStyle();
            Font fontNaslova = radnaSveska.createFont();
            fontNaslova.setBold(true);
            fontNaslova.setFontHeightInPoints((short) 13);
            stilNaslova.setFont(fontNaslova);

            CellStyle stilZaglavlja = radnaSveska.createCellStyle();
            Font fontZaglavlja = radnaSveska.createFont();
            fontZaglavlja.setBold(true);
            stilZaglavlja.setFont(fontZaglavlja);
            stilZaglavlja.setBorderBottom(BorderStyle.THIN);

            int red = 0;

            Row naslov = list.createRow(red++);
            Cell celijaNaslova = naslov.createCell(0);
            celijaNaslova.setCellValue("Spisak prijavljenih kandidata");
            celijaNaslova.setCellStyle(stilNaslova);

            red++;

            list.createRow(red++).createCell(0).setCellValue(
                    "Termin: " + termin.getDatum().format(DATUM)
                    + " u " + termin.getVremePocetka().format(VREME));

            list.createRow(red++).createCell(0).setCellValue(
                    "Vrsta ispita: " + nazivVrste(termin.getVrstaIspita()));

            list.createRow(red++).createCell(0).setCellValue(
                    "Adresa: " + termin.getAdresa());

            list.createRow(red++).createCell(0).setCellValue(
                    "Broj kandidata: " + prijave.size());

            red++;

            Row zaglavlje = list.createRow(red++);
            String[] kolone = {"Rb.", "Prezime", "Ime", "Email", "Broj prijave"};
            for (int i = 0; i < kolone.length; i++) {
                Cell celija = zaglavlje.createCell(i);
                celija.setCellValue(kolone[i]);
                celija.setCellStyle(stilZaglavlja);
            }

            int redniBroj = 1;
            for (Prijava prijava : prijave) {
                Korisnik kandidat = prijava.getKorisnik();
                Row vrsta = list.createRow(red++);
                vrsta.createCell(0).setCellValue(redniBroj++);
                vrsta.createCell(1).setCellValue(kandidat.getPrezime());
                vrsta.createCell(2).setCellValue(kandidat.getIme());
                vrsta.createCell(3).setCellValue(kandidat.getEmail());
                vrsta.createCell(4).setCellValue(prijava.getId());
            }

            for (int i = 0; i < kolone.length; i++) {
                list.autoSizeColumn(i);
            }

            radnaSveska.write(izlaz);
            return izlaz.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Greška pri izvozu spiska", e);
        }
    }

    private String nazivVrste(VrstaIspita vrsta) {
        return vrsta == VrstaIspita.MATEMATIKA
                ? "Matematika"
                : "Test opšte informisanosti";
    }
}