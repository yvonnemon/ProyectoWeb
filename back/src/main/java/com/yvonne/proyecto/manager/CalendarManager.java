package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.VacationStatus;
import com.yvonne.proyecto.repository.CalendarRepository;
import com.yvonne.proyecto.repository.CrudManager;
import com.yvonne.proyecto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarManager implements CrudManager<Calendar> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CalendarRepository calendarRepository;

    @Override
    public List<Calendar> getAll() throws Exception {
        try{
            return (List<Calendar>) calendarRepository.findAll();

        } catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    @Override
    public void create(Calendar vacation) throws Exception {
        try {
            vacation.setStatus(VacationStatus.PENDING);
            calendarRepository.save(vacation);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void delete(Calendar vacation) throws Exception {
        vacation.setStatus(VacationStatus.CANCELED);
        calendarRepository.save(vacation);
    }

    @Override
    public Boolean update(Calendar object) throws Exception {
        return null;
    }

    public void updateStatus(Calendar vacation, VacationStatus status) throws Exception {
        vacation.setStatus(status);
        calendarRepository.save(vacation);
    }

    @Override
    public Calendar getById(Integer id) throws Exception {
        return calendarRepository.findById(id).orElse(null);
    }

    public List<Calendar> getAllFromUser(User user) throws Exception {

        return calendarRepository.findByUser(user);
    }
}
