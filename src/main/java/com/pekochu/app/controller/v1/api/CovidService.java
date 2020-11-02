package com.pekochu.app.controller.v1.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pekochu.app.model.covid19.Estado;
import com.pekochu.app.model.covid19.Reporte;
import com.pekochu.app.service.covid19.ReporteService;
import io.swagger.annotations.Api;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/covid", produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(value = "pekoservices-application")
public class CovidService {

    @Autowired
    ReporteService reporteService;

    private final static Logger LOGGER = LoggerFactory.getLogger(CovidService.class.getCanonicalName());

    @RequestMapping(value = {"/", "/**"})
    public @ResponseBody
    ResponseEntity<String> main() {
        LOGGER.info("Home API");
        JSONObject data = new JSONObject();
        JSONObject content = new JSONObject();
        content.put("type", "greet");
        content.put("status", "OK");

        data.put("header", HttpStatus.OK);
        data.put("data", content);

        return ResponseEntity.status(HttpStatus.OK.value()).body(data.toString());
    }

    @RequestMapping(value = "latest")
    public @ResponseBody
    ResponseEntity<String> latest() {
        LOGGER.info("Home API");
        JSONObject data = new JSONObject();
        JSONObject content = new JSONObject();

        Reporte latest = reporteService.lastReport();
        JSONObject jsonBody = new JSONObject();
        Writer writer = new StringWriter();
        String fecha_corte = latest.getFecha();

        ObjectMapper objectMapper = new ObjectMapper();

        List<Reporte> reportes = reporteService.findReportsByDate(fecha_corte);
        reportes.forEach(reporte -> {
            Estado reporteEstado = reporte.getState();
            try {
                objectMapper.writeValue(writer, reporte);
                jsonBody.put(reporteEstado.getCve(), new JSONObject(writer.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        content.put("type", latest.getClass().getName());
        content.put("body", jsonBody);

        try {
            objectMapper.writeValue(writer, latest);
            content.put("latest", new JSONObject(writer.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        content.put("status", "OK");

        data.put("header", HttpStatus.OK);
        data.put("data", content);

        return ResponseEntity.status(HttpStatus.OK.value()).body(data.toString());
    }
}
