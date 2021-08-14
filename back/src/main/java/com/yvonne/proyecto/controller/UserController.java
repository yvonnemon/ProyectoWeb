package com.yvonne.proyecto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.yvonne.proyecto.manager.TokenManager;
import com.yvonne.proyecto.manager.UserManager;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserManager userManager;

    @Autowired
    private Gson gson;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userManager.getAll();
    }

    @GetMapping("/employee")
    public List<UserDto> getAllEmployee() {
        return userManager.getAllEmployee();
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser(HttpServletRequest request) {
        try {
            User user = TokenManager.getUserFromToken(request);
            return ResponseEntity.status(HttpStatus.OK).body(user);

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PostMapping(value = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createUser(@RequestBody User data) {
        try {
            userManager.create(data);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Uno de los campos esta repetido");
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            Boolean update = userManager.update(user);
            if(update){
                return ResponseEntity.status(HttpStatus.OK).body("Usuario actualizado");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody String id) {

            JsonObject jsonObject = gson.fromJson(id, JsonObject.class);

            String x = jsonObject.get("id").toString();

            if (userManager.getById(Integer.parseInt(x)) != null) {

                try {
                    userManager.delete(userManager.getById(Integer.parseInt(x)));
                    return ResponseEntity.status(HttpStatus.OK).body("Usuario borrado");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hubo un error borrando el usuario");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
            }
    }
}


