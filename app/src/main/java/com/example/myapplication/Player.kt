package com.example.myapplication

import java.util.GregorianCalendar
import java.util.Locale

enum class Gender { MALE, FEMALE }

data class BirthDate(val year: Int, val month: Int, val day: Int) {
    init {
        GregorianCalendar(year, month - 1, day).apply {
            isLenient = false
            timeInMillis
        }
    }

    fun formatted(): String = String.format(Locale.ROOT, "%02d.%02d.%04d", day, month, year)
}

data class Player(
    val fullName: String,
    val gender: Gender,
    val course: Int,
    val difficulty: Int,
    val birthDate: BirthDate
) {
    val zodiac: Zodiac get() = Zodiac.fromDate(birthDate)
}
