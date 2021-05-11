package com.yvonne.proyecto.repository;

import java.util.List;

public interface CrudManager<T> {
    List<T> getAll() throws Exception;
    void create(T object)  throws Exception;
    void delete(T object)  throws Exception;
    Boolean update(T object)  throws Exception;
    T getById(Integer id)  throws Exception;
}
