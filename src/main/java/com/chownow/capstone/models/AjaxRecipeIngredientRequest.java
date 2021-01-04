package com.chownow.capstone.models;

public class AjaxRecipeIngredientRequest {
    private long id;
    private String name;
    private String unit;
    private double amount;

    public AjaxRecipeIngredientRequest(){}

    public AjaxRecipeIngredientRequest(String name, String unit, double amount) {
        this.name = name;
        this.unit = unit;
        this.amount = amount;
    }

    public AjaxRecipeIngredientRequest(long id, String name, String unit, double amount) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
