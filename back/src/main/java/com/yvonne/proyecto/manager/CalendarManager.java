package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.Calendar;
import com.yvonne.proyecto.model.Document;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.VacationStatus;
import com.yvonne.proyecto.model.dto.CalendarDto;
import com.yvonne.proyecto.repository.CalendarRepository;
import com.yvonne.proyecto.repository.CrudManager;
import com.yvonne.proyecto.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class CalendarManager implements CrudManager<Calendar> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CalendarRepository calendarRepository;

    private static final Logger LOG = LogManager.getLogger(Document.class);

    @Override
    public List<Calendar> getAll() {
        try {
            return (List<Calendar>) calendarRepository.findAll();
        } catch (Exception e) {
            LOG.error("ERROR: lista de calendarios vacia" + e.getMessage());
            throw e;
        }
    }

    @Override
    public void create(Calendar object) throws Exception {
    }

    @Override
    public void delete(Calendar object) throws Exception {
    }


    public void create(CalendarDto calendardto) {
        try {
            Calendar calendar = new Calendar();
            LocalDateTime from = LocalDateTime.ofInstant(calendardto.getStartDate().toInstant(), ZoneId.systemDefault());
            LocalDateTime to = LocalDateTime.ofInstant(calendardto.getEndDate().toInstant(), ZoneId.systemDefault());
            calendar.setStartDate(from);
            calendar.setEndDate(to);
            calendar.setComment(calendardto.getComment());
            calendar.setUser(calendardto.getUser());

            calendar.setStatus(VacationStatus.PENDING);
            calendarRepository.save(calendar);
        } catch (Exception e) {
            LOG.error("ERROR: no se puede crear nuevo calendario" + e.getMessage());
            throw e;
        }
    }


    public void delete(CalendarDto vacation) {
        try {
            Calendar calendar = getById(vacation.getId());
            calendar.setStatus(VacationStatus.CANCELED);
            calendarRepository.save(calendar);
        } catch (Exception e) {
            LOG.error("ERROR: no se pudo cancelar " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Boolean update(Calendar calendar) {
        return false;
    }

    public void updateStatus(CalendarDto vacation) {
        try {
            // vacation.setStatus(status);
            Calendar calendar = getById(vacation.getId());
            calendar.setStatus(vacation.getStatus());

            calendarRepository.save(calendar);
        } catch (Exception e) {
            LOG.error("ERROR: no se pudo actualizar el estado " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Calendar getById(Integer id) {
        try {
            return calendarRepository.findById(id).orElse(null);
        } catch (Exception e) {
            LOG.error("ERROR: no existe ese id " + e.getMessage(), e);
            throw e;
        }

    }

    public List<Calendar> getMonthly() {
        try {
            LocalDateTime currentdate = LocalDateTime.now();
            return calendarRepository.findCalendarByStartDateAfter(LocalDateTime.of(currentdate.getYear(), currentdate.getMonthValue(), 1, 0, 0));
        } catch (Exception e) {
            LOG.error("ERROR: No se pudieron recuperar los datos " + e.getMessage(), e);
            throw e;
        }
    }

    public List<Calendar> getPending() {
        try {
            return calendarRepository.findCalendarByStatus(VacationStatus.PENDING);
        } catch (Exception e) {
            LOG.error("ERROR: No se pudieron recuperar los datos " + e.getMessage(), e);
            throw e;
        }
    }

    public List<Calendar> getNext(User user) {
        try {
            Pageable limit = PageRequest.of(0, 5);
            return calendarRepository.findCalendarByStatusAndUserOrderByIdDesc(VacationStatus.APPROVED, user, limit);
        } catch (Exception e) {
            LOG.error("ERROR: No se pudieron recuperar los datos " + e.getMessage(), e);
            throw e;
        }
    }

    public void deleteAllFromUser(User user) {
        calendarRepository.deleteAllByUser(user);
    }

    public List<Calendar> getAllFromUser(User user) {
        try {
            return calendarRepository.findByUser(user);
        } catch (Exception e) {
            LOG.error("ERROR: No se pudieron recuperar los datos " + e.getMessage(), e);
            throw e;
        }
    }
}
