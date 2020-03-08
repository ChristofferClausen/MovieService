package com.iths.christoffer.movieservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

//@EnableEurekaClient
@RestController
@Slf4j
@RequestMapping("/api/v1/movies")
public class MoviesController {

    final MoviesRepository repository;
    final ModelAssembler assembler;

    @Autowired
    RestTemplate restTemplate;
    HttpClient client;

    public MoviesController(MoviesRepository repository, ModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping()
    public CollectionModel<EntityModel<Movie>> all() {
        log.debug("called: all()");
        return assembler.toCollectionModel(repository.findAll());
    }

    @GetMapping(value = "/{id:[0-9]}")
    public ResponseEntity<EntityModel<Movie>> one(@PathVariable long id) {
        log.info("Called: one");
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin")
    public ResponseEntity<EntityModel<Movie>> createMovie(@RequestBody Movie movie) {
        log.info("Called: createMovie()");
        var m = repository.save(movie);
        log.info("Saved: " + m);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(MoviesController.class).slash(m.getId()).toUri());
        return new ResponseEntity<>(assembler.toModel(m), headers, HttpStatus.CREATED);
    }

    @PatchMapping("/admin/{id}")
    public ResponseEntity<EntityModel<Movie>> updateMovie(@RequestBody Movie newMovie, @PathVariable long id) {
        log.info("Called: updateMovie()");
        return repository.findById(id).map(m -> {
            if (!newMovie.getName().isEmpty())
                m.setName(newMovie.getName());
            repository.save(m);
            log.debug("Object updated");
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(linkTo(MoviesController.class).slash(m.getId()).toUri());
            return new ResponseEntity<>(assembler.toModel(m), headers, HttpStatus.OK);
        }).orElseGet(() -> {
            log.debug("No fields updated in movie with id: " + repository.getOne(id).getId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        });
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<EntityModel<Movie>> replaceMovie(@RequestBody Movie newMovie, @PathVariable long id) {
        log.info("Called: replaceMovie()");
        return repository.findById(id).map(m -> {
            m.setName(newMovie.getName());
            repository.save(m);
            log.debug("Object replaced");
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(linkTo(MoviesController.class).slash(m.getId()).toUri());
            return new ResponseEntity<>(assembler.toModel(m), headers, HttpStatus.OK);
        }).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable long id) {
        log.info("Called: deleteMovie");
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.debug("Object deleted");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
