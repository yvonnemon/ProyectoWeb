package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.Document;
import com.yvonne.proyecto.model.Role;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.dto.UserDto;
import com.yvonne.proyecto.repository.CrudManager;
import com.yvonne.proyecto.repository.UserRepository;
import com.yvonne.proyecto.util.EmailSender;
import com.yvonne.proyecto.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.NaturalId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserManager implements CrudManager<User> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CalendarManager calendarManager;

    @Autowired
    DocumentManager documentManager;

    @Autowired
    ClockManager clockManager;

    @Override
    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public void create(User object) throws Exception {

    }

    private static final Logger LOG = LogManager.getLogger(Document.class);

    public void create(UserDto user) throws Exception {
        try {
            String pass = Util.randomString(7);

            User tempUser = new User();
            tempUser.setName(user.getName());
            tempUser.setLastname(user.getLastname());
            tempUser.setAddress(user.getAddress());
            tempUser.setTelephone(user.getTelephone());
            tempUser.setPassword(pass);
            //para tests
            tempUser.setPassword("123");

            tempUser.setDni(user.getDni());
            tempUser.setUsername(user.getUsername());
            tempUser.setEmail(user.getEmail());
            tempUser.setRole(user.getRole());

            String data = setBodyHtml(tempUser);
            tempUser.setPassword(passwordCypher(pass));

            EmailSender.sendEmail("Nuevo registro", "templates/template.html",
                    data, tempUser.getEmail());
            userRepository.save(tempUser);

        } catch (Exception e) {
            LOG.error("ERROR: el archivo a guardar no existe " + e.getMessage(), e);
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    @Override
    @Transactional
    public void delete(User user) {
        documentManager.deleteAllFromUser(user);
        calendarManager.deleteAllFromUser(user);
        clockManager.deleteAllFromUser(user);
        userRepository.delete(user);
    }

    @Override
    public Boolean update(User object) throws Exception {
        return null;
    }

    public Boolean update(UserDto user) {
        if (userRepository.existsById(user.getId())) {

            User updatedUser = userRepository.findById(user.getId()).orElse(null);

            updatedUser.setDni(user.getDni());
            updatedUser.setName(user.getName());
            updatedUser.setLastname(user.getLastname());
            updatedUser.setTelephone(user.getTelephone());
            updatedUser.setUsername(user.getUsername());
            updatedUser.setAddress(user.getAddress());
            if(user.getPassword() != null && !user.getPassword().isEmpty()){
                updatedUser.setPassword(passwordCypher(user.getPassword()));

            }
            //esta linea es para testear
            //updatedUser.setPassword(passwordCypher("123"));
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

    public String findUser(UserDto data) throws Exception {
        String token;
        if (!data.getUsername().isEmpty() || !data.getPassword().isEmpty()) {
            token = getUserByLogin(data.getUsername(), data.getPassword());
        } else {
            token = findByGoogleUser(data.getEmail(), data.getName(), data.getLastname());
        }
        return token;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(user.getAuthorities());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), roles);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String findByGoogleUser(String email, String name, String lastname) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            user = new User();
            user.setName(name);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setPassword(Util.randomString(7));
            user.setRole(Role.EMPLOYEE);
            user.setDni("1");
            user.setUsername(name+lastname+Util.randomString(5));
            user.setTelephone(0);
            user.setAddress("");

            userRepository.save(user);
            return TokenManager.generateToken(user);
        } else {
            return TokenManager.generateToken(findByEmail(email));
        }

    }

    public String getUserByLogin(String user, String pass) {
        try {
            User loginUser = userRepository.findByUsername(user);
            boolean matched = BCrypt.checkpw(pass, loginUser.getPassword());
            if (matched) {
                return TokenManager.generateToken(loginUser);
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            return e.toString();
        }
    }

    public List<UserDto> getAllEmployee() {

        List<User> users = userRepository.findByRole(Role.EMPLOYEE);
        List<UserDto> result = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            UserDto user = new UserDto();
            User u = users.get(i);
            user.setFullName(u.getName() + " " + u.getLastname());
            user.setId(u.getId());
            result.add(user);
        }
        return result;
    }

    private String passwordCypher(String password) {

        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    private String setBodyHtml(User usuario) {
        //a??adir el contenido del correo, para que se vea bonito
        StringBuilder result = new StringBuilder();

        String first = "<tr>  <td style=\"padding: 20px 0 30px 0;\"> <span style=\"font-size: 18px\">";
        String header = "Tu nombre de usuario es este: ";
        String user = usuario.getUsername();
        String span = "<br><span>";
        String payAndDate = "La contrase??a para acceder es esta: " + usuario.getPassword() + " recuerda cambiarla.";
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
