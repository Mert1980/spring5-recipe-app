package guru.springframework.controllers;


import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.codehaus.plexus.util.cli.Arg;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class IndexControllerTest {

    IndexController indexController;

    @Mock
    RecipeService recipeService;

    @Mock
    Model model;

    ArgumentCaptor<Set<Recipe>> argumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        indexController = new IndexController(recipeService);
        argumentCaptor = ArgumentCaptor.forClass(Set.class);
    }

    @Test
    public void getIndexPage() {

        //given
        Set<Recipe> recipes = new HashSet<>();
        recipes.add(new Recipe());
        recipes.add(new Recipe());

        //when
        when(recipeService.getRecipes()).thenReturn(recipes);
        String viewName = indexController.getIndexPage(model);

        //then
        assertEquals("index", viewName);
        verify(recipeService, times(1)).getRecipes();
        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());

        // capturedArgument variable contains the value that was passed to the model when the addAttribute method
        // is invoked
        Set<Recipe> capturedArgument = argumentCaptor.getValue();

        System.out.println("Captured argument" + capturedArgument);
        assertEquals(2, capturedArgument.size());
    }
}
