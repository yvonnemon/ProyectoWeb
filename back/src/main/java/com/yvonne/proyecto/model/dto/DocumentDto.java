package com.yvonne.proyecto.model.dto;

import com.yvonne.proyecto.model.User;

import javax.persistence.*;
import java.util.List;

public class DocumentDto {

    private Integer id;

    private String name;

    private String path;

    private UserDto user;

    private List<FileObject> fileArray;

    public List<FileObject> getFileArray() {
        return fileArray;
    }

    public void setFileArray(List<FileObject> fileArray) {
        this.fileArray = fileArray;
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
