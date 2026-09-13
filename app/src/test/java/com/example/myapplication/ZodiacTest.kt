package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Test

class ZodiacTest {
    @Test
    fun signsChangeOnTheirCalendarBoundaries() {
        val starts = listOf(
            Triple(1, 20, Zodiac.AQUARIUS), Triple(2, 19, Zodiac.PISCES),
            Triple(3, 21, Zodiac.ARIES), Triple(4, 20, Zodiac.TAURUS),
            Triple(5, 21, Zodiac.GEMINI), Triple(6, 21, Zodiac.CANCER),
            Triple(7, 23, Zodiac.LEO), Triple(8, 23, Zodiac.VIRGO),
            Triple(9, 23, Zodiac.LIBRA), Triple(10, 23, Zodiac.SCORPIO),
            Triple(11, 22, Zodiac.SAGITTARIUS), Triple(12, 22, Zodiac.CAPRICORN)
        )
        starts.forEachIndexed { index, (month, day, sign) ->
            assertEquals(sign, Zodiac.fromDate(BirthDate(2000, month, day)))
            assertEquals(starts[(index + 11) % 12].third,
                Zodiac.fromDate(BirthDate(2000, month, day - 1)))
        }
    }

    @Test
    fun leapDayAndYearRollover() {
        assertEquals(Zodiac.PISCES, Zodiac.fromDate(BirthDate(2000, 2, 29)))
        assertEquals(Zodiac.CAPRICORN, Zodiac.fromDate(BirthDate(2000, 1, 1)))
        assertEquals(Zodiac.CAPRICORN, Zodiac.fromDate(BirthDate(2000, 12, 31)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidDateIsRejected() {
        BirthDate(2001, 2, 29)
    }

    @Test
    fun playerKeepsInputAndComputesSign() {
        val player = Player("Иванов Иван Иванович", Gender.MALE, 2, 7, BirthDate(2004, 4, 20))
        assertEquals("20.04.2004", player.birthDate.formatted())
        assertEquals(Zodiac.TAURUS, player.zodiac)
        assertEquals(2, player.course)
        assertEquals(7, player.difficulty)
    }
}
