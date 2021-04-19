package com.yvonne.proyecto.repository;

import com.yvonne.proyecto.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Integer> {

}
