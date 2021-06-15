package guru.springframework.controllers;
import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IngredientController {

    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final UnitOfMeasureService unitOfMeasureService;

    @GetMapping
    @RequestMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable Long recipeId, Model model){
        log.debug("Getting ingredient list for recipe id: " + recipeId);

        // use command object to avoid lazy load errors in Thymeleaf.
        // It's an anti pattern to transfer persistent entities between layers - more specifically between
        // the presentation and backend layers. A Command object is a specialized DTO for MVC.
        // Therefore,  RecipeCommand is passed instead of Recipe entity in IngredientController.java
        // Same in the Thymeleaf view, even though there is no form binding.
        model.addAttribute("recipe", recipeService.findCommandById(recipeId));

        return "recipe/ingredient/list";
    }

    @GetMapping
    @RequestMapping("recipe/{recipeId}/ingredient/{ingredientId}/show")
    public String showRecipeIngredient(@PathVariable Long recipeId,
                                       @PathVariable Long ingredientId,
                                       Model model){

        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, ingredientId));
        return "recipe/ingredient/show";
    }

    @GetMapping
    @RequestMapping("recipe/{recipeId}/ingredient/new")
    public String createRecipeIngredient(@PathVariable Long recipeId, Model model){
        // why do we add recipeCommand? We don't use it.
        // todo raise exception if null
        RecipeCommand recipeCommand = recipeService.findCommandById(recipeId);

        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(recipeId);
        model.addAttribute("ingredient", ingredientCommand);

        //init uom
        ingredientCommand.setUnitOfMeasure(new UnitOfMeasureCommand());
        model.addAttribute("uomList", unitOfMeasureService.listAllUoms());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping
    @RequestMapping("recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(Long.valueOf(recipeId), Long.valueOf(id)));

        model.addAttribute("uomList", unitOfMeasureService.listAllUoms());
        return "recipe/ingredient/ingredientform";
    }


    /**
     * When the enclosing form is posted, a POST request gets sent to http://localhost:8080/recipe/2/ingredient
     *
     * The form data goes in this way:
     *
     * id: 26
     * description: radishes, thinly sliced
     * amount: 4.00
     * uom.id: 5
     * The last piece of data is relevant to your select.
     *
     * Once it reach the controller, the data gets mapped to IngredientCommand in the endpoint method saveOrUpdate()
     * of the IngredientController .
     *
     * */

    /**
     * The path in @Post maps an incoming HTTP POST request to the handler method saveOrUpdate().
     *
     * So whenever an ingredient is updated for a recipe (say with recipe ID 3),
     * then a POST request comes from the frontend as http://localhost:8080/recipe/3/ingredient.
     * This request body contains the updated ingredient.
     *
     * When the request arrives, Spring finds two things:
     * 1: It is a POST request
     * 2: It has a path recipe/<recipe_id>/ingredient
     *
     * Spring then matches and figures out that the request is meant for the method
     *
     * PostMapping("/recipe/{recipeId}/ingredient")
     * public String saveOrUpdate(@ModelAttribute IngredientCommand command){
     * ...
     * }
     *
     * Now this method performs the database operation and returns a redirect String as:
     *
     * /recipe/" + savedCommand.getRecipeId() + "/ingredient/" + savedCommand.getId() + "/show
     * Spring, based on the preceding redirect String calls the following showRecipeIngredient() method of
     * IngredientController.
     *
     * @GetMapping
     * @RequestMapping("recipe/{recipeId}/ingredient/{id}/show")
     * public String showRecipeIngredient(@PathVariable String recipeId,
     *                                    @PathVariable String id, Model model){
     *     model.addAttribute("ingredient", ingredientService
     *                                      .findByRecipeIdAndIngredientId(Long.valueOf(recipeId), Long.valueOf(id)));
     *     return "recipe/ingredient/show";
     * }
     * Note that this method returns the show.html thymeleaf template that will finally display the updated ingredient.
     *
     * */
    @PostMapping("recipe/{recipeId}/ingredient")
    public String saveOrUpdate(@ModelAttribute IngredientCommand command){
        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command);

        log.debug("saved receipe id:" + savedCommand.getRecipeId());
        log.debug("saved ingredient id:" + savedCommand.getId());

        return "redirect:/recipe/" + savedCommand.getRecipeId() + "/ingredient/" + savedCommand.getId() + "/show";
    }

    @DeleteMapping("recipe/{recipeId}/ingredient/{ingredientId}/delete")
    public String deleteRecipeIngredient(@PathVariable String recipeId, @PathVariable String ingredientId){
        ingredientService.deleteById(Long.valueOf(recipeId), Long.valueOf(ingredientId));
        return "redirect:/recipe/" + recipeId + "/ingredients";
    }
}
