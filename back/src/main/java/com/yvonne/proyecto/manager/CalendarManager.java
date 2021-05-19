package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.VacationStatus;
import com.yvonne.proyecto.repository.CalendarRepository;
import com.yvonne.proyecto.repository.CrudManager;
import com.yvonne.proyecto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public List<Calendar> getMonthly(){
        LocalDateTime currentdate = LocalDateTime.now();
                //LocalDate.of(currentdate.getYear(), currentdate.getMonth(),1);
        return calendarRepository.findCalendarByStartDateAfter(LocalDateTime.of(currentdate.getYear(),currentdate.getMonthValue(),1,0,0));
    }

    public List<Calendar> getPending(){
        return calendarRepository.findCalendarByStatus(VacationStatus.PENDING);
    }

    public List<Calendar> getNext(User user) throws Exception {
        Pageable limit = PageRequest.of(0,5);

        return calendarRepository.findCalendarByStatusAndUserOrderByIdDesc(VacationStatus.APPROVED, user,limit );
    }

    public List<Calendar> getAllFromUser(User user) throws Exception {

        return calendarRepository.findByUser(user);
    }
}
