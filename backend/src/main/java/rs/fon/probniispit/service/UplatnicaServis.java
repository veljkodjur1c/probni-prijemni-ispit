package rs.fon.probniispit.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.probniispit.exception.NijePronadjenoException;
import rs.fon.probniispit.model.Korisnik;
import rs.fon.probniispit.model.Prijava;
import rs.fon.probniispit.model.PrijavaTermin;
import rs.fon.probniispit.model.Termin;
import rs.fon.probniispit.repository.PrijavaRepository;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class UplatnicaServis {

    private static final DateTimeFormatter DATUM = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
    private static final DateTimeFormatter VREME = DateTimeFormatter.ofPattern("HH:mm");

    private final PrijavaRepository prijavaRepository;

    public UplatnicaServis(PrijavaRepository prijavaRepository) {
        this.prijavaRepository = prijavaRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generisi(Integer prijavaId) {
        Prijava prijava = prijavaRepository.findById(prijavaId)
                .orElseThrow(() -> new NijePronadjenoException("Prijava ne postoji"));
        return napravi(prijava);
    }

    @Transactional(readOnly = true)
    public byte[] generisiZaKandidata(Integer prijavaId, Integer korisnikId) {
        Prijava prijava = prijavaRepository.findByIdAndKorisnikId(prijavaId, korisnikId)
                .orElseThrow(() -> new NijePronadjenoException("Prijava ne postoji"));
        return napravi(prijava);
    }

    public byte[] napravi(Prijava prijava) {
        ByteArrayOutputStream izlaz = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(izlaz);
             PdfDocument pdf = new PdfDocument(writer);
             Document dokument = new Document(pdf)) {

            Korisnik kandidat = prijava.getKorisnik();

            dokument.add(new Paragraph("Fakultet organizacionih nauka")
                    .setFontSize(16)
                    .setBold());

            dokument.add(new Paragraph("Uplatnica za probni prijemni ispit")
                    .setFontSize(12)
                    .setMarginBottom(20));

            dokument.add(redPodatka("Broj prijave:", "#" + prijava.getId()));
            dokument.add(redPodatka("Datum prijave:",
                    prijava.getDatumPrijave().format(DATUM)));
            dokument.add(redPodatka("Kandidat:",
                    kandidat.getIme() + " " + kandidat.getPrezime()));
            dokument.add(redPodatka("Email:", kandidat.getEmail()));

            dokument.add(new Paragraph("Prijavljeni termini")
                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(8));

            Table tabela = new Table(UnitValue.createPercentArray(new float[]{3, 2, 4, 3}))
                    .useAllAvailableWidth();

            tabela.addHeaderCell(zaglavlje("Datum"));
            tabela.addHeaderCell(zaglavlje("Vreme"));
            tabela.addHeaderCell(zaglavlje("Vrsta ispita"));
            tabela.addHeaderCell(zaglavljeDesno("Cena"));

            for (PrijavaTermin stavka : prijava.getStavke()) {
                Termin t = stavka.getTermin();
                tabela.addCell(celija(t.getDatum().format(DATUM)));
                tabela.addCell(celija(t.getVremePocetka().format(VREME)));
                tabela.addCell(celija(nazivVrste(t.getVrstaIspita().name())));
                tabela.addCell(celijaDesno(stavka.getCenaUTrenutkuPrijave() + " RSD"));
            }

            dokument.add(tabela);

            dokument.add(new Paragraph("Ukupno za uplatu: " + prijava.getUkupnaCena() + " RSD")
                    .setBold()
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(12));

            dokument.add(new Paragraph("Podaci za uplatu")
                    .setBold()
                    .setMarginTop(24)
                    .setMarginBottom(8));

            dokument.add(redPodatka("Primalac:",
                    "Fakultet organizacionih nauka, Jove Ilica 154, Beograd"));
            dokument.add(redPodatka("Racun primaoca:", "840-1234567-89"));
            dokument.add(redPodatka("Svrha uplate:",
                    "Probni prijemni ispit - prijava br. " + prijava.getId()));
            dokument.add(redPodatka("Poziv na broj:", String.valueOf(prijava.getId())));

            dokument.add(new Paragraph(
                    "Nakon izvrsene uplate i njenog evidentiranja od strane administratora, "
                    + "vasa prijava dobija status Prijavljen.")
                    .setFontSize(9)
                    .setMarginTop(24));

        } catch (Exception e) {
            throw new RuntimeException("Greska pri generisanju uplatnice", e);
        }

        return izlaz.toByteArray();
    }

    private Paragraph redPodatka(String naziv, String vrednost) {
        return new Paragraph()
                .add(new Paragraph(naziv).setBold().setMarginBottom(0))
                .add(" " + vrednost)
                .setMarginBottom(2)
                .setFontSize(10);
    }

    private Cell zaglavlje(String tekst) {
        return new Cell()
                .add(new Paragraph(tekst).setBold().setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(0.5f));
    }

    private Cell zaglavljeDesno(String tekst) {
        return zaglavlje(tekst).setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell celija(String tekst) {
        return new Cell()
                .add(new Paragraph(tekst).setFontSize(10))
                .setBorder(Border.NO_BORDER);
    }

    private Cell celijaDesno(String tekst) {
        return celija(tekst).setTextAlignment(TextAlignment.RIGHT);
    }

    private String nazivVrste(String vrsta) {
        return vrsta.equals("MATEMATIKA") ? "Matematika" : "Test opste informisanosti";
    }
}