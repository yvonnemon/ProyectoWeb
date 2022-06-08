package com.yvonne.proyecto.repository;

import com.yvonne.proyecto.model.Role;
import com.yvonne.proyecto.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
    User findByUsername(String username);
    List<User> findByRole(Role role);
    User findByEmail(String email);

}
