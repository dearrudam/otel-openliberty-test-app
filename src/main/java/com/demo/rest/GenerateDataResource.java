package com.demo.rest;

import com.github.javafaker.Faker;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
@Path("/generate-data")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class GenerateDataResource {

    private final Faker faker = new Faker();
    private Authors authorsRepo;
    private Books booksRepo;

    @Inject
    public GenerateDataResource(Authors authors,Books booksRepo) {
        this.authorsRepo = authors;
        this.booksRepo = booksRepo;
    }

    public GenerateDataResource() {
        this(null,null);
    }

    public record GeneratedData(long numberOfBooks, long numberOfAuthors) {
    }

    @GET
    public GeneratedData populate() {

        var books = IntStream.range(1, faker.number().numberBetween(1, 10))
                .boxed()
                .map(bookNum -> Book.newBook(faker))
                .map(book -> Map.of(book.getTitle(),book))
                .reduce(new HashMap<>(),(a,b)->{
                    a.putAll(b);
                    return a;
                })
                .values();

        var authors = new LinkedList<>(IntStream.range(1, faker.number().numberBetween(1, 10))
                .boxed()
                .map(num -> Author.newAuthor(faker))
                .map(author -> Map.of(author.getName(), author))
                .reduce(new HashMap<>(), (a, b) -> {
                    a.putAll(b);
                    return a;
                })
                .values());


        books.forEach(book -> {
            var owners = faker.number().numberBetween(1, 3);
            while (owners > 0) {
                var author = authors.removeFirst();
                author.add(book);
                authors.addLast(author);
                authorsRepo.save(author);
                owners--;
            }
        });

        return new GeneratedData(authors.size(), books.size());
    }

}
