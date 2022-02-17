package com.yvonne.proyecto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yvonne.proyecto.manager.DocumentManager;
import com.yvonne.proyecto.manager.TokenManager;
import com.yvonne.proyecto.model.Document;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.dto.DocumentDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/document")
public class DocumentController {

    @Autowired
    private Gson gson;

    private static final Logger LOG = LogManager.getLogger(Document.class);

    @Autowired
    private DocumentManager documentManager;

    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getAll() {
        try {
            List<Document> result = documentManager.getAll();
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e){
            LOG.error( "ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Document>> getLasts()  {
        try {
            List<Document> result = documentManager.getLasts();
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e){
            LOG.error( "ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/employee")
    public ResponseEntity<List<Document>> getUserLasts(HttpServletRequest request) {

        try {
            User user = TokenManager.getUserFromRequest(request);

            List<Document> result = documentManager.getUserLasts(user);
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e){
            LOG.error( "ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/userdocs")
    public ResponseEntity<List<Document>> getAllFromUser(HttpServletRequest request) {
        try {
            User user = TokenManager.getUserFromRequest(request);
            List<Document> result = documentManager.getAllFromUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e){
            LOG.error( "ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/document")
    public Document getOne(Document data)  {
        // TODO borrar si no se usa
        return null;
    }

    @PostMapping(value = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody DocumentDto data) {

        try {
            documentManager.generateNewDocument(data);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }

    }

    @PutMapping("/update")
    public ResponseEntity<String> update(Document data) {
        // TODO borrar si no se usa

        return null;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody String id) throws Exception {
        JsonObject jsonObject = gson.fromJson(id, JsonObject.class);

        String x = jsonObject.get("id").toString();

        try {
            Document doc = documentManager.getById(Integer.parseInt(x));
            documentManager.delete(doc);
            return ResponseEntity.status(HttpStatus.OK).body("ok");

        } catch (Exception e){
            LOG.error( "ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestBody String data) throws Exception{
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);

        String x = jsonObject.get("data").toString();

        try {

            byte[] result = documentManager.downloadFile(Integer.parseInt(x));
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e){
            LOG.error( "ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }
}
