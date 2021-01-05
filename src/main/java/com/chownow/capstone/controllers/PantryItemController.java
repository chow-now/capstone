package com.chownow.capstone.controllers;

import com.chownow.capstone.models.AjaxPantryIngredientRequest;
import com.chownow.capstone.models.Ingredient;
import com.chownow.capstone.models.PantryIngredient;
import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.IngredientRepository;
import com.chownow.capstone.repos.PantryIngredientRepository;
import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class PantryItemController {

    @Autowired
    private IngredientRepository ingredientDao;

    @Autowired
    private PantryIngredientRepository pantryIngDao;

    @Autowired
    private UserService userServ;

    // CREATE A NEW PANTRY ITEM
    @RequestMapping(value = "/pantry/ingredient/new", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public @ResponseBody
    String postPantryIngredient(@RequestBody AjaxPantryIngredientRequest pantryIngredient) {
        User currentUser = userServ.loggedInUser();
        Ingredient dbIngredient = null;
        boolean isNotInDb = true;
        for (Ingredient i : ingredientDao.findAllByNameLike(pantryIngredient.getName())) {
            if (pantryIngredient.getName().equalsIgnoreCase(i.getName())) {
                dbIngredient = i;
                isNotInDb = false;
                break;
            }
        }
        if (isNotInDb) {
            dbIngredient = ingredientDao.save(new Ingredient(pantryIngredient.getName().toLowerCase()));
        }
        PantryIngredient newPantryIng = new PantryIngredient(
                pantryIngredient.getAmount(),
                pantryIngredient.getUnit(),
                currentUser.getPantry(),
                dbIngredient
        );
        pantryIngDao.save(newPantryIng);
        return "im done";
    }

    // EDIT PANTRY INGREDIENT
    @RequestMapping(value = "/pantry/ingredient/edit", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public @ResponseBody
    String editPantryIngredient(@RequestBody AjaxPantryIngredientRequest pantryIngredient) {
        PantryIngredient pi = pantryIngDao.getOne(pantryIngredient.getId());
        pi.setAmount(pantryIngredient.getAmount());
        pi.setUnit(pantryIngredient.getUnit());
        pantryIngDao.save(pi);
        return "update complete";
    }
}
