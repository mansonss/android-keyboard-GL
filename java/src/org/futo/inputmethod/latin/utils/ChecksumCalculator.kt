package org.futo.inputmethod.latin.utils

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object ChecksumCalculator {
    @Throws(IOException::class)
    fun checksum(`in`: InputStream): String? {
        val digester: MessageDigest = try {
            MessageDigest.getInstance("SHA-256")
        } catch (_: NoSuchAlgorithmException) {
            return null
        }
        val bytes = ByteArray(8192)
        var byteCount: Int
        while (`in`.read(bytes).also { byteCount = it } > 0) {
            digester.update(bytes, 0, byteCount)
        }
        val digest = digester.digest()
        val s = StringBuilder()
        for (i in digest.indices) {
            s.append(String.format("%1\$02x", digest[i]))
        }
        return s.toString()
    }

    fun checksum(file: File) = runCatching {
        file.inputStream().use { checksum(it) }
    }.getOrNull()
}
