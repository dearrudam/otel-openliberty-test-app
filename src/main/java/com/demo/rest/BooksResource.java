package com.demo.rest;

import jakarta.data.repository.Pageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
@Path("/books")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BooksResource {

    private Books books;

    @Inject
    public BooksResource(Books books) {
        this.books = books;
    }

    public BooksResource() {
        this(null);
    }

    @GET
    public List<BookResponse> getBooks(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        return books.findAll(Pageable.ofPage(page).size(pageSize)).stream().map(BookResponse::of).toList();
    }

    @GET
    @Path("/{id}")
    public BookResponse getBook(@PathParam("id") String id) {
        return books.findById(id)
                .map(BookResponse::of)
                .orElseThrow(()->new WebApplicationException(Response.Status.NOT_FOUND));
    }

    public record BookResponse(String id, String title, String publisher, List<AuthorInfo> authors) {
        public static BookResponse of(Book book) {
            return new BookResponse(book.getId(), book.getTitle(), book.getPublisher(), book.getAuthors().stream().map(AuthorInfo::of).toList());
        }
    }

    public record AuthorInfo(String id, String name) {
        public static AuthorInfo of(Author author) {
            return new AuthorInfo(author.getId(), author.getName());
        }
    }
}
