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

    val imgPlanificador :Drawable = mock(Drawable::class.java)
    val imgUserTEA :Drawable = mock(Drawable::class.java)
    val imgObjeto :Drawable = mock(Drawable::class.java)

    var isCheckUserTEA = false
    var isCheckObjeto = false


    @Before
    fun setUp() {
        activityRule.scenario.onActivity { activity ->
            configuracionActivity = activity
        }
    }


    //    fun comprobarCampos(txtPlanificadorText: String, txtUsernameText: String, txtUsuarioTEAText: String, txtObjetoText: String, imgPlanificador: Drawable, imgUserTEA: Drawable, imageObjeto: Drawable, infoUserTEA: Boolean, infoObjeto: Boolean): Boolean {

    @Test
    fun emptyFields_returnFalse(){
       // Assert.assertFalse(configuracionActivity.comprobarCampos("","","","",imgPlanificador,imgUserTEA,imgObjeto,isCheckUserTEA,isCheckObjeto))
    }

    @Test
    fun emptyPlanificador_returnFalse(){
      //  Assert.assertFalse(configuracionActivity.comprobarCampos("","username","userTEA","objeto",imgPlanificador,imgUserTEA,imgObjeto,isCheckUserTEA,isCheckObjeto))
    }

    @Test
    fun emptyUsername_returnFalse(){
      //  Assert.assertFalse(configuracionActivity.comprobarCampos("planificador","","userTEA","objeto",imgPlanificador,imgUserTEA,imgObjeto,isCheckUserTEA,isCheckObjeto))
    }

    @Test
    fun emptyUserTEA_returnFalse(){
        isCheckUserTEA = true
     //   Assert.assertFalse(configuracionActivity.comprobarCampos("planificador","username","","objeto",imgPlanificador,imgUserTEA,imgObjeto,isCheckUserTEA,isCheckObjeto))
    }

    @Test
    fun emptyObjeto_returnFalse(){
        isCheckObjeto = true
       // Assert.assertFalse(configuracionActivity.comprobarCampos("planificador","username","userTEA","",imgPlanificador,imgUserTEA,imgObjeto,isCheckUserTEA,isCheckObjeto))
    }

}