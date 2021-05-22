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
    CalendarManager calendarManager;

    @Autowired
    private Gson gson;

    @GetMapping("/vacations")
    public List<Calendar> getAllVacacions() throws Exception {
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
    public ResponseEntity<List<Calendar>> getNext(HttpServletRequest request) throws Exception {
        try {
            User user = TokenManager.validateAnyToken(request);
            List<Calendar> result = calendarManager.getNext(user);
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }



    @GetMapping("/users")
    public ResponseEntity<List<Calendar>> getAllFromUser(HttpServletRequest request) throws Exception {

        try {
            User user = TokenManager.validateAnyToken(request);

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
            Calendar calendar = new Calendar();
            LocalDateTime from = LocalDateTime.ofInstant(data.getStartDate().toInstant(), ZoneId.systemDefault());
            LocalDateTime to = LocalDateTime.ofInstant(data.getEndDate().toInstant(), ZoneId.systemDefault());
            calendar.setStartDate(from);
            calendar.setEndDate(to);
            calendar.setComment(data.getComment());
            calendar.setUser(TokenManager.validateAnyToken(request));

            calendarManager.create(calendar);
            return ResponseEntity.status(HttpStatus.OK).body("Vacaciones solicitadas");
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.toString());
        }
    }

    @PutMapping("/update")
    public ResponseEntity updateStatus(@RequestBody String data) throws Exception {
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        String id = jsonObject.get("id").toString();
        String clean = jsonObject.get("status").toString();
        clean = clean.replace("\"","");
        VacationStatus status = VacationStatus.valueOf(clean);
        if (calendarManager.getById(Integer.parseInt(id)) != null) {

            try {
                Calendar calendar = calendarManager.getById(Integer.parseInt(id));
                calendarManager.updateStatus(calendar, status);
                return ResponseEntity.status(HttpStatus.OK).body("Estado modificado");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hubo un error modificando los datos");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Fechas no encontradas");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody String id) throws Exception {

        JsonObject jsonObject = gson.fromJson(id, JsonObject.class);

        String x = jsonObject.get("id").toString();

        if (calendarManager.getById(Integer.parseInt(x)) != null) {

            try {
                calendarManager.delete(calendarManager.getById(Integer.parseInt(x)));
                return ResponseEntity.status(HttpStatus.OK).body("Usuario borrado");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hubo un error borrando el usuario");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }
    }
}