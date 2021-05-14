package com.yvonne.proyecto.repository;

import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRepository extends CrudRepository<Calendar, Integer> {
    List<Calendar> findByUser(User user);
}
