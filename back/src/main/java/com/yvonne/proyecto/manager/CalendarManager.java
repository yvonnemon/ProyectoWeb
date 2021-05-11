package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.repository.CalendarRepository;
import com.yvonne.proyecto.repository.CrudManager;
import com.yvonne.proyecto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class CalendarManager implements CrudManager<Calendar> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CalendarRepository calendarRepository;

    @Override
    public List<Calendar> getAll() throws Exception {
        return (List<Calendar>) calendarRepository.findAll();
    }

    @Override
    public void create(Calendar object) throws Exception {

    }

    @Override
    public void delete(Calendar object) throws Exception {

    }

    @Override
    public Boolean update(Calendar object) throws Exception {
        return null;
    }

    @Override
    public Calendar getById(Integer id) throws Exception {
        return null;
    }
}
