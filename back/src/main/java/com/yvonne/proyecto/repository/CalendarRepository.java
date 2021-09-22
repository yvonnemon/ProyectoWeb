package com.yvonne.proyecto.repository;

import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.VacationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface CalendarRepository extends CrudRepository<Calendar, Integer> {
    List<Calendar> findByUser(User user);
    List<Calendar> findCalendarByStartDateAfter(LocalDateTime date);
    List<Calendar> findCalendarByStatus(VacationStatus status);
    List<Calendar> findCalendarByStatusAndUserOrderByIdDesc(VacationStatus status, User user, Pageable p);
    List<Calendar> findCalendarByStatusAndUser( VacationStatus status, User user);
    void deleteAllByUser(User user);
}
