package com.recicar.marketplace.dto;

/**
 * Single row for vehicle compatibility (fitment) table on product detail.
 */
public class VehicleCompatibilityDto {

    private String make;
    private String model;
    private Integer yearFrom;
    private Integer yearTo;
    private String engine;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public void setYearTo(Integer yearTo) {
        this.yearTo = yearTo;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getYearRangeLabel() {
        if (yearFrom == null || yearTo == null) {
            return "";
        }
        return yearFrom.equals(yearTo) ? String.valueOf(yearFrom) : yearFrom + " – " + yearTo;
    }
}
