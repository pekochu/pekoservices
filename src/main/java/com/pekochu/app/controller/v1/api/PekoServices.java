package com.pekochu.app.controller.v1.api;

import io.swagger.annotations.Api;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(value = "pekoservices-application")
public class PekoServices {

    private final static Logger LOGGER = LoggerFactory.getLogger(PekoServices.class.getCanonicalName());

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

}
