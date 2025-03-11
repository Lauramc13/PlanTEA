package com.example.plantea.presentacion.actividades

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import com.example.plantea.R
import org.junit.Before
import org.junit.Test

class CrearPlanActivityTest {
    private lateinit var resource : CrearPlanActivity

    @get:Rule
    val activityRule = ActivityScenarioRule(CrearPlanActivity::class.java)

    @Before
    fun setUp() {
        activityRule.scenario.onActivity { activity ->
            resource = activity
        }
    }

   /* @Test
    fun clickPicto(){
      /*  onView(withId(R.id.categoria_medico)).perform(click())

        //click on a pictogram of the listaPictogramas
        onView(withId(R.id.contenedor_fragments)).perform(click())
*/

        //resource.mostrarCategoria(1)
    }*/

}