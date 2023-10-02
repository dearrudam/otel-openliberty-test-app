package com.demo.rest;

import com.github.javafaker.Faker;
import jakarta.json.bind.annotation.JsonbVisibility;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Entity
@JsonbVisibility(FieldVisibilityStrategy.class)
public class Author {

    public static Author newAuthor(Faker faker) {
        var fakerAuthor = faker.book().author();
        return newAuthor(fakerAuthor);
    }

    private static Author newAuthor(String name) {
        return new Author(UUID.randomUUID().toString(), name);
    }

    @Id
    private String id;

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "author_book",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Book> books = new LinkedHashSet<>();

    /**
     * don't use it
     */
    @Deprecated
    public Author() {
    }

    protected Author(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Book> getBooks() {
        return Set.copyOf(books);
    }

    public void add(Book... books) {
        Stream.of(books).forEach(this::add);
    }

    public void add(Book book) {
        if (this.books.add(book)) {
            book.assignTo(this);
        }
    }

    public void remove(Book... books) {
        Stream.of(books).forEach(this::remove);
    }

    public void remove(Book book) {
        if (this.books.remove(book)) {
            book.unassignedFrom(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
