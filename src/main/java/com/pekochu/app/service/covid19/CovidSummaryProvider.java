package com.pekochu.app.service.covid19;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CovidSummaryProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(CovidSummaryProvider.class.getCanonicalName());

    private final static String API_URL = "https://covid19.mathdro.id/api/countries/%s/%s";
    private final static String SINAVE_URL = "https://covid19.sinave.gob.mx/Log.aspx/Grafica22";
    private final static String OPEN_DATA_URL = "https://www.gob.mx/salud/documentos/datos-abiertos-152127";
    private final static String OPEN_DATA_DB = "http://datosabiertos.salud.gob.mx/gobmx/salud/datos_abiertos/datos_abiertos_covid19.zip";

    // Datos abiertos
    // URL: https://www.gob.mx/salud/documentos/datos-abiertos-152127
    // MIRROR: https://repounam.org/data/.input/
    // Repo URL
    // http://datosabiertos.salud.gob.mx/gobmx/salud/datos_abiertos/datos_abiertos_covid19.zip
    // http://187.210.186.146/gobmx/salud/datos_abiertos/historicos/10/datos_abiertos_covid19_25.10.2020.zip
    // https://repounam.org/data/.input/2020-10-24.zip

    public void downloadOpenData(){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMd");
        String flagFile = "covid_update_flag.json";
        Path pathFlagFile = Paths.get("./", flagFile);
        Path zipFile = Paths.get("./downloads/covidmx/", "covid_open_data.zip");
        JSONObject jsonFileFlag;

        try{
            // Read file in order to perform comparision
            StringBuilder jsonStringFlag = new StringBuilder();

            LOGGER.info("Checking website for updates");
            Document page = Jsoup.connect(OPEN_DATA_URL)
                    .userAgent("Mozilla")
                    .ignoreContentType(true).get();

            if(pathFlagFile.toFile().exists()){
                List<String> flagLines = Files.readAllLines(pathFlagFile);
                flagLines.forEach(line -> jsonStringFlag.append(line));
                jsonFileFlag = new JSONObject(jsonStringFlag.toString());
            }else{
                jsonFileFlag = new JSONObject();
                jsonFileFlag.put("page_last_update", "unknown");
            }

            String md5page = DigestUtils.md5Hex(page.data().getBytes());
            Element divUpdate = page.select("body > main > div.container > div.row > div.col-sm-5.col-md-4.col-xs-12.pull-right > div > dl > dd:nth-child(4)").first();
            String lastOpenDataUpdate = divUpdate.text();

            // Trigger download
            if(!jsonFileFlag.getString("page_last_update").equals(lastOpenDataUpdate)){
                ReadableByteChannel readChannel = Channels.newChannel(new URL(OPEN_DATA_DB).openStream());
                FileOutputStream fileOS = new FileOutputStream(zipFile.toFile().getAbsolutePath());
                FileChannel writeChannel = fileOS.getChannel();
                writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
                // Ready
                LOGGER.info("Zip file downloaded");
                File zipOpenData = zipFile.toFile();
                // Calculate MD5
                String checkSum = DigestUtils.md5Hex(Files.readAllBytes(zipOpenData.toPath()));

                jsonFileFlag.put("page_last_update", lastOpenDataUpdate);
                jsonFileFlag.put("page_md5_checksum", md5page);
                jsonFileFlag.put("file_zip_md5_checksum", checkSum);

                Files.write(pathFlagFile, jsonFileFlag.toString().getBytes(), StandardOpenOption.CREATE);

                // Unzip file
                new ZipFile(zipFile.toFile()).extractFile(String.format("%sCOVID19MEXICO.csv",
                        sdf.format(today)), "./downloads/covidmx/", "covid19.csv");
                LOGGER.info("CSV file unzipped");
                // Read the CSV
                readCovidCSV();
                // Closing streams
                fileOS.close();
                // Delete files on directory to save space on disk
                LOGGER.info("Performing deletes");
                FileUtils.cleanDirectory(new File("./downloads/covidmx/"));
            }else{
                LOGGER.info("Everything is up to date");
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

    }

    public void readCovidCSV(){
        // Fecha de corte
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fecha_corte = sdf.format(today);
        // CSV
        Path csvFile = Paths.get("./downloads/covidmx/", "covid19.csv");
        String[] cves = {
               "", "AGU", "BCN", "BCS", "CAM", "COA", "COL", "CHP", "CHH", "CMX", "DUR", "GUA", "GRO", "HID", "JAL",
                "MEX", "MIC", "MOR", "NAY", "NLE", "OAX", "PUE", "QUE", "ROO", "SLP", "SIN", "SON", "TAB", "TAM",
                "TLA", "VER", "YUC", "ZAC", "NAL" // NACIONAL
        };
        int NAL = cves.length - 2;

        if(!csvFile.toFile().exists()){
            LOGGER.error("File doesnt exist!");
        }else{
            // JSON Object
            JSONObject summary = new JSONObject();
            JSONObject estado;
            Map<String, AtomicLong> estados;
            for(int i = 1; i < cves.length; i++){
                estado = new JSONObject();
                estado.put("CVE", cves[i]);
                estado.put("fecha", fecha_corte);
                estado.put("sospechosos", 0L);
                estado.put("confirmados", 0L);
                estado.put("defunciones", 0L);
                summary.put(String.valueOf(i-1), estado);
            }
            // Continue reading the csv file
            CsvParserSettings settings = new CsvParserSettings();
            settings.getFormat().setLineSeparator("\n");
            settings.setHeaderExtractionEnabled(true);
            CsvParser parser = new CsvParser(settings);

            LOGGER.info("Parse begins");
            parser.beginParsing(csvFile.toFile(), StandardCharsets.ISO_8859_1);
            Record record;
            while ((record = parser.parseNextRecord()) != null) {
                int clasificacion_final = record.getInt("CLASIFICACION_FINAL");
                String fecha_defuncion = record.getString("FECHA_DEF");
                int entidad = record.getInt("ENTIDAD_UM") - 1;

                if(clasificacion_final == 6){
                    // SOSPECHOSOS
                    summary.getJSONObject(String.valueOf(entidad)).increment("sospechosos"); // ESTADO
                    summary.getJSONObject(String.valueOf(NAL)).increment("sospechosos"); // NAL
                }else if(clasificacion_final == 1 || clasificacion_final == 2 || clasificacion_final == 3){
                    // POSITIVOS
                    summary.getJSONObject(String.valueOf(entidad)).increment("confirmados"); // ESTADO
                    summary.getJSONObject(String.valueOf(NAL)).increment("confirmados"); // NAL
                    // DEFUNCIONES
                    if(!fecha_defuncion.equals("9999-99-99")){
                        try{
                            Date defuncion = sdf.parse(fecha_defuncion);
                            if(today.compareTo(defuncion) >= 0){
                                summary.getJSONObject(String.valueOf(entidad)).increment("defunciones"); // ESTADO
                                summary.getJSONObject(String.valueOf(NAL)).increment("defunciones"); // NAL
                            }
                        } catch (ParseException e) {
                            LOGGER.error(e.getMessage());
                        }

                    }
                }
            }
        }
    }

    public Map<Integer, String> getSemaphore(){
        String SEMAFORO_CONACYT = "https://datos.covid-19.conacyt.mx/Semaforo/semaforo.php";
        Map<Integer, String> semaforo_epidemiologico = null;
        // Posible patterns
        // SColors\[[^\[]*\]=\'([^\']*)\';
        // Best: SColors\[([^\[]*)\]=\'([^\']*)\';

        try {
            Document page = Jsoup.connect(SEMAFORO_CONACYT)
                    .userAgent("Mozilla")
                    .ignoreContentType(true).get();

            Element scripts = page.select("script").last();
            Pattern arraySemaforo = Pattern.compile("SColors\\[([^\\[]*)\\]=\\'([^\\']*)\\';");
            Matcher m = arraySemaforo.matcher(scripts.html());

            boolean b = m.find();

            if(!b){
                return null;
            }else{
                semaforo_epidemiologico = new HashMap<>();
            }

            while(b){
                semaforo_epidemiologico.put(Integer.valueOf(m.group(1).replace("'", "")), m.group(2));
                b = m.find();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return semaforo_epidemiologico;
    }

    public void updateRecoveries(){

    }



    public JSONObject getCovidSummary(String country){
        JSONObject news = null;
        try{
            news = new JSONObject(Jsoup.connect(String.format(API_URL, country, ""))
                    .ignoreContentType(true)
                    .get().body().text());

            LOGGER.info("ARCGIS CONSULTADA CORRECTAMENTE");
        }catch(IOException | JSONException e){
            LOGGER.error(e.getMessage());
        }

        return news;
    }

    public JSONArray getCovidDetails(String country){
        JSONArray object = null;
        try{
            object = new JSONArray(Jsoup.connect(String.format(API_URL, country, "confirmed"))
                    .ignoreContentType(true)
                    .get().body().text());

            LOGGER.info("ARCGIS CONSULTADA CORRECTAMENTE");
        }catch(IOException | JSONException e){
            LOGGER.error(e.getMessage());
        }

        return object;
    }

    public JSONObject getCovidMX(){
        JSONObject news = null;
        String result = null;
        int i = 1;
        try{
            news = new JSONObject(Jsoup.connect(SINAVE_URL)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .method(Connection.Method.POST)
                    .header("accept", "*/*")
                    .header("accept-language", "en-US,en;q=0.9")
                    .header("content-Type", "application/json")
                    .header("sec-fetch-dest", "empty")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .header("x-requested-with", "XMLHttpRequest")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/81.0.4044.113 Safari/537.36 Edg/81.0.416.58")
                    .requestBody("{}")
                    .execute().body());

            result = news.get("d").toString().replace("[[", "[").replace("]]", "]");
            news = new JSONObject();
            for(String s : result.split(",\\[")){
                String res = "[".concat(s);
                res = res.replace("[[", "[");
                news.put(String.valueOf(i++), new JSONArray(res));
            }

            LOGGER.info("SINAVE/SISVER CONSULTADA CORRECTAMENTE");
        }catch(IOException | JSONException e){
            LOGGER.error(e.getMessage(), e);
        }

        return news;
    }

    private class CovidMxState{

        private int id;
        private String label;
        private long habitantes;
        private int confirmados;
        private int estudiados;
        private int sospechosos;
        private int defunciones;
        private int recuperados;

        public CovidMxState(int id, String label, long habitantes, int confirmados, int estudiados, int sospechosos, int defunciones, int recuperados) {
            this.id = id;
            this.label = label;
            this.habitantes = habitantes;
            this.confirmados = confirmados;
            this.estudiados = estudiados;
            this.sospechosos = sospechosos;
            this.defunciones = defunciones;
            this.recuperados = recuperados;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public long getHabitantes() {
            return habitantes;
        }

        public void setHabitantes(long habitantes) {
            this.habitantes = habitantes;
        }

        public int getConfirmados() {
            return confirmados;
        }

        public void setConfirmados(int confirmados) {
            this.confirmados = confirmados;
        }

        public int getEstudiados() {
            return estudiados;
        }

        public void setEstudiados(int estudiados) {
            this.estudiados = estudiados;
        }

        public int getSospechosos() {
            return sospechosos;
        }

        public void setSospechosos(int sospechosos) {
            this.sospechosos = sospechosos;
        }

        public int getDefunciones() {
            return defunciones;
        }

        public void setDefunciones(int defunciones) {
            this.defunciones = defunciones;
        }

        public int getRecuperados() {
            return recuperados;
        }

        public void setRecuperados(int recuperados) {
            this.recuperados = recuperados;
        }
    }

    @NotNull
    private java.util.List<CovidMxState> orderStates(CovidMxState[] estados){
        java.util.List<CovidMxState> lista = new ArrayList<>();
        java.util.List<CovidMxState> ordenado = new ArrayList<>();
        int suma = 0;

        Collections.addAll(lista, estados);
        lista.remove(lista.size()-1);
        lista.sort(Comparator.comparingInt((CovidMxState::getConfirmados)));

        for(int i = lista.size()-1; i >= 0; i--){
            ordenado.add(lista.get(i));
        }

        return ordenado;
    }
}
