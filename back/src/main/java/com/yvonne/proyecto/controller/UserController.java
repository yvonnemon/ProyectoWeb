package com.yvonne.proyecto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.yvonne.proyecto.manager.UserManager;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/user")
    public User getUser(@RequestBody String id) {
        JsonObject jsonObject = gson.fromJson(id, JsonObject.class);
        String x = jsonObject.get("id").toString();

        return userManager.getById(Integer.parseInt(x));
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody UserDto data) {
        Map<Boolean, String> credentials = userManager.getUserByLogin(data.getUsername(), data.getPassword());
        if (credentials.containsKey(true)) {
            return ResponseEntity.ok(HttpStatus.OK + "&" + credentials.get(true));

        } else {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);

        }
    }

    @PostMapping(value = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createUser(@RequestBody User data) {
        try {
            userManager.create(data);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateUser(@RequestBody User user) {
        try {
            userManager.update(user);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteUser(@RequestBody String id) {

            JsonObject jsonObject = gson.fromJson(id, JsonObject.class);

            String x = jsonObject.get("id").toString();

            if (userManager.getById(Integer.parseInt(x)) != null) {

            try {
                userManager.delete(userManager.getById(Integer.parseInt(x)));
                return ResponseEntity.ok(HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
            }
            } else {
                return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
            }

    }
}


