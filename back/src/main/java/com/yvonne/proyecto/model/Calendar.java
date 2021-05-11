package com.yvonne.proyecto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="calendar")
public class Calendar {

    @Id
    @Column(name = "vacation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonFormat(pattern="dd-MM-yyyy")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @JsonFormat(pattern="dd-MM-yyyy")
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @Column(name = "approve_date")
    private LocalDateTime approveDate;

    @Column(name = "status", nullable = false)
    private DocumentStatus status;

    @Column(name = "comment", nullable = true)
    private String comment;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(LocalDateTime approveDate) {
        this.approveDate = approveDate;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Calendar{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", comment='" + comment + '\'' +
                '}';
    }
}
