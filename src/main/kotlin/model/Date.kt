package model

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

data class Date(val day: Int, val month: Int, val year: Int) : Comparable<Date>, java.io.Serializable {

    init {
        if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1) {
            throw Exception("Incorrect date!")
        }
    }

    fun getRelativeDay(daysToMove: Int): Date {
        val date = Date(day, month, year)
        val calendar = date.getCalendar()
        calendar.add(Calendar.DATE, daysToMove)
        val day = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return Date(day, month, year)
    }

    fun getDateMinusMonths(months: Int): Date {
        var remainingMonths = months
        var month = this.month
        var year = this.year
        while (remainingMonths > 0) {
            month--
            if (month == 0) {
                month = 12
                year--
            }
            remainingMonths--
        }
        return Date(this.day, month, year)
    }

    fun getDatePlusMonths(months: Int): Date {
        var remainingMonths = months
        var month = this.month
        var year = this.year
        while (remainingMonths > 0) {
            month++
            if (month == 13) {
                month = 1
                year++
            }
            remainingMonths--
        }
        return Date(this.day, month, year)
    }

    fun isInPeriod(beginning: Date, end: Date): Boolean {
        val currentDate = getCalendar().time
        return this == beginning || (currentDate.after(beginning.getCalendar().time) && currentDate.before(
            end.getCalendar().time
        ))
    }

    fun isNewMonth(): Boolean {
        return day == 1
    }

    fun isNewYear(): Boolean {
        return day == 1 && month == 1
    }

    fun getMonthsBetween(otherDate: Date): Int {
        val df = DateTimeFormatter.ofPattern("yyyy-M-d")
        return ChronoUnit.MONTHS.between(YearMonth.from(LocalDate.parse(toString(), df)),
            YearMonth.from(LocalDate.parse(otherDate.toString(), df))).toInt()
    }

    private fun getCalendar(): Calendar {
        return GregorianCalendar(year, month - 1, day)
    }

    override fun compareTo(other: Date): Int {
        var output = year.compareTo(other.year)
        if (output == 0) {
            output = month.compareTo(other.month)
            if (output == 0) {
                output = day.compareTo(day)
            }
        }
        return output
    }

    override fun toString(): String {
        return "$year-$month-$day"
    }

    companion object {

        fun fromString(string: String): Date {
            val data = string.split("-")
            val date = data[2].toInt()
            val month = data[1].toInt()
            val year = data[0].toInt()
            return Date(date, month, year)
        }
    }
}
