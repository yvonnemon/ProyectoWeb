package com.yvonne.proyecto.repository;

import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CalendarRepository extends CrudRepository<Calendar, Integer> {
    List<Calendar> findByUser(User user);
}
