package com.yvonne.proyecto.repository;

import com.yvonne.proyecto.model.Clockin;
import com.yvonne.proyecto.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClockRepository extends CrudRepository<Clockin, Integer> {
    List<Clockin> findAllByOrderByIdAsc();
    List<Clockin> findAllByUserOrderByIdDesc(User user);
    List<Clockin> findAllByUserAndStartDateAfterOrderByIdDesc(User user, LocalDateTime date);
}
