
package com.recicar.marketplace.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "car_model")
public class CarModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "make_id", nullable = false)
    @JsonIgnore
    private CarMake make;

    @OneToMany(mappedBy = "model")
    @JsonIgnore
    private List<CarTrim> trims;

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

    public CarMake getMake() {
        return make;
    }

    public void setMake(CarMake make) {
        this.make = make;
    }

    public List<CarTrim> getTrims() {
        return trims;
    }

    public void setTrims(List<CarTrim> trims) {
        this.trims = trims;
    }
}
