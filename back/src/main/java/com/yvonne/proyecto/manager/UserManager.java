package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.Role;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.dto.UserDto;
import com.yvonne.proyecto.repository.CrudManager;
import com.yvonne.proyecto.repository.UserRepository;
import com.yvonne.proyecto.util.EmailSender;
import com.yvonne.proyecto.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserManager implements CrudManager<User> {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public void create(User user) throws Exception{

        String pass = Util.randomString(7);
        user.setPassword(pass);
        String data = setBodyHtml(user);
        userRepository.save(user);
        EmailSender.sendEmail("Nuevo registro", "templates/template.html",
                data, user.getEmail());
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public Boolean update(User user){
        if (userRepository.existsById(user.getId())) {

            User updatedUser = userRepository.findById(user.getId()).orElse(null);

            updatedUser.setDni(user.getDni());
            updatedUser.setName(user.getName());
            updatedUser.setLastname(user.getLastname());
            updatedUser.setTelephone(user.getTelephone());
            updatedUser.setUsername(user.getUsername());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setEmail(user.getEmail());

            userRepository.save(updatedUser);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public User getById(Integer id) {

        return userRepository.findById(id).orElse(null);
    }

    public Map<Boolean, String> getUserByLogin(String user, String pass) {
        User loginUser = userRepository.findByUsernameAndPassword(user, pass);
        Map<Boolean, String> response = new HashMap<>();
        if (loginUser != null) {
            String credentials = TokenManager.generateToken(loginUser);
            response.put(true, credentials);
        } else {
            response.put(false, "wrong credentials");
        }


        return response;
    }

    public List<UserDto> getAllEmployee(){

        List<User> users = userRepository.findByRole(Role.EMPLOYEE);
        List<UserDto> result = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            UserDto user = new UserDto();
            User u = users.get(i);
            user.setFullName(u.getName()+" "+u.getLastname());
            user.setId(u.getId());
            result.add(user);
        }
        return result;
    }

    private String setBodyHtml(User usuario) {
        StringBuilder result = new StringBuilder();

        String first = "<tr>  <td style=\"padding: 20px 0 30px 0;\"> <span style=\"font-size: 18px\">";
        String header = "Tu nombre de usuario es este: ";
        String user = usuario.getUsername();
        String span = "<br><span>";
        String payAndDate = "La contraseña para acceder es esta: " + usuario.getPassword() + " recuerda cambiarla.";
        String spanclose = "</span>";
        String welcome = " <br><span> Bienvenido " + usuario.getName() + "</span>";
        result.append(first);
        result.append(header);
        result.append(user);
        result.append(span);
        result.append(payAndDate);
        result.append(welcome);
        result.append(spanclose);

        String tdtr = "</span> </td></tr>";
        result.append(tdtr);
        return result.toString();
    }
}
