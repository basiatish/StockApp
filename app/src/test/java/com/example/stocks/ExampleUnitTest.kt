package com.example.stocks

import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {
        val str = "2023-05-06 06:45:00"
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = LocalDateTime.parse(str, format)
        val oldZone = ZoneId.of("America/New_York")
        val localZone = ZoneId.systemDefault()
        val serverDate = date.atZone(oldZone)
        println("Server Date $serverDate")
        val localDate = serverDate.withZoneSameInstant(localZone)
        println("Local Date $localDate")
        val currentTimeMills = Instant.now().toEpochMilli()
        val millsLocal = localDate.toInstant().toEpochMilli()
        val delta = currentTimeMills - millsLocal
        val days = TimeUnit.MILLISECONDS.toDays(delta).toInt()
        val hours = (TimeUnit.MILLISECONDS.toHours(delta) - days * 24L).toInt()
        val mins = (TimeUnit.MILLISECONDS.toMinutes(delta) - TimeUnit.MILLISECONDS.toHours(delta) * 60).toInt()
        val weeks = days / 7
        val months = weeks / 4
        val years = months / 12
        println("Days: $days, Hours: $hours, Minutes: $mins")
        var res = ""
        if (years == 1) {
            res = "$years Year Ago"
        } else if (years > 1) {
            res = "$years Years Ago"
        } else if (months == 1) {
            res = "$months Month Ago"
        } else if (months in 2..11) {
            res = "$months Months Ago"
        } else if (weeks == 1) {
            res = "$weeks Week Ago"
        } else if (weeks in 2..3) {
            res = "$weeks Weeks Ago"
        } else if (days == 1) {
            res = "$days Day Ago"
        } else if (days in 2..6) {
            res = "$days Days Ago"
        } else if (hours == 1) {
            res = "$hours Hour Ago"
        } else if (hours in 2..23) {
            res = "$hours Hours Ago"
        } else if (mins == 1) {
            res = "$mins Minute Ago"
        } else if (mins in 2..59) {
            res = "$mins Minutes Ago"
        } else {
            res = "Seconds Ago"
        }

        println(res)
    }
}