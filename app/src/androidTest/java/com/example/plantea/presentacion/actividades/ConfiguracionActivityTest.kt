package com.example.plantea.presentacion.actividades

import android.graphics.drawable.Drawable
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class ConfiguracionActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ConfiguracionActivity::class.java)

    private lateinit var configuracionActivity: ConfiguracionActivity


    @Before
    fun setUp() {
        activityRule.scenario.onActivity { activity ->
            configuracionActivity = activity
        }
    }


    //    fun comprobarCampos(txtPlanificadorText: String, txtUsernameText: String, txtUsuarioTEAText: String, txtObjetoText: String, imgPlanificador: Drawable, imgUserTEA: Drawable, imageObjeto: Drawable, infoUserTEA: Boolean, infoObjeto: Boolean): Boolean {

    @Test
    fun emptyFields_returnFalse(){
        val imgPlanificador :Drawable = mock(Drawable::class.java)
        val imgUserTEA :Drawable = mock(Drawable::class.java)
        val imgObjeto :Drawable = mock(Drawable::class.java)

        Assert.assertFalse(configuracionActivity.comprobarCampos("","","","",imgPlanificador,imgUserTEA,imgObjeto,false,false))
    }


}