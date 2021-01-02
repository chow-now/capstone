package com.chownow.capstone.dto;

public class RecipeDto {


        private String title;
        private String image;
        private String prep;
        private String cook;
        private String servings;
        private String summary;
        private String categories;
        private String ingredients;
        private String directions;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getPrep() {
            return prep;
        }

        public void setPrep(String prep) {
            this.prep = prep;
        }

        public String getCook() {
            return cook;
        }

        public void setCook(String cook) {
            this.cook = cook;
        }

        public String getServings() {
            return servings;
        }

        public void setServings(String servings) {
            this.servings = servings;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getCategories() {
            return categories;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }

        public String getIngredients() {
            return ingredients;
        }

        public void setIngredients(String ingredients) {
            this.ingredients = ingredients;
        }

        public String getDirections() {
            return directions;
        }

        public void setDirections(String directions) {
            this.directions = directions;
        }
    }

