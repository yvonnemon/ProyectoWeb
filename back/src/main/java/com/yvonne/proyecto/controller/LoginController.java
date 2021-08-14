package com.yvonne.proyecto.controller;

import com.yvonne.proyecto.manager.UserManager;
import com.yvonne.proyecto.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class LoginController {
    @Autowired
    private UserManager userManager;


    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDto data) {
        try{
            if(!data.getUsername().isEmpty() || !data.getPassword().isEmpty()) {
                String token = userManager.getUserByLogin(data.getUsername(), data.getPassword());
                return ResponseEntity.ok(HttpStatus.OK + "&" + token);
            } else {
                String token = userManager.findByGoogleUser(data.getEmail(),data.getName(),data.getLastname());
                return ResponseEntity.ok(HttpStatus.OK + "&" + token);
            }
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sus credenciales no son correctas");
        }
    }

}
