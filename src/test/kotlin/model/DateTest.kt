package model

import org.junit.jupiter.api.Assertions.*

internal class DateTest {

    @org.junit.jupiter.api.Test
    fun getDatePlusMonths() {
        assertEquals(Date(4, 6, 2001), Date(4, 3, 2001).getDatePlusMonths(3))
        assertEquals(Date(4, 4, 2002), Date(4, 3, 2001).getDatePlusMonths(13))
    }

    @org.junit.jupiter.api.Test
    fun getRelativeDay() {
        assertEquals(Date(31, 1, 2010), Date(30, 1, 2010).getRelativeDay(1))
        assertEquals(Date(2, 2, 2010), Date(1, 2, 2010).getRelativeDay(1))
        assertEquals(Date(1, 2, 2010), Date(31, 1, 2010).getRelativeDay(1))
        assertEquals(Date(31, 1, 2010), Date(1, 2, 2010).getRelativeDay(-1))
        for(i in 1..30) {
            assertEquals(Date(i + 1, 1, 2010), Date(i, 1, 2010).getRelativeDay(1))
        }
    }

    @org.junit.jupiter.api.Test
    fun getMonthsBetween() {
        assertEquals(5, Date(2, 1, 2010).getMonthsBetween(Date(1, 6, 2010)))
        assertEquals(7, Date(1, 1, 2010).getMonthsBetween(Date(1, 8, 2010)))
        assertEquals(23, Date(1, 1, 2000).getMonthsBetween(Date(1, 12, 2001)))
    }

    @org.junit.jupiter.api.Test
    fun isInPeriod() {
        assertFalse(
            Date(1, 2, 2000).isInPeriod(
                Date(1, 1, 2000),
                Date(10, 1, 2000)
            )
        )
        assertFalse(
            Date(1, 3, 2000).isInPeriod(
                Date(1, 1, 2000),
                Date(10, 2, 2000)
            )
        )
        assertFalse(
            Date(1, 1, 2000).isInPeriod(
                Date(1, 1, 1998),
                Date(1, 1, 1999)
            )
        )
        assertFalse(
            Date(1, 1, 2000).isInPeriod(
                Date(1, 1, 2001),
                Date(1, 1, 2002)
            )
        )
        assertTrue(
            Date(5, 1, 2000).isInPeriod(
                Date(1, 1, 2000),
                Date(10, 1, 2000)
            )
        )
        assertTrue(
            Date(1, 2, 2000).isInPeriod(
                Date(1, 1, 2000),
                Date(1, 3, 2000)
            )
        )
        assertTrue(
            Date(1, 2, 2001).isInPeriod(
                Date(1, 1, 2000),
                Date(1, 3, 2002)
            )
        )
    }
}
