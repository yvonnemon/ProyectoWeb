package com.yvonne.proyecto.controller;

import com.yvonne.proyecto.manager.CalendarManager;
import com.yvonne.proyecto.manager.UserManager;
import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/calendar")
public class CalendarController {

    @Autowired
    CalendarManager calendarManager;

    @GetMapping("/vacations")
    public List<Calendar> getAllUsers() throws Exception {
        return calendarManager.getAll();
    }

}
