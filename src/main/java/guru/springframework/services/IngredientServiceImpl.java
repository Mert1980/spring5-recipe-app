package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.IngredientRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeID, Long ingredientId) {

        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeID);

        if(!recipeOptional.isPresent()){
            //todo implement error handling
            log.error("Recipe id not found:" + recipeID);
        }

        Recipe recipe = recipeOptional.get();

        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map(ingredient -> {
                    System.out.println("1 " + ingredientToIngredientCommand);
                    return ingredientToIngredientCommand.convert(ingredient);
                        }).findFirst();



        if(!ingredientCommandOptional.isPresent()){
            //todo implement error handling
            log.error("Ingredient id not found: " + recipeID);
        }
        return ingredientCommandOptional.get();
    }

    @Override
    @Transactional
    public IngredientCommand saveIngredientCommand(IngredientCommand command) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(command.getRecipeId());

        if(!recipeOptional.isPresent()){

            //todo toss error if not found!
            log.error("Recipe not found for id: " + command.getRecipeId());
            return new IngredientCommand();
        } else {
            Recipe recipe = recipeOptional.get();

            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(command.getId()))
                    .findFirst();

            if(ingredientOptional.isPresent()){
                Ingredient ingredientFound = ingredientOptional.get();
                ingredientFound.setDescription(command.getDescription());
                ingredientFound.setAmount(command.getAmount());
                ingredientFound.setUom(unitOfMeasureRepository
                        .findById(command.getUnitOfMeasure().getId())
                        .orElseThrow(() -> new RuntimeException("UOM NOT FOUND"))); //todo address this
            } else {
                //add new Ingredient
                Ingredient ingredient = ingredientCommandToIngredient.convert(command);
                ingredient.setRecipe(recipe);
                recipe.addIngredient(ingredient);
            }

            Recipe savedRecipe = recipeRepository.save(recipe);

            Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                    .findFirst();

            //check by description
            if(!savedIngredientOptional.isPresent()){
                //not totally safe... But best guess
                savedIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
                        .filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
                        .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(command.getUnitOfMeasure().getId()))
                        .findFirst();
            }

            //to do check for fail
            return ingredientToIngredientCommand.convert(savedIngredientOptional.get());
        }
    }

    @Override
    public void deleteById(Long recipeId, Long ingredientId){
        log.debug("Deleting ingredient: " + recipeId + " " + ingredientId);
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

        if(recipeOptional.isPresent()){
            Recipe recipe = recipeOptional.get();
            log.debug("Found recipe");
            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId() == ingredientId)
                    .findFirst();

            if(ingredientOptional.isPresent()){
                log.debug("Found ingredient");
                Ingredient ingredientToDelete = ingredientOptional.get();
                ingredientToDelete.setRecipe(null);
                recipeOptional.get().getIngredients().remove(ingredientToDelete);
                recipeRepository.save(recipe);
            }
        } else {
            log.debug("Recipe Id not found: " + recipeId);
        }
    }
}

/**
 * Notes for deleteById Method
 * CASCADE: When some action is performed on an entity, the same action is performed on its children entities.
 *
 * In this case, the parent entity is the Recipe object (on which we defined the cascade type) and the children are
 * the Ingredients Objects. And we used CascadeTypes.All as the cascade operations.
 *
 * Now let's take a look at the different cascade types:
 *
 * PERSIST: when the parent is saved, the children are saved too
 * MERGE: when the parent is merged with a same identifier entity, the children are merged with their corresponding
 * children of the same identifier entity too
 * REMOVE (or DELETE in Hibernate): when the parent row is removed from the database, the children rows are also
 * removed from the database. Thus, deleting the parent deletes the children (not the other way round!).
 * DETACH: when the parent is detached from the persistent context, the children are detached too
 * LOCK: when the parent is re-attached to the persistence context (what lock does), the children are re-attached too
 * REFRESH: when the parent is refreshed (reloaded from the database), the children are refreshed too
 * REPLICATE: when the parent is replicated, the children are replicated too
 * SAVE_UPDATE: when the parent is saved/updated, the children are saved/updated too
 * (if you want more details, take a look at this which I based my answer on)
 *
 * Now, let's identify the JPA/Hibernate operation associated to the delete ingredient:
 *
 * when deleting an ingredient, we remove it from the Set of Ingredients of the Recipe and we removed the reference
 * to the recipe in the ingredient. None of the Recipe or the Ingredient were actually deleted (no REMOVE operation
 * of any kind), the only JPA/Hibernate operation here is a MERGE when calling the save of the RecipeRepository
 * (we merge the Recipe we save with the one in the Database with the same Primary Key)! So this will just update
 * the recipe and its children in the database, the ingredient we wanted to delete actually just has its recipe field
 * updated to point to null. Which is why it is not deleted in the database despite the CASCADE.
 */

/**
 * ADDITIONAL NOTES:
 * if you want to delete the ingredient in the database without implementing a repository for the ingredient
 * (but this works too of course), there is another way, orphanRemoval = true. This will automatically remove
 * from the database (delete thus) any children Ingredient entity which is no more referenced in the ingredients
 * variable of the Recipe entity.
 *
 * Actually, even if you implemented an IngredientRepository, orphanRemoval = true is recommended. That's because
 * it allows you for example to automatically delete all the ingredients of a Recipe by just calling a clear() on
 * the ingredients variable, without having to resort calling the IngredientRepository and having to implement a query
 * to delete all the ingredients in the database where recipe_id = your-recipe. It is easier to manage the ingredients
 * that way.
 *
 * However, because of how Thymeleaf manages the forms, you will have to modify the saveOrUpdate of the recipe if
 * you add orphanRemoval = true. That's because as the ingredients are just displayed in the html and are not in
 * input th:field tags (and simply adding th:field="*{ingredients}" does not work because of conversion issues
 * between String type and Set<Ingredient>), basically, when submitting your recipeform, the ingredients field is
 * always null. There aren't many solutions for this, either you manage to add the Set<Ingredient> back in the
 * recipeform submitted (it is complicated and cumbersome, I tried it first but finally dropped that idea), or
 * either you modify you RecipeServiceImpl like this:
 *
 * ...
 *
 * @Override
 * @Transactional
 * public RecipeCommand saveRecipeCommand(final RecipeCommand recipeCommand) {
 *     Recipe detachedRecipe = recipeCommandToRecipe.convert(recipeCommand);
 *
 *     if (detachedRecipe == null) {
 *         throw new RuntimeException("Trying to save or update null recipe");
 *     }
 *
 *     Recipe savedRecipe = recipeRepository.save(this.editRecipe(detachedRecipe));
 *
 *     if (log.isDebugEnabled()) {
 *         log.debug("Saved RecipeId:" + savedRecipe.getId());
 *     }
 *
 *     return recipeToRecipeCommand.convert(savedRecipe);
 *
 * }
 *
 * ...
 *
 * private Recipe editRecipe(final Recipe detachedRecipe) {
 *     if (detachedRecipe.getId() == null) {
 *         // new recipe so no editing
 *         return detachedRecipe;
 *     }
 *     Recipe recipeToUpdate = this.findById(detachedRecipe.getId());
 *     recipeToUpdate.setDescription(detachedRecipe.getDescription());
 *     recipeToUpdate.setCategories(detachedRecipe.getCategories());
 *     recipeToUpdate.setPrepTime(detachedRecipe.getPrepTime());
 *     recipeToUpdate.setCookTime(detachedRecipe.getCookTime());
 *     recipeToUpdate.setDifficulty(detachedRecipe.getDifficulty());
 *     recipeToUpdate.setServings(detachedRecipe.getServings());
 *     recipeToUpdate.setSource(detachedRecipe.getSource());
 *     recipeToUpdate.setUrl(detachedRecipe.getUrl());
 *     recipeToUpdate.setDirections(detachedRecipe.getDirections());
 *     recipeToUpdate.setNotes(detachedRecipe.getNotes());
 *
 *     return recipeToUpdate;
 * }
 *
 *
 * This is the implementation that I chose, and actually, it is recommended to manually update the objects like in this
 * editRecipe method. Indeed, as the Objects' models become more and more complex, it becomes increasingly dangerous
 * to directly save in the database your updated object directly from the form, because the JPA merge operation can
 * sometimes lead to unexpected issues. Furthermore, this allows you to add guards before updating your object,
 * for example by checking that the values entered by the user are valid before saving (which you should always do,
 * especially if your application will be visible to the public because front end guards can easily be bypassed and
 * you should NEVER trust user input from front-end).
 * */

