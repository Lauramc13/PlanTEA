package com.example.plantea.presentacion.actividades

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import com.example.plantea.R
import org.junit.Before
import org.junit.Test



class RegisterActivityTest{
    private lateinit var resource : RegisterActivity

    @get:Rule
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Before
    fun setUp() {
        activityRule.scenario.onActivity { activity ->
            resource = activity
        }
    }

    //Empty tests
    @Test
    fun empty_NameReturnFalse(){
        assertFalse(resource.comprobarTextViewsVacios("username", "password", "password2", "", "email@email.com", "object", "name2", false, false))
    }

    @Test
    fun empty_UsernameReturnFalse(){
        assertFalse(resource.comprobarTextViewsVacios("", "password", "password2", "name", "email", "object", "name2", false , false))
    }

    @Test
    fun empty_PasswordReturnFalse(){
        assertFalse(resource.comprobarTextViewsVacios("username", "", "password2", "name", "email", "object", "name2", false, false))
    }

    @Test
    fun empty_Password2ReturnFalse(){
        assertFalse(resource.comprobarTextViewsVacios("username", "password", "", "name", "email", "object", "name2",    false, false))
    }

    @Test
    fun empty_EmailReturnFalse(){
        assertFalse(resource.comprobarTextViewsVacios("username", "password", "password2", "name", "", "object", "name2", false, false))
    }

    @Test
    fun empty_ObjectReturnFalse(){
        assertFalse(resource.comprobarTextViewsVacios("username", "password", "password2", "name", "email", "", "name2", true, false))
    }

    @Test
    fun empty_Name2ReturnFalse(){
        assertFalse(resource.comprobarTextViewsVacios("username", "password", "password2", "name", "email", "object", "", false, true))
    }

    @Test
    fun noEmptyFieldsReturnTrue(){
        assertTrue(resource.comprobarTextViewsVacios("username", "password", "password", "name", "email", "object", "name2", true, true))
    }

    @Test
    fun allEmptyFieldsReturnFalse(){
        assertFalse(resource.comprobarTextViewsVacios("", "", "", "", "", "", "", false, false))
    }

    //Valid tests
    @Test
    fun isValidEmailReturnTrue() {
        assertTrue(resource.isAccountValid("correo@gmail.com", "password", "password", true).isValid)
    }

    @Test
    fun isntValidEmailReturnFalse() {
        assertFalse( resource.isAccountValid("asdf", "password", "password", true).isValid)
    }

    @Test
    fun isValidPasswordReturnTrue() {
        assertTrue(resource.isAccountValid("correo@gmail.com", "123456", "123456", true).isValid)
    }

    @Test
    fun isntValidPasswordReturnFalse() {
        assertFalse(resource.isAccountValid("correo@gmail.com", "123", "123", true).isValid)
    }

    @Test
    fun isntSamePasswordReturnFalse() {
        assertFalse(resource.isAccountValid("correo@gmail.com", "password", "password2", true).isValid)
    }

    @Test
    fun isTextViewsVaciosReturnTrue(){
        assertFalse(resource.isAccountValid("correo@gmail.com", "password", "password2", false).isValid)
    }

    //Test que si se pulsa el botonAyuda, se muestra el tooltip
    @Test
    fun toolTip_isVisible(){
        //onView(withId(R.id.scrollRegister)).perform(scrollTo(), click())
        onView(withId(R.id.buttonAyudaActividad)).perform(click())
        onView(withId(R.id.tooltipText)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }
}