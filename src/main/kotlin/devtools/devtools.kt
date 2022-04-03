package devtools

import model.Date

fun getTrainingAndTestPeriods(
    beginningDate: Date,
    endDate: Date,
    periodLengthInMonths: Int,
    trainingPeriodsPerTestPeriod: Int
): Pair<List<Pair<Date, Date>>, List<Pair<Date, Date>>> {
    val periods = mutableListOf<Pair<Date, Date>>()
    var date = beginningDate.copy()
    while (date < endDate) {
        val nextDate = date.getDatePlusMonths(periodLengthInMonths)
        periods.add(Pair(date.copy(), nextDate.copy()))
        date = nextDate
    }

    val trainingPeriods = mutableListOf<Pair<Date, Date>>()
    val testPeriods = mutableListOf<Pair<Date, Date>>()
    periods.shuffle()
    outer@ while (periods.isNotEmpty()) {
        for (i in 0 until trainingPeriodsPerTestPeriod) {
            trainingPeriods += periods[0]
            periods.removeAt(0)
            if (periods.isEmpty()) {
                break@outer
            }
        }
        testPeriods += periods[0]
        periods.removeAt(0)
    }
    return Pair(trainingPeriods, testPeriods)
}
