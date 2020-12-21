package com.chownow.capstone.models;

public class AjaxPantryIngredientRequest {
    private String name;
    private String unit;
    private double amount;

    public AjaxPantryIngredientRequest(){}

    public AjaxPantryIngredientRequest(String name, String unit, double amount) {
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
}
