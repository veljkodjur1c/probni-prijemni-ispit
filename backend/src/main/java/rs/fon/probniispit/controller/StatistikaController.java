package rs.fon.probniispit.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.probniispit.dto.StatistikaDTO;
import rs.fon.probniispit.service.StatistikaServis;

@RestController
@RequestMapping("/api/statistika")
@PreAuthorize("hasRole('ADMIN')")
public class StatistikaController {

    private final StatistikaServis statistikaServis;

    public StatistikaController(StatistikaServis statistikaServis) {
        this.statistikaServis = statistikaServis;
    }

    @GetMapping
    public StatistikaDTO statistika() {
        return statistikaServis.statistika();
    }
}