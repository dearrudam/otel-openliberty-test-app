package com.demo.rest;

import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;

@Repository
public interface Books extends PageableRepository<Book, String> {
}
