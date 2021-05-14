package com.yvonne.proyecto.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.VacationStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

public class CalendarDto {

    private Date startDate;

    private Date endDate;

    private VacationStatus status;

    private String comment;

    private User user;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public VacationStatus getStatus() {
        return status;
    }

    public void setStatus(VacationStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
