package com.demo.rest;

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;

@Repository
public interface Authors extends PageableRepository<Author, String> {

    Page<Author> findAll(Pageable pageable);

}
