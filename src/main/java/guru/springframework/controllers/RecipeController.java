package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable Long id, Model model){
        model.addAttribute("recipe", recipeService.findById(id));
        return "recipe/show";
    }

    @GetMapping("recipe/new")
    public String newRecipe(Model model){
        model.addAttribute("recipe", new RecipeCommand());
        return "recipe/recipeform";
    }


    /**
     * @ModelAttribute binds the form post parameters to the RecipeCommand object
     * The method annotated with @PostMapping("recipe") will get called when the recipeform is submitted.
     *     recipeForm contains this code.
     *     <form  th:object="${recipe}" th:action="@{/recipe/}" method="post">
     *
     *  As seen, th:action="@{/recipe/}" is specified, and also importantly, method="post" is specified.
     *  So when a user submits the form on the browser, the browser will send out a POST request like this.
     *
     *      POST http://localhost:8080/recipe
     *
     *  This request will get mapped to the method marked with @PostMapping("recipe")
     */
    @PostMapping("recipe")
    public String saveRecipe(@ModelAttribute RecipeCommand command){
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command);

        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }

    @GetMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable Long id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return  "recipe/recipeform";
    }

    @GetMapping("recipe/{id}/delete")
    public String deleteRecipe(@PathVariable Long id){
        log.debug("Deleting Id: " + id);
        recipeService.deleteById(id);
        return  "redirect:/";
    }

}
