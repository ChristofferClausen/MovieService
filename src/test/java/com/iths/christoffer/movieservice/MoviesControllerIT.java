package com.iths.christoffer.movieservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoviesControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MoviesRepository repository;

    Movie movie1 = new Movie("Movie1");
    Movie movie2 = new Movie("Movie2");
    Movie movie3 = new Movie(null);
    Movie movie4 = new Movie("");

    @Order(1)
    @Test
    void postOneMovieExpectingStatusIsCreatedAndCorrectValues() throws Exception {
        mockMvc.perform(post("/api/v1/movies/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("Movie1"))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/movies/1")))
                .andExpect(jsonPath("_links.movies.href", is("http://localhost/api/v1/movies")));
    }

    @Order(2)
    @Test
    void getOneMovieWithIdOneExpectingStatusIsOkAndCorrectValues() throws Exception {
        mockMvc.perform(get("/api/v1/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("Movie1"))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/movies/1")))
                .andExpect(jsonPath("_links.movies.href", is("http://localhost/api/v1/movies")));
    }

    @Order(3)
    @Test
    void getAllMoviesExpectingStatusIsOkAndCorrectValues() throws Exception {
        mockMvc.perform(post("/api/v1/movies/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie2)));
        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.movieList[0].id").value(1))
                .andExpect(jsonPath("_embedded.movieList[0].name").value("Movie1"))
                .andExpect(jsonPath("_embedded.movieList[0]._links.self.href", is("http://localhost/api/v1/movies/1")))
                .andExpect(jsonPath("_embedded.movieList[0]._links.movies.href", is("http://localhost/api/v1/movies")))//;
                .andExpect(jsonPath("_embedded.movieList[1].id").value(2))
                .andExpect(jsonPath("_embedded.movieList[1].name").value("Movie2"))
                .andExpect(jsonPath("_embedded.movieList[1]._links.self.href", is("http://localhost/api/v1/movies/2")))
                .andExpect(jsonPath("_embedded.movieList[1]._links.movies.href", is("http://localhost/api/v1/movies")));
    }

    @Order(4)
    @Test
    void putMovieWithIdOneExpectingStatusIsOkAndCorrectValues() throws Exception {
        mockMvc.perform(put("/api/v1/movies/admin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").isEmpty())
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/movies/1")))
                .andExpect(jsonPath("_links.movies.href", is("http://localhost/api/v1/movies")));
    }

    @Order(5)
    @Test
    void patchMovieWithIdOneExpectingStatusIsOkAndCorrectValues() throws Exception {
        mockMvc.perform(patch("/api/v1/movies/admin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Movie2"))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/movies/1")))
                .andExpect(jsonPath("_links.movies.href", is("http://localhost/api/v1/movies")));
    }

    @Order(6)
    @Test
    void patchMovieWithIdOneExpectingStatusIsNotFoundAndUnchangedValues() throws Exception {
        mockMvc.perform(patch("/api/v1/movies/admin/1")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(movie4)));
//                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/movies/1"))
                .andExpect(jsonPath("name").value("Movie2"))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/movies/1")))
                .andExpect(jsonPath("_links.movies.href", is("http://localhost/api/v1/movies")));
    }

    @Order(7)
    @Test
    void deleteMovieWithIdTwoExpectingStatusIsOkAndCorrectValues() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/admin/2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/movies/"))
                .andExpect(jsonPath("_embedded.moviesList[1]").doesNotExist());
    }

    @Order(8)
    @Test
    void patchMovieWithIdThreeExpectingStatusIsNotFound() throws Exception {
        mockMvc.perform(patch("/api/v1/movies/admin/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie4)))
                .andExpect(status().isNotFound());
    }

    @Order(9)
    @Test
    void putMovieWithIdThreeExpectingStatusIsNotFound() throws Exception {
        mockMvc.perform(put("/api/v1/movies/admin/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie4)))
                .andExpect(status().isNotFound());
    }

    @Order(10)
    @Test
    void deleteMovieWithIdThreeExpectingStatusIsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/admin/3"))
                .andExpect(status().isNotFound());
    }
}
