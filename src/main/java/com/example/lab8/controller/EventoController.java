package com.example.lab8.controller;

import com.example.lab8.dto.RespuestaClimaDTO;
import com.example.lab8.model.Evento;
import com.example.lab8.repository.EventoRepository;
import com.example.lab8.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final EventoRepository eventoRepository;

    @GetMapping("/eventos")
    public ResponseEntity<List<Map<String, Object>>> obtenerEventos(@RequestParam String ciudad) {
        return ResponseEntity.ok(eventoService.obtenerEventos(ciudad));
    }

    @GetMapping("/clima")
    public ResponseEntity<?> obtenerClima(
            @RequestParam String ciudad,
            @RequestParam String fecha,
            @RequestParam(required = false, defaultValue = "Evento sin nombre") String match) {

        LocalDate fechaEvento = LocalDate.parse(fecha);
        RespuestaClimaDTO resultado = eventoService.obtenerClimaAvanzado(ciudad, fechaEvento, match);
        if (resultado != null) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontró pronóstico para esa fecha"));
        }
    }


    @PostMapping("/eventos")
    public ResponseEntity<?> registrarEvento(@RequestBody Evento evento) {
        Evento resultado = eventoService.registrarEventoConClima(evento);
        if (resultado != null) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No se pudo obtener el clima para esa fecha y ciudad"));
        }
    }

}
