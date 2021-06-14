package com.example.surveysapp.other

import android.util.Patterns

/**
 * @author longtran
 * @since 13/06/2021
 */

// An extension uses to validate an email
fun String?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()