package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.Clockin;
import com.yvonne.proyecto.model.Document;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.dto.CalendarDto;
import com.yvonne.proyecto.model.dto.ClockinDto;
import com.yvonne.proyecto.repository.ClockRepository;
import com.yvonne.proyecto.repository.CrudManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClockManager implements CrudManager<Clockin> {

    private static final Logger LOG = LogManager.getLogger(Document.class);

    @Autowired
    private ClockRepository clockRepository;

    @Autowired
    private UserManager userManager;

    @Override
    public List<Clockin> getAll() throws Exception {
        return clockRepository.findAllByOrderByIdAsc();
    }

    @Override
    public void create(Clockin data) throws Exception {

        try {
            Clockin clock = new Clockin();
            ZoneId zone = ZoneId.of("Europe/Paris");
            LocalDateTime in = LocalDateTime.now(zone);
            LocalDateTime out;
            in.toString();
            if(data.getEndDate() != null) {
                out = LocalDateTime.now(zone);
                data.setEndDate(out);
            }
            clock.setStartDate(in);
            clock.setUser(data.getUser());

            clockRepository.save(clock);
        } catch (Exception e){
            LOG.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(Clockin object) throws Exception {

    }

    @Override
    public Boolean update(Clockin object) throws Exception {
        return null;
    }


    public Boolean update(User user) throws Exception {

        List<Clockin> lista = clockRepository.findAllByUserOrderByIdDesc(user);
        Clockin clockin = lista.get(0);

        ZoneId zone = ZoneId.of("Europe/Paris");
        clockin.setEndDate(LocalDateTime.now(zone));
        clockRepository.save(clockin);
        return true;
    }

    @Override
    public Clockin getById(Integer id) throws Exception {
        return null;
    }

    public List<Clockin> getAllFromUser(User user) {
        return clockRepository.findAllByUserOrderByIdDesc(user);
    }

    public List<Clockin> stalkUser(User user) {
        LocalDateTime oneweek = LocalDateTime.now().minusDays(6);
        return clockRepository.findAllByUserAndStartDateAfterOrderByIdDesc(user,oneweek);
    }

}
