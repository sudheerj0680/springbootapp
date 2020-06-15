package com.myproject.orderbook.objects.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Instrument name is required.")
    @Basic(optional = false)
    private String name;

    public Instrument(String name) {
        this.name = name;
    }

    public Instrument() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
