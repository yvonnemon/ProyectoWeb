package com.yvonne.proyecto.controller;

import com.yvonne.proyecto.manager.ClockManager;
import com.yvonne.proyecto.manager.TokenManager;
import com.yvonne.proyecto.manager.UserManager;
import com.yvonne.proyecto.model.Clockin;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.dto.UserDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/clock")
public class ClockController {

    private static final Logger LOG = LogManager.getLogger(DocumentController.class);

    @Autowired
    private ClockManager clockManager;

    @Autowired
    private UserManager userManager;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Clockin>> getAll() {
        try {
            //lista todos los fichajes del dia
            List<Clockin> result = clockManager.getAll();
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            LOG.error("ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/userclockin")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<Clockin>> getAllFromUser(HttpServletRequest request) {
        try {
            //lista toddos los del usuario
            User user = TokenManager.getUserFromRequest(request);
            List<Clockin> result = clockManager.getAllFromUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            LOG.error("ERROR: no se pudieron recuperar los archivos " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PostMapping(value = "/insert")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity create( HttpServletRequest request) {
        try {
            //nuevo item asignado al usuario del token
            Clockin clockin = new Clockin();
            clockin.setUser(TokenManager.getUserFromRequest(request));
            clockin.setEndDate(null);
            clockManager.create(clockin);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }
    @PutMapping(value = "/update")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<String> update(HttpServletRequest request) {
        try {
            //va a updatear el la ultima entrada del usuario del token
            boolean update = clockManager.update(TokenManager.getUserFromRequest(request));
            if (update) {
                return ResponseEntity.status(HttpStatus.OK).body("Usuario actualizado");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
            }
        } catch (Exception e) {
            LOG.error("ERROR: no se pudo actualizar el usuario " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
    }
    @PostMapping("/stalk")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Clockin>> stalkfromuser(@RequestBody UserDto user, HttpServletRequest request){
        try {
            //si es admin puede ver los registros de todos los usuarios
            User usuario = userManager.getById(user.getId());
            List<Clockin> result = clockManager.stalkUser(usuario);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

}
