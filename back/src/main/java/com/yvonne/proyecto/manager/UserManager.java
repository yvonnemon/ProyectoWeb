package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.repository.CrudManager;
import com.yvonne.proyecto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManager implements CrudManager<User> {
    @Autowired
    UserRepository userRepository;



    @Override
    public List<User> getAll() {
        return (List<User>)userRepository.findAll();
    }

    @Override
    public void create(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(User user) {
       userRepository.delete(user);
    }

    @Override
    public Boolean update(User user) {
        if( userRepository.existsById(user.getId()) ){
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

    public User getUserByLogin(String user, String pass){
        String asd = TokenManager.generateToken(user, pass);

        return new User();
    }
}
