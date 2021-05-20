package com.yvonne.proyecto.controller.crud;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CrudController <T>{
    ResponseEntity<List<T>> getAll() throws Exception;

    T getOne(T data) throws Exception;

    ResponseEntity create(T data) throws Exception;

    ResponseEntity<String> update(T data) throws Exception;

    ResponseEntity<String> delete(String id) throws Exception;

}
