package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import net.bytebuddy.matcher.ElementMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.verify

class PreLoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(PreLoginActivity::class.java)

    @Mock
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    @Mock
    private lateinit var usuario: Usuario

    // Test subject
    private lateinit var preLoginActivity: PreLoginActivity

    // Mock launcher
    private lateinit var launcher: ActivityResultLauncher<Intent>



    @Before
    fun setUp() {
        activityRule.scenario.onActivity { activity ->
            preLoginActivity = activity
        }

       /* launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            preLoginActivity.handleGoogleSignInResult(result)
        }

        val scenario = ActivityScenario.launch(PreLoginActivity::class.java)
        scenario.onActivity {
            preLoginActivity = it
            preLoginActivity.mGoogleSignInClient = mGoogleSignInClient
            preLoginActivity.usuario = usuario
            preLoginActivity.launcher = launcher
        }*/
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

    @Test
    fun accountDoesntExistReturnFalse() = runBlocking {
        val resultDeferred = CompletableDeferred<Boolean>()

        val callback: (Boolean) -> Unit = { result ->
            resultDeferred.complete(result)
        }

        preLoginActivity.iniciarSesion("email", "password", callback)

        val result = resultDeferred.await()
        assertFalse(result)
    }

    // Test el login con una cuenta de Google
    /*@Test
    fun signInGoogle_Success() {
        // Mock successful Google sign-in result
        val mockAccount = mock(GoogleSignInAccount::class.java)
        `when`(mockAccount.email).thenReturn("test@example.com")
        `when`(mGoogleSignInClient.signInIntent).thenReturn(Intent())

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