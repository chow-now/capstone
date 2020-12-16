package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.Image;
import com.chownow.capstone.repos.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
public class ApiImageController {
    @Autowired
    private ImageRepository imageDao;

    /* GET MAPPINGS */

    // get all images
    @GetMapping
    public @ResponseBody
    List<Image> getAllImages(){
        return imageDao.findAll();
    }

    // get image by id
    @GetMapping("/{id}")
    public @ResponseBody Image getImage(@PathVariable(value="id") long imageId) {
        Optional<Image> image = imageDao.findById(imageId);
        return image.orElse(null);
    }

    /* POST MAPPINGS FOR CRUD */

    // create image
    @PostMapping("/new")
    public @ResponseBody
    Image createImage(@RequestBody Image image){
        return imageDao.save(image);
    }

    // update image
    @PostMapping("/{id}/edit")
    public @ResponseBody Image updateImage(@RequestBody Image requestImage,@PathVariable (value = "id")long imageId){
        Optional<Image> image = imageDao.findById(imageId);
        if(image.isPresent()) {
            Image dbImage = image.get();
            return imageDao.save(dbImage);
        }
        return null;
    }

    // delete image
    @PostMapping("/{id}/delete")
    public ResponseEntity<Image> deleteImage(@PathVariable ("id") long imageId){
        Optional<Image> image = imageDao.findById(imageId);
        if(image.isPresent()) {
            Image dbImage = image.get();
            imageDao.delete(dbImage);
        }
        return ResponseEntity.ok().build();
    }
}
