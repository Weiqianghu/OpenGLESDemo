package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.support.annotation.RawRes
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

object TextResourceReader {
    fun readTextFileFromResource(context: Context, @RawRes resourceId: Int): String {
        val body = StringBuilder()

        context.resources?.openRawResource(resourceId)?.let {
            val bufferedReader = BufferedReader(InputStreamReader(it))
            var nextLine = bufferedReader.readLine()
            while (nextLine != null) {
                body.append(nextLine)
                body.append("\n")
                nextLine = bufferedReader.readLine()
            }
        }

        return body.toString()
    }
}