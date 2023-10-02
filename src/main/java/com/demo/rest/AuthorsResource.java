package com.demo.rest;

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
@Path("/authors")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class AuthorsResource {

    @Inject
    private Authors authors;

    @Inject
    public AuthorsResource(Authors authors) {
        this.authors = authors;
    }

    public AuthorsResource() {
    }

    @GET
    public List<AuthorResponse> getAuthors(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        Page<Author> foundedAuthors = authors.findAll(Pageable.ofPage(page).size(pageSize));
        return foundedAuthors.stream().map(AuthorResponse::of).toList();
    }

    @GET
    @Path("/{id}")
    public AuthorResponse getAuthor(@PathParam("id") String id) {
        return authors.findById(id)
                .map(AuthorResponse::of)
                .orElseThrow(()->new WebApplicationException(Response.Status.NOT_FOUND));
    }

    public record AuthorResponse(String id, String name, List<BookInfo> books) {
        public static AuthorResponse of(Author author) {
            return new AuthorResponse(author.getId(), author.getName(),author.getBooks().stream().map(BookInfo::of).toList());
        }
    }

    public record BookInfo(String id, String title, String publisher) {
        public static BookInfo of(Book book) {
            return new BookInfo(book.getId(), book.getTitle(), book.getPublisher());
        }
    }
}
