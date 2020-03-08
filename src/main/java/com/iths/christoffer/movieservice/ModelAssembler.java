package com.iths.christoffer.movieservice;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ModelAssembler implements RepresentationModelAssembler<Movie, EntityModel<Movie>> {


    @Override
    public EntityModel<Movie> toModel(Movie entity) {
        return  new EntityModel<>(entity,
                linkTo(methodOn(MoviesController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(MoviesController.class).all()).withRel("movies"));
    }

    @Override
    public CollectionModel<EntityModel<Movie>> toCollectionModel(Iterable<? extends Movie> entities) {
        var collection = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(collection,
                linkTo(methodOn(MoviesController.class).all()).withSelfRel());
    }
}
