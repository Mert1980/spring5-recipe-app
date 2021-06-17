package guru.springframework.controllers;

import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
public class ImageController {
    private final ImageService imageService;
    private final RecipeService recipeService;

    @GetMapping("recipe/{recipeId}/image")
    public String showUploadForm(@PathVariable Long recipeId, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(recipeId));
        return "recipe/imageuploadform";
    }

    @PostMapping("recipe/{recipeId}/image")
    public String handleImagePost(@PathVariable Long recipeId, @RequestParam("imagefile") MultipartFile file){
        imageService.saveImageFile(recipeId, file);
        return "redirect:/recipe/" + recipeId + "/show";
    }

}
