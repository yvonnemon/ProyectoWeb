package com.yvonne.proyecto.controller;

import com.google.gson.Gson;
import com.yvonne.proyecto.manager.CalendarManager;
import com.yvonne.proyecto.manager.TokenManager;
import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.dto.CalendarDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/calendar")
public class CalendarController {

    @Autowired
    private CalendarManager calendarManager;

    private static final Logger LOG = LogManager.getLogger(CalendarController.class);

    @GetMapping("/vacations")
    public List<Calendar> getAllVacations() {
        return calendarManager.getAll();
    }

    @GetMapping("/monthly")
    public List<Calendar> getMonthly() {
        return calendarManager.getMonthly();
    }

    @GetMapping("/pending")
    public List<Calendar> getPending() {
        return calendarManager.getPending();
    }

    @GetMapping("/next")
    public ResponseEntity<List<Calendar>> getNext(HttpServletRequest request) {

        try {
            User user = TokenManager.getUserFromRequest(request);
            List<Calendar> result = calendarManager.getNext(user);

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            LOG.error("ERROR: no se hay vacaciones disponibles " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<Calendar>> getAllFromUser(HttpServletRequest request) {

        try {
            User user = TokenManager.getUserFromRequest(request);
            List<Calendar> list = calendarManager.getAllFromUser(user);

            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            LOG.error("ERROR: no se pudieron recuperar las vacaciones " + e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertVacation(@RequestBody CalendarDto data, HttpServletRequest request) {

        try {
            data.setUser(TokenManager.getUserFromRequest(request));
            calendarManager.create(data);
            return ResponseEntity.status(HttpStatus.OK).body("Vacaciones solicitadas");
        } catch (Exception e) {
            LOG.error("ERROR: no se pudo crear las vacaciones" + e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.toString());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateStatus(@RequestBody CalendarDto data) {

        try {
            calendarManager.updateStatus(data);
            return ResponseEntity.status(HttpStatus.OK).body("Estado modificado");
        } catch (Exception e) {
            LOG.error("ERROR: no se modificaron los datos correctamente " + e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hubo un error modificando los datos");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody CalendarDto data) {

        try {
            calendarManager.delete(data);
            return ResponseEntity.status(HttpStatus.OK).body("Usuario borrado");
        } catch (Exception e) {
            LOG.error("ERROR: no se pudieron borrar las vacaciones" + e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hubo un error borrando el usuario");
        }
    }
}