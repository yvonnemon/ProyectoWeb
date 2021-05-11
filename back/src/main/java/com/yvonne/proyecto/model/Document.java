package com.yvonne.proyecto.model;

import javax.persistence.*;

@Entity
@Table(name="document")
public class Document {

    @Id
    @Column(name = "document_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comment", nullable = false, unique = true)
    private String name;

    @Column(name = "path", nullable = false, unique = true)
    private String path;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

