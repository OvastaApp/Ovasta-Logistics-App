package com.ovasta.logisticsapp.base.encryption

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.ovasta.logisticsapp.base.constants.LocalConstants
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {
    private const val KEY_ALIAS = "secret"
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"

    private val cipher = Cipher.getInstance(TRANSFORMATION)

    private val keyStore by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        } else {
            null
        }
    }

    private fun getKey(): SecretKey {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getKeyFromKeystore()
        } else {
            getDerivedKey()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getKeyFromKeystore(): SecretKey {
        return try {
            val existingKey = keyStore?.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            existingKey?.secretKey ?: createKeystoreKey()
        } catch (e: Exception) {
            keyStore?.deleteEntry(KEY_ALIAS)
            createKeystoreKey()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createKeystoreKey(): SecretKey {
        return KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setRandomizedEncryptionRequired(true)
                        .setUserAuthenticationRequired(false)
                        .build()
                )
            }
            .generateKey()
    }

    private fun getDerivedKey(): SecretKey {
        val keyMaterial = buildString {
            append(LocalConstants.AppId)
//            append(BuildConfig.CRYPTO_KEY_MATERIAL)
            append("v1")
        }

        // Use SHA-256 to derive a 256-bit key
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(keyMaterial.toByteArray(Charsets.UTF_8))

        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    fun encrypt(bytes: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(bytes)
        return iv + encrypted
    }

    fun decrypt(bytes: ByteArray): ByteArray {
        val iv = bytes.copyOfRange(0, 16) // AES block size is always 16 bytes
        val data = bytes.copyOfRange(16, bytes.size)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        return cipher.doFinal(data)
    }
}