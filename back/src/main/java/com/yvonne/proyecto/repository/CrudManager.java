package com.yvonne.proyecto.repository;

import java.util.List;

public interface CrudManager<T> {
    List<T> getAll();
    void create(T object);
    void delete(T object);
    Boolean update(T object);
    T getById(Integer id);
}
