package com.example.lab8.service;

import com.example.lab8.dto.RespuestaClimaDTO;
import com.example.lab8.model.Evento;
import com.example.lab8.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class EventoService {

    private final String API_KEY = "88e12060abad41ab97212738250906";
    private final RestTemplate restTemplate = new RestTemplate();
    private final EventoRepository eventoRepository;

    public List<Map<String, Object>> obtenerEventos(String ciudad) {
        String url = "https://api.weatherapi.com/v1/sports.json?key=" + API_KEY + "&q=" + ciudad;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> eventos = (List<Map<String, Object>>) response.get("football");
        if (eventos == null) eventos = new ArrayList<>();

        LocalDate hoy = LocalDate.now();
        LocalDate maxFecha = hoy.plusDays(7);

        return eventos.stream()
                .filter(ev -> {
                    Object fechaObj = ev.get("date");
                    if (fechaObj == null) return false; // si no hay fecha, se descarta

                    LocalDate fecha = LocalDate.parse(fechaObj.toString());
                    return !fecha.isBefore(hoy) && !fecha.isAfter(maxFecha);
                })
                .collect(Collectors.toList());
    }
    public RespuestaClimaDTO obtenerClimaAvanzado(String ciudad, LocalDate fecha, String partido) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + API_KEY + "&q=" + ciudad + "&days=7";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        Map<String, Object> forecast = (Map<String, Object>) response.get("forecast");
        List<Map<String, Object>> dias = (List<Map<String, Object>>) forecast.get("forecastday");

        for (Map<String, Object> dia : dias) {
            if (dia.get("date").equals(fecha.toString())) {
                Map<String, Object> day = (Map<String, Object>) dia.get("day");
                Map<String, Object> condition = (Map<String, Object>) day.get("condition");

                return new RespuestaClimaDTO(
                        partido,
                        fecha.toString(),
                        ciudad,
                        new RespuestaClimaDTO.Weather(
                                condition.get("text").toString(),
                                Double.parseDouble(day.get("maxtemp_c").toString()),
                                Double.parseDouble(day.get("mintemp_c").toString())
                        )
                );
            }
        }

        return null;
    }
    public Evento registrarEventoConClima(Evento evento) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + API_KEY + "&q=" + evento.getCiudad() + "&days=7";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        Map<String, Object> forecast = (Map<String, Object>) response.get("forecast");
        List<Map<String, Object>> dias = (List<Map<String, Object>>) forecast.get("forecastday");

        for (Map<String, Object> dia : dias) {
            if (dia.get("date").equals(evento.getFecha().toString())) {
                Map<String, Object> day = (Map<String, Object>) dia.get("day");
                Map<String, Object> condition = (Map<String, Object>) day.get("condition");

                evento.setCondicion(condition.get("text").toString());
                evento.setTempMax(Double.parseDouble(day.get("maxtemp_c").toString()));
                evento.setTempMin(Double.parseDouble(day.get("mintemp_c").toString()));

                return eventoRepository.save(evento);
            }
        }

        return null;
    }


}
