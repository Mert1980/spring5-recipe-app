package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecipeServiceIT {

    public static final String NEW_DESCRIPTION = "New Description";

    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Autowired
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Transactional
    @Test
    public void testSaveOfDescription() throws Exception {
        //given
        Iterable<Recipe> recipes = recipeRepository.findAll();
        Recipe testRecipe = recipes.iterator().next();
        RecipeCommand testRecipeCommand = recipeToRecipeCommand.convert(testRecipe);

        //when
        testRecipeCommand.setDescription(NEW_DESCRIPTION);
        RecipeCommand savedRecipeCommand = recipeService.saveRecipeCommand(testRecipeCommand);

        //then
        assertEquals(NEW_DESCRIPTION, savedRecipeCommand.getDescription());
        assertEquals(testRecipe.getId(), savedRecipeCommand.getId());
        assertEquals(testRecipe.getCategories().size(), savedRecipeCommand.getCategories().size());
        assertEquals(testRecipe.getIngredients().size(), savedRecipeCommand.getIngredients().size());
    }
}

// NOTES
/* Each call to Spring Data JPA executes in separate transactions. To encompass them in one single transaction,
the @Transactional annotation is used. Assume you are not using @Transactional. For any database operation a
transaction is required. In Sring Data JPA, all JPA repository methods comes with @Transactional annotation applied.

The transactions for these method calls begin when the method is called and ends when the method execution is
finished (unless there is an outer transaction of the same database)

Consider following lines,

User savedUser = usersRepository.findById(ID);
Address address = savedUser.getAddresses().get(0);

A transaction starts and ends in the usersRepository.findById(ID) itself. Now "User savedUser" object has addresses
which will be loaded lazily. So when you call savedUser.getAddresses(), it tries to load lazily using the persistence
session. As the transaction context was already closed there is no active associated session. And you get the infamous
LazyInitializationException.

On adding @Transactional, a transaction begins in usersRepository.findById itself and now when do
savedUser.getAddresses(), it gets called within this same transaction. And now when you do savedUser.getAddresses(),
there is an associated session with it and therefore it will work properly.
*
* */
