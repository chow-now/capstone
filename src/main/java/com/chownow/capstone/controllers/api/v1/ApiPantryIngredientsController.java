package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.PantryIngredient;
import com.chownow.capstone.repos.PantryIngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pantry-ingredients")
public class ApiPantryIngredientsController {
    @Autowired
    private PantryIngredientRepository pantryIngredientDao;

    /* GET MAPPINGS */

    // get all pantryIngredients
    @GetMapping
    public @ResponseBody
    List<PantryIngredient> getAllPantryIngredients(){
        return pantryIngredientDao.findAll();
    }

    // get pantryIngredient by id
    @GetMapping("/{id}")
    public @ResponseBody PantryIngredient getPantryIngredient(@PathVariable(value="id") long pantryIngredientId) {
        Optional<PantryIngredient> pantryIngredient = pantryIngredientDao.findById(pantryIngredientId);
        return pantryIngredient.orElse(null);
    }

    /* POST MAPPINGS FOR CRUD */

    // create pantryIngredient
    @PostMapping("/new")
    public @ResponseBody
    PantryIngredient createPantryIngredient(@RequestBody PantryIngredient pantryIngredient){
        return pantryIngredientDao.save(pantryIngredient);
    }

    // update pantryIngredient
    @PostMapping("/{id}/edit")
    public @ResponseBody PantryIngredient updatePantryIngredient(@RequestBody PantryIngredient requestPantryIngredient,@PathVariable (value = "id")long pantryIngredientId){
        Optional<PantryIngredient> pantryIngredient = pantryIngredientDao.findById(pantryIngredientId);
        if(pantryIngredient.isPresent()) {
            PantryIngredient dbPantryIngredient = pantryIngredient.get();
            return pantryIngredientDao.save(dbPantryIngredient);
        }
        return null;
    }

    // delete pantryIngredient
    @PostMapping("/{id}/delete")
    public ResponseEntity<PantryIngredient> deletePantryIngredient(@PathVariable ("id") long pantryIngredientId){
        Optional<PantryIngredient> pantryIngredient = pantryIngredientDao.findById(pantryIngredientId);
        if(pantryIngredient.isPresent()) {
            PantryIngredient dbPantryIngredient = pantryIngredient.get();
            pantryIngredientDao.delete(dbPantryIngredient);
        }
        return ResponseEntity.ok().build();
    }

}
