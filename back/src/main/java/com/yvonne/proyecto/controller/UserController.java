package com.yvonne.proyecto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.yvonne.proyecto.manager.UserManager;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( value = "/user")
public class UserController {
    @Autowired
    private UserManager userManager;

    @Autowired
    private Gson gson;

    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userManager.getAll();
    }

    @PostMapping("/login")
    public ResponseEntity loginUser( @RequestParam("user") String data){
        //String[] login = data.split("&");
        User usuario = userManager.getUserByLogin("user","");
        String asd = "asd";
        return ResponseEntity.ok(HttpStatus.OK+asd);
        //ResponseEntity.ok();
    }

    @PostMapping("/register")
    public String createUser(@RequestBody User user){
        userManager.create(user);
        return "usuario creado";
    }

    @PutMapping("/update")
    public String updateUser(@RequestBody User user){

        Boolean done = userManager.update(user);
        if(done){
            return "usuario actualizado";
        } else {
            return "hubo un error";
        }
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestBody String id){
        JsonObject jsonObject = gson.fromJson(id, JsonObject.class);

        String x = jsonObject.get("id").toString();

        if(userManager.getById(Integer.parseInt(x)) != null){
            userManager.delete(userManager.getById(Integer.parseInt(x)));
            return "usuario borrado";
        } else {
            return "no existe el usuario";
        }
    }
}

