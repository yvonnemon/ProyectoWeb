package com.yvonne.proyecto.repository;

import com.yvonne.proyecto.model.Document;
import com.yvonne.proyecto.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Integer> {
    List<Document> findDocumentByUser(User user);
    List<Document> findAllByOrderByIdDesc(Pageable p);
    List<Document> findDocumentByUserOrderByIdDesc(User user, Pageable P);
    void deleteAllByUser(User user);

}
