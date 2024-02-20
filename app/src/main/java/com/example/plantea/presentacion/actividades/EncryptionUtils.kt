package com.example.plantea.presentacion.actividades

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class EncryptionUtils {
    companion object {
        fun encrypt(data: String, context: Context): String {
            val plainText = data.toByteArray(Charsets.UTF_8)
            val keygen = KeyGenerator.getInstance("AES")
            keygen.init(256)
            val key = keygen.generateKey()
            saveSecretKey(context, key)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val cipherText = cipher.doFinal(plainText)
            saveInitializationVector(context, cipher.iv)

            val sb = StringBuilder()
            for (b in cipherText) {
                sb.append(b.toChar())
            }

            return cipherText.toString(Charsets.UTF_8)
        }

        fun getEncrypt(data: String, context: Context): String {
            val ivSpec = IvParameterSpec(getSavedInitializationVector(context))
            val key = getSavedSecretKey(context)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
            val cipherText = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

            val sb = StringBuilder()
            for (b in cipherText) {
                sb.append(b.toChar())
            }
            return cipherText.toString(Charsets.UTF_8)

        }

        private fun getSavedInitializationVector(context: Context): ByteArray {
            val prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
            val strInitializationVector = prefs.getString("initialization_vector", "")
            val bytes = android.util.Base64.decode(strInitializationVector, android.util.Base64.DEFAULT)
            val ois = ObjectInputStream(ByteArrayInputStream(bytes))
            return ois.readObject() as ByteArray
        }

        private fun saveSecretKey(context: Context, secretKey: SecretKey) {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(secretKey)
            val strToSave =
                String(android.util.Base64.encode(baos.toByteArray(), android.util.Base64.DEFAULT))
            val prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("secret_key", strToSave)
            editor.apply()
        }

        private fun getSavedSecretKey(context: Context): SecretKey {
            val prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
            val strSecretKey = prefs.getString("secret_key", "")
            val bytes = android.util.Base64.decode(strSecretKey, android.util.Base64.DEFAULT)
            val ois = ObjectInputStream(ByteArrayInputStream(bytes))
            return ois.readObject() as SecretKey
        }

        private fun saveInitializationVector(context: Context, initializationVector: ByteArray) {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(initializationVector)
            val strToSave =
                String(android.util.Base64.encode(baos.toByteArray(), android.util.Base64.DEFAULT))
            val prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("initialization_vector", strToSave)
            editor.apply()
        }
    }
}