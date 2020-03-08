package com.iths.christoffer.movieservice;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class Movie {
    @GeneratedValue @Id private long id;
    private String name;

    public Movie(String name) {
        this.name = name;
    }
}
