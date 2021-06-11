package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final RecipeRepository recipeRepository;

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
}
