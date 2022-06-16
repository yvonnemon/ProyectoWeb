package com.yvonne.proyecto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yvonne.proyecto.manager.DocumentManager;
import com.yvonne.proyecto.manager.TokenManager;
import com.yvonne.proyecto.model.*;
import com.yvonne.proyecto.model.dto.DocumentDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping(value = "/document")
public class DocumentController {

    @Autowired
    private Gson gson;

    private static final Logger LOG = LogManager.getLogger(DocumentController.class);

    @Autowired
    private DocumentManager documentManager;

    @GetMapping("/documents")
    @Secured("ADMIN")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Document>> getAll() {
        try {
            //lista todos los documentos
            List<Document> result = documentManager.getAll();
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            LOG.error("ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/admin")
    @Secured("ADMIN")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Document>> getLasts(HttpServletRequest request) {

        try {
            //lista los ultimos documentos
            List<Document> result = documentManager.getLasts();
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            LOG.error("ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<Document>> getUserLasts(HttpServletRequest request) {

        try {
            //devolvera siempre los del usuario del token
            User user = TokenManager.getUserFromRequest(request);

            List<Document> result = documentManager.getUserLasts(user);
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            LOG.error("ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/userdocs")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<Document>> getAllFromUser(HttpServletRequest request) {
        try {
            //lista toddos los del usuario
            User user = TokenManager.getUserFromRequest(request);
            List<Document> result = documentManager.getAllFromUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            LOG.error("ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping(value = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ADMIN")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity create(@RequestBody DocumentDto data) {

        try {
            //nuevo item
            documentManager.generateNewDocument(data);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }

    }


    @DeleteMapping("/delete")
    @Secured("ADMIN")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> delete(@RequestBody DocumentDto doc) throws Exception {

        try {
            //el admin puede borrar todo
            documentManager.delete(doc);
            return ResponseEntity.status(HttpStatus.OK).body("ok");

        } catch (Exception e) {
            LOG.error("ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR: no se pudieron recuperar los archivos");
        }
    }

    @PostMapping("/download")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<byte[]> downloadFile(@RequestBody String data, HttpServletRequest request) throws Exception {

        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        String x = jsonObject.get("data").toString();

        try {
            Document doc = documentManager.getById(Integer.parseInt(x));

            User user = TokenManager.getUserFromRequest(request);
            if (doc.getUser().equals(user) || TokenManager.getUserFromRequest(request).getRole() == Role.ADMIN){
                byte[] result = documentManager.downloadFile(Integer.parseInt(x));
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("e".getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            LOG.error("ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString().getBytes(StandardCharsets.UTF_8));
        }

    }
}
