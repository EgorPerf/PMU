package com.example.myapplication

enum class Zodiac(val nameResource: Int, val drawing: String) {
    ARIES(R.string.aries, "M50,80 L50,35 C50,5 10,5 15,35 C18,48 32,45 32,35 M50,35 C50,5 90,5 85,35 C82,48 68,45 68,35"),
    TAURUS(R.string.taurus, "M20,15 C20,48 80,48 80,15 M50,40 A24,24 0,1 1,49.9 40"),
    GEMINI(R.string.gemini, "M25,15 Q50,25 75,15 M35,20 L35,80 M65,20 L65,80 M25,85 Q50,75 75,85"),
    CANCER(R.string.cancer, "M80,30 Q45,5 20,30 A12,12 0,1 0,44 30 A12,12 0,1 0,20 30 M20,70 Q55,95 80,70 A12,12 0,1 0,56 70 A12,12 0,1 0,80 70"),
    LEO(R.string.leo, "M28,55 A13,13 0,1 0,28 81 A13,13 0,1 0,28 55 M40,65 C65,38 27,28 45,15 C70,-2 88,28 70,52 C45,90 82,96 87,73"),
    VIRGO(R.string.virgo, "M15,75 L15,25 Q25,10 35,25 L35,70 M35,25 Q45,10 55,25 L55,70 M55,25 Q65,10 75,25 L75,62 Q75,82 58,88 M75,38 Q96,12 91,47 Q88,68 68,77 L89,88"),
    LIBRA(R.string.libra, "M15,82 L85,82 M15,63 L35,63 C5,20 95,20 65,63 L85,63"),
    SCORPIO(R.string.scorpio, "M15,77 L15,27 Q25,12 35,27 L35,77 M35,27 Q45,12 55,27 L55,67 Q55,85 76,77 L88,65 M76,65 L88,65 L88,77"),
    SAGITTARIUS(R.string.sagittarius, "M20,80 L80,20 M48,20 L80,20 L80,52 M25,45 L55,75"),
    CAPRICORN(R.string.capricorn, "M10,28 Q20,12 28,28 L40,73 L55,27 Q70,8 70,40 L70,65 Q90,45 92,66 Q94,90 72,80 L57,65"),
    AQUARIUS(R.string.aquarius, "M10,42 L25,27 L40,42 L55,27 L70,42 L85,27 L95,37 M10,72 L25,57 L40,72 L55,57 L70,72 L85,57 L95,67"),
    PISCES(R.string.pisces, "M25,15 Q60,50 25,85 M75,15 Q40,50 75,85 M15,50 L85,50");

    companion object {
        // Принятые календарные границы знаков, без астрономического расчёта.
        fun fromDate(date: BirthDate): Zodiac = when (date.month * 100 + date.day) {
            in 120..218 -> AQUARIUS
            in 219..320 -> PISCES
            in 321..419 -> ARIES
            in 420..520 -> TAURUS
            in 521..620 -> GEMINI
            in 621..722 -> CANCER
            in 723..822 -> LEO
            in 823..922 -> VIRGO
            in 923..1022 -> LIBRA
            in 1023..1121 -> SCORPIO
            in 1122..1221 -> SAGITTARIUS
            else -> CAPRICORN
        }
    }
}
