package com.teamtreehouse.todotoday.dao;

import com.teamtreehouse.todotoday.model.Task;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskDao extends CrudRepository<Task, Long> {
    //extends to CrudRepository and enable spring data data source -> creates the implementation for you.
    //needs smart naming like findByDescription
}
