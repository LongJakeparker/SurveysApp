package com.example.surveysapp.other

import android.text.method.PasswordTransformationMethod
import android.view.View

/**
 * @author longtran
 * @since 20/06/2021
 */
class CustomPasswordTransformationMethod: PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View?): CharSequence? {
        return PasswordCharSequence(source)
    }

    private class PasswordCharSequence(private val source: CharSequence) : CharSequence {
        override val length: Int
            get() = source.length

        override fun get(index: Int): Char {
            return '●'
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return source.subSequence(startIndex, endIndex) // Return default
        }
    }
}