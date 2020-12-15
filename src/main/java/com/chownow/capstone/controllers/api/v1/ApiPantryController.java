package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.Pantry;
import com.chownow.capstone.repos.PantryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/pantries")
public class ApiPantryController {
    @Autowired
    private PantryRepository pantryDao;

    /* GET MAPPINGS */

    // get all users
    @GetMapping
    public @ResponseBody
    List<Pantry> getAllPantries(){
        return pantryDao.findAll();
    }

    // get pantry by id
    @GetMapping("/{id}")
    public @ResponseBody Pantry getPantry(@PathVariable(value="id") long pantryId) {
        Optional<Pantry> pantry = pantryDao.findById(pantryId);
        return pantry.orElse(null);
    }

    /* POST MAPPINGS FOR CRUD */

    // create pantry
    @PostMapping("/new")
    public @ResponseBody
    Pantry createPantry(@RequestBody Pantry pantry){
        return pantryDao.save(pantry);
    }

    // update pantry
    @PostMapping("/{id}/edit")
    public @ResponseBody Pantry updatePantry(@RequestBody Pantry requestPantry,@PathVariable (value = "id")long pantryId){
        Optional<Pantry> pantry = pantryDao.findById(pantryId);
        if(pantry.isPresent()) {
            Pantry dbPantry = pantry.get();
            return pantryDao.save(dbPantry);
        }
        return null;
    }

    // delete pantry
    @PostMapping("/{id}/delete")
    public ResponseEntity<Pantry> deletePantry(@PathVariable ("id") long pantryId){
        Optional<Pantry> pantry = pantryDao.findById(pantryId);
        if(pantry.isPresent()) {
            Pantry dbPantry = pantry.get();
            pantryDao.delete(dbPantry);
        }
        return ResponseEntity.ok().build();
    }
}
