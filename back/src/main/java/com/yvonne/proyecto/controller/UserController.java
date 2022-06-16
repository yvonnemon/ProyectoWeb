package com.yvonne.proyecto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yvonne.proyecto.manager.TokenManager;
import com.yvonne.proyecto.manager.UserManager;
import com.yvonne.proyecto.model.Document;
import com.yvonne.proyecto.model.Role;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.dto.UserDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserManager userManager;

    private static final Logger LOG = LogManager.getLogger(UserController.class);

    @Autowired
    private Gson gson;

    @GetMapping("/users")
    @Secured("ADMIN")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userManager.getAll();
    }

    @GetMapping("/employee")
    @Secured("ADMIN")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDto> getAllEmployee() {
        return userManager.getAllEmployee();
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<User> getUser(HttpServletRequest request) {
        try {
            //si el token es de un admin, se esta cargando la info del admin, asi que se devuelve la info del dueño del token
            User user = TokenManager.getUserFromRequest(request);
            return ResponseEntity.status(HttpStatus.OK).body(userManager.getById(user.getId()));

        } catch (Exception e) {
            LOG.error("ERROR: el duaurio no existe " + e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PostMapping(value = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ADMIN")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity createUser(@RequestBody UserDto data) {
        try {
            userManager.create(data);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("ERROR: no se puede crear el usuario " + e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Uno de los campos esta repetido");
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<String> updateUser(@RequestBody UserDto user, HttpServletRequest request) {
        try {
            //si es el propio usuario o el admin
            if (Objects.requireNonNull(TokenManager.getUserFromRequest(request)).getId().equals(user.getId()) || Objects.requireNonNull(TokenManager.getUserFromRequest(request)).getRole() == Role.ADMIN){
                boolean update = userManager.update(user);
                if (update) {
                    return ResponseEntity.status(HttpStatus.OK).body("Usuario actualizado");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo actualizar el usuario");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se pudo actualizar el usuario");
            }
        } catch (Exception e) {
            LOG.error("ERROR: no se pudo actualizar el usuario " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
    }

    @DeleteMapping("/delete")
    @Secured("ADMIN")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@RequestBody String id) {

        JsonObject jsonObject = gson.fromJson(id, JsonObject.class);

        String x = jsonObject.get("id").toString();

        if (userManager.getById(Integer.parseInt(x)) != null) {

            try {
                userManager.delete(userManager.getById(Integer.parseInt(x)));
                return ResponseEntity.status(HttpStatus.OK).body("Usuario borrado");
            } catch (Exception e) {
                LOG.error("ERROR: no se pudo borar el usuario " + e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hubo un error borrando el usuario");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }
    }
}


