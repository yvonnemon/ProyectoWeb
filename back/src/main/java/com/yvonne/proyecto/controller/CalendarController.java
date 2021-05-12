package com.yvonne.proyecto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yvonne.proyecto.manager.CalendarManager;
import com.yvonne.proyecto.manager.TokenManager;
import com.yvonne.proyecto.manager.UserManager;
import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.VacationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping(value = "/calendar")
public class CalendarController {

    @Autowired
    CalendarManager calendarManager;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    @Autowired
    private Gson gson;

    @GetMapping("/vacations")
    public List<Calendar> getAllVacacions() throws Exception {
        return calendarManager.getAll();
    }
    @GetMapping("/users")
    public List<Calendar> getAllFromUser(HttpServletRequest request) throws Exception {
        String auth = request.getHeader("Authorization");
        String token = auth.split(" ")[1];
        User asd = TokenManager.getTokenUser(token);

        return calendarManager.getAll();
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
