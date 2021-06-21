package com.example.surveysapp.other

import androidx.core.util.PatternsCompat

/**
 * @author longtran
 * @since 13/06/2021
 */

// An extension uses to validate an email
fun String?.isValidEmail() =
    !isNullOrEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()