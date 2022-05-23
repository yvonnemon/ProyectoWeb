package com.yvonne.proyecto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yvonne.proyecto.manager.CalendarManager;
import com.yvonne.proyecto.manager.TokenManager;
import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.VacationStatus;
import com.yvonne.proyecto.model.dto.CalendarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping(value = "/calendar")
public class CalendarController {

    @Autowired
    private CalendarManager calendarManager;

    @Autowired
    private Gson gson;

    @GetMapping("/vacations")
    public List<Calendar> getAllVacations() throws Exception {
        return calendarManager.getAll();
    }
    @GetMapping("/monthly")
    public List<Calendar> getMonthly() throws Exception {
        return calendarManager.getMonthly();
    }

    @GetMapping("/pending")
    public List<Calendar> getPending() throws Exception {
        return calendarManager.getPending();
    }
    @GetMapping("/next")
    public ResponseEntity<List<Calendar>> getNext(HttpServletRequest request) {
        try {
            User user = TokenManager.getUserFromRequest(request);
            List<Calendar> result = calendarManager.getNext(user);

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<Calendar>> getAllFromUser(HttpServletRequest request)
    {
        try
        {
            User user = TokenManager.getUserFromRequest(request);
            List<Calendar> list = calendarManager.getAllFromUser(user);

            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertVacation(@RequestBody CalendarDto data, HttpServletRequest request) {

        try {
            data.setUser(TokenManager.getUserFromRequest(request));

            calendarManager.create(data);
            return ResponseEntity.status(HttpStatus.OK).body("Vacaciones solicitadas");
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.toString());
        }
    }

    @PutMapping("/update")
    public ResponseEntity updateStatus(@RequestBody CalendarDto data) throws Exception {

            try {
                calendarManager.updateStatus(data);
                return ResponseEntity.status(HttpStatus.OK).body("Estado modificado");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hubo un error modificando los datos");
            }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody CalendarDto data) throws Exception {

            try {

                calendarManager.delete(data);
                return ResponseEntity.status(HttpStatus.OK).body("Usuario borrado");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hubo un error borrando el usuario");
            }
    }
}