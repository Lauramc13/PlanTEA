package com.example.plantea.presentacion.actividades

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.google.android.gms.tasks.Task
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mock

class PreLoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(PreLoginActivity::class.java)

   /* @Mock
    private lateinit var mGoogleSignInClientMock: GoogleSignInClient*/
    @Mock
    private lateinit var usuario: Usuario

    // Test subject
    private lateinit var preLoginActivity: PreLoginActivity

    // Mock launcher
    private lateinit var launcher: ActivityResultLauncher<Intent>



    @Before
    fun setUp() {

        /*launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            preLoginActivity.handleGoogleSignInResult(result)
        }*/
        activityRule.scenario.onActivity { activity ->
            preLoginActivity = activity
            //preLoginActivity.mGoogleSignInClient = mGoogleSignInClientMock
           // preLoginActivity.usuario = usuario
           // preLoginActivity.launcher = launcher
        }
    }

    @Test
    fun empty_NameReturnFalse(){
       assertFalse(preLoginActivity.comprobarTextViewsVacios("", "password"))
    }

    @Test
    fun empty_PasswordReturnFalse(){
        assertFalse(preLoginActivity.comprobarTextViewsVacios("username", ""))
    }

    @Test
    fun empty_NameAndPasswordReturnFalse(){
        assertFalse(preLoginActivity.comprobarTextViewsVacios("", ""))
    }

    @Test
    fun noEmpty_NameAndPasswordReturnTrue(){
        assertTrue(preLoginActivity.comprobarTextViewsVacios("username", "password"))
    }

    // Este test solo funciona si se ha creado la cuenta anteriormente
    @Test
    fun accountExistsReturnTrue(){
        var result = true
        val callback: (Boolean) -> Unit = {
            result = it
        }
        preLoginActivity.iniciarSesion("laura.mc.1304@gmail.com", "123456", callback)
        assertTrue(result)

    }

  /*  @Test
    fun testIniciarSesion_Success() {
        val auth = mock(FirebaseAuth::class.java)

        preLoginActivity.auth = auth

        val email = "test@example.com"
        val password = "password123"

        `when`(auth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(TestUtils.createSuccessfulTask())

        var result: Boolean? = null
        preLoginActivity.iniciarSesion(email, password) {
            result = it
        }

        // Verify that the callback was called with the expected result
        assertEquals(true, result)
    }
*/
    // Test el login con una cuenta de Google
   /* @Test
    fun signInGoogle_Success() {
        // Mock successful Google sign-in result
        val mockAccount = mock(GoogleSignInAccount::class.java)
        `when`(mockAccount.email).thenReturn("test@example.com")
        `when`(mGoogleSignInClientMock.signInIntent).thenReturn(Intent())

        val mockActivityResult = Instrumentation.ActivityResult(
            Activity.RESULT_OK,
            Intent().putExtra("account", mockAccount)
        )

        // Perform the sign-in action
        onView(withId(R.id.Signin)).perform(click())

        // Verify that the expected methods are called
        verify(usuario).consultarId("test@example.com", preLoginActivity)
        verify(preLoginActivity).configurarDatos("test@example.com")
        verify(preLoginActivity).startActivity(Intent(preLoginActivity, PreLoginActivity::class.java))
        verify(preLoginActivity).finish()
    }

    @Test
    fun signInGoogle_Failure() {
        // Mock unsuccessful Google sign-in result
        `when`(mGoogleSignInClient.signInIntent).thenReturn(Intent())
        val mockActivityResult = Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null)

        // Perform the sign-in action
        onView(withId(R.id.Signin)).perform(click())

        // Verify that the expected failure handling is performed
       // verify(preLoginActivity).handleGoogleSignInFailure(any(ApiException::class.java))
    }
*/
}

object TestUtils {
    fun <T> createSuccessfulTask(result: T? = null): Task<T> {
        val task = mock(Task::class.java) as Task<T>
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.result).thenReturn(result)
        return task
    }

    fun <T> createFailedTask(exception: Exception): Task<T> {
        val task = mock(Task::class.java) as Task<T>
        `when`(task.isSuccessful).thenReturn(false)
        `when`(task.exception).thenReturn(exception)
        return task
    }
}

class ActividadActivityTest {

    private lateinit var activityActivity: ActividadActivity

    @JvmField
    @RegisterExtension
    val activityRule = ActivityScenarioRule(ActividadActivity::class.java)


    @BeforeEach
    fun setUp() {
        activityRule.scenario.onActivity { activity ->
            activityActivity = activity
        }
    }

    @org.junit.jupiter.api.Test
    fun testCardObjetoClick(){

        onView(withId(R.id.card_objeto)).perform(click())

        onView(ViewMatchers.withText(R.string.noConfigurationActivity))
            .inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.icono_CerrarDialogo)).perform(click())
    }

}