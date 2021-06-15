package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;

public interface IngredientService {
    IngredientCommand findByRecipeIdAndIngredientId(Long recipeID, Long ingredientId);
    IngredientCommand saveIngredientCommand(IngredientCommand command);
    IngredientCommand deleteById(Long recipeId ,Long ingredientId);
}
