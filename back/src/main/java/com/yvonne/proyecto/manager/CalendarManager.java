package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.Document;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.VacationStatus;
import com.yvonne.proyecto.repository.CalendarRepository;
import com.yvonne.proyecto.repository.CrudManager;
import com.yvonne.proyecto.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOG = LogManager.getLogger(Document.class);

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
        try {
            vacation.setStatus(VacationStatus.CANCELED);
            calendarRepository.save(vacation);
        } catch (Exception e) {
            LOG.error("ERROR: no se pudo cancelar " + e.getMessage(), e);
            throw new Exception(e);
        }

    }

    @Override
    public Boolean update(Calendar calendar) throws Exception {
        return null;
    }

    public void updateStatus(Calendar vacation, VacationStatus status) throws Exception {
        try {
            vacation.setStatus(status);
            calendarRepository.save(vacation);
        } catch (Exception e) {
            LOG.error("ERROR: no se pudo actualizar el estado " + e.getMessage(), e);
            throw new Exception(e);
        }
    }

    @Override
    public Calendar getById(Integer id) throws Exception {
        try {
            return calendarRepository.findById(id).orElse(null);
        } catch (Exception e) {
            LOG.error("ERROR: no existe ese id " + e.getMessage(), e);
            throw new Exception(e);
        }

    }

    public List<Calendar> getMonthly() throws Exception{
        try {
            LocalDateTime currentdate = LocalDateTime.now();
            return calendarRepository.findCalendarByStartDateAfter(LocalDateTime.of(currentdate.getYear(),currentdate.getMonthValue(),1,0,0));
        } catch (Exception e) {
            LOG.error("ERROR: No se pudieron recuperar los datos " + e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public List<Calendar> getPending() throws Exception{
        try {
            return calendarRepository.findCalendarByStatus(VacationStatus.PENDING);
        } catch (Exception e) {
            LOG.error("ERROR: No se pudieron recuperar los datos " + e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public List<Calendar> getNext(User user) throws Exception {
        try {
            Pageable limit = PageRequest.of(0,5);
            return calendarRepository.findCalendarByStatusAndUserOrderByIdDesc(VacationStatus.APPROVED, user,limit );
        } catch (Exception e) {
            LOG.error("ERROR: No se pudieron recuperar los datos " + e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public void deleteAllFromUser(User user){
        calendarRepository.deleteAllByUser(user);
    }

    public List<Calendar> getAllFromUser(User user) throws Exception {
        try {
            return calendarRepository.findByUser(user);
        } catch (Exception e) {
            LOG.error("ERROR: No se pudieron recuperar los datos " + e.getMessage(), e);
            throw new Exception(e);
        }
    }
}
