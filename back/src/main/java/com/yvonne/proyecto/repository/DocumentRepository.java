package com.yvonne.proyecto.repository;

import com.yvonne.proyecto.model.Document;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document, Integer> {
}
