package com.pekochu.app.service;

import com.pekochu.app.util.Constants;
import com.pekochu.app.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class MinecraftInterface {

    private final static Logger LOGGER = LoggerFactory.getLogger(MinecraftInterface.class.getCanonicalName());

    public MinecraftInterface(){ }

    public JSONObject startServer(){
        Map params = new HashMap<>();
        String method = "startServer";
        params.put("id", "API SERVER NODE");
        return new JSONObject(call(method, params));
    }

    public JSONObject stopServer(){
        Map params = new HashMap<>();
        String method = "stopServer";
        params.put("id", "API_SERVER_NODE");
        return new JSONObject(call(method, params));
    }

    public JSONObject killServer(){
        Map params = new HashMap<>();
        String method = "killServer";
        params.put("id", "API_SERVER_NODE");
        return new JSONObject(call(method, params));
    }

    public JSONObject restartServer(){
        Map params = new HashMap<>();
        String method = "restartServer";
        params.put("id", "API_SERVER_NODE");
        return new JSONObject(call(method, params));
    }

    public JSONObject getStatus(){
        // Comandos de Multicraft
        Map params = new HashMap<>();
        String method = "getServerStatus";
        params.put("id", "API_SERVER_NODE");
        return new JSONObject(call(method, params));
    }

    public JSONObject getServerResources(){
        // Comandos de Multicraft
        Map params = new HashMap<>();
        String method = "getServerResources";
        params.put("id", "API_SERVER_NODE");
        return new JSONObject(call(method, params));
    }

    public String call(String method, Map<String, String> params){
        String URL = "API_SERVER_NODE".concat(Objects.requireNonNull(buildParams(method, params)));
        String response;

        // Petición HTTP
        try{
            response = (Jsoup.connect(URL).ignoreContentType(true).ignoreHttpErrors(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/79.0.3945.130 Safari/537.36")
                    .get().body().text());

            // Si la respuesta no es un JSON, hay un error
            if(!Utils.isJSONValid(response)){
                JSONObject error = new JSONObject();
                JSONArray errors = new JSONArray();
                error.put("success", false);
                errors.put(response);
                error.put("errors", errors);

                response = error.toString();
            }
        }catch(IOException e){
            LOGGER.error(e.getMessage(), e);

            // Template error
            JSONObject error = new JSONObject();
            JSONArray errors = new JSONArray();
            error.put("success", false);
            errors.put(e.getMessage());
            error.put("errors", errors);

            response = error.toString();
        }

        return response;
    }

    @Nullable
    private String buildParams(String method, Map<String, String> params) {
        params = new HashMap<>(params);

        try{
            params.put("_MulticraftAPIMethod", method);
            params.put("_MulticraftAPIUser", "API_SERVER_USERNAME");

            StringBuilder apiKeySalt = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (Map.Entry<String, String> param : params.entrySet()) {
                final String parameterKey = param.getKey();
                final String parameterValue = param.getValue();

                // The api key is hashed with all params put after each other (with their values)
                apiKeySalt.append(parameterKey).append(parameterValue);
                query.append("&").append((parameterKey)).append("=").append((parameterValue));
            }

            // Append apikey (build by a hash of the apikey and the concatinated params as salt)
            query.append("&_MulticraftAPIKey=").append(getMulticraftEncodedAPIKey(apiKeySalt.toString(),
                    "API_SERVER_KEY"));
            return query.toString();
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    private String getMulticraftEncodedAPIKey(@NotNull String parameterQuery, @NotNull String apiKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hasher = Mac.getInstance("HmacSHA256");
        hasher.init(new SecretKeySpec(apiKey.getBytes(), "HmacSHA256"));

        byte[] hash = hasher.doFinal(parameterQuery.getBytes());

        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }
}
