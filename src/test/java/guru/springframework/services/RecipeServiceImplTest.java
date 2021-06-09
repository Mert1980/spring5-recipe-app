package guru.springframework.services;

import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
/*
We don't mock the object under test. We mock its dependencies.  Now the service implementation is the object
under test and it is dependent on repository. Therefore we mocked the repository.

Going ahead when we have a controller that calls the service, and we want to test the controller,
we will then mock the service.
 */
public class RecipeServiceImplTest {

    RecipeServiceImpl recipeService;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);
    }

    @Test
    public void getRecipes() {
        Recipe recipe = new Recipe();
        HashSet recipesData = new HashSet();
        recipesData.add(recipe);

        // If a call is made to the findAll() method of the RecipeRepository mock object,
        // then the mock should return recipesData
        when(recipeRepository.findAll()).thenReturn(recipesData);

        Set<Recipe> recipes = recipeService.getRecipes();

        assertEquals( 1, recipes.size());
        // verify that the findAll() method of the mock recipeRepository is called exactly once
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    public void findById(){
        Long id = 1L;
        Recipe recipe = new Recipe();
        recipe.setId(id);

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        Recipe returnedRecipe = recipeService.findById(id);

        assertNotNull("Null recipe returned", returnedRecipe);
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, never()).findAll();
        assertEquals(id, returnedRecipe.getId());
    }

    @Test
    public void deleteById(){
        //given
        Long idToDelete = 2L;

        //when
        recipeService.deleteById(idToDelete);

        //then
        verify(recipeRepository, times(1)).deleteById(anyLong());
    }

}