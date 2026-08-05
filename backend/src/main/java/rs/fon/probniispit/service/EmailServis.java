package rs.fon.probniispit.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rs.fon.probniispit.model.Korisnik;
import rs.fon.probniispit.model.Prijava;

import java.time.format.DateTimeFormatter;

@Service
public class EmailServis {

    private static final Logger log = LoggerFactory.getLogger(EmailServis.class);
    private static final DateTimeFormatter DATUM = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    private final JavaMailSender posiljalac;
    private final String adresaPosiljaoca;
    private final boolean ukljucen;

    public EmailServis(JavaMailSender posiljalac,
                       @Value("${app.mail.posiljalac}") String adresaPosiljaoca,
                       @Value("${app.mail.ukljucen}") boolean ukljucen) {
        this.posiljalac = posiljalac;
        this.adresaPosiljaoca = adresaPosiljaoca;
        this.ukljucen = ukljucen;
    }

    @Async("mailExecutor")
    public void posaljiUplatnicu(Prijava prijava, byte[] uplatnica) {
        if (!ukljucen) {
            log.info("Slanje email-a je iskljuceno, preskacem prijavu #{}", prijava.getId());
            return;
        }

        Korisnik kandidat = prijava.getKorisnik();

        try {
            MimeMessage poruka = posiljalac.createMimeMessage();
            MimeMessageHelper pomocnik = new MimeMessageHelper(poruka, true, "UTF-8");

            pomocnik.setFrom(adresaPosiljaoca);
            pomocnik.setTo(kandidat.getEmail());
            pomocnik.setSubject("Potvrda prijave za probni prijemni ispit");
            pomocnik.setText(sadrzaj(prijava), true);
            pomocnik.addAttachment(
                    "uplatnica-" + prijava.getId() + ".pdf",
                    new ByteArrayResource(uplatnica));

            posiljalac.send(poruka);

            log.info("Uplatnica za prijavu #{} poslata na {}",
                    prijava.getId(), kandidat.getEmail());

        } catch (Exception e) {
            log.error("Neuspelo slanje uplatnice za prijavu #{}: {}",
                    prijava.getId(), e.getMessage());
        }
    }

    private String sadrzaj(Prijava prijava) {
        Korisnik k = prijava.getKorisnik();

        StringBuilder terminiHtml = new StringBuilder();
        prijava.getStavke().forEach(s -> terminiHtml.append("<li>")
                .append(s.getTermin().getDatum().format(DATUM))
                .append(" u ")
                .append(s.getTermin().getVremePocetka())
                .append(" — ")
                .append(s.getTermin().getVrstaIspita() == rs.fon.probniispit.model.VrstaIspita.MATEMATIKA
                        ? "Matematika" : "Test opšte informisanosti")
                .append("</li>"));

        return """
                <p>Poštovani/a %s %s,</p>
                <p>Vaša prijava broj <strong>#%d</strong> je uspešno evidentirana.</p>
                <p>Prijavljeni termini:</p>
                <ul>%s</ul>
                <p>Ukupan iznos za uplatu: <strong>%s RSD</strong></p>
                <p>Uplatnicu sa svim podacima nalazite u prilogu ovog email-a.
                Nakon izvršene uplate i njenog evidentiranja, vaša prijava
                dobija status „Prijavljen".</p>
                <p>Fakultet organizacionih nauka</p>
                """.formatted(
                k.getIme(), k.getPrezime(),
                prijava.getId(),
                terminiHtml.toString(),
                prijava.getUkupnaCena());
    }
}