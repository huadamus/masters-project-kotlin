package model

import output.round

data class StrategyDetails(
    val period: Pair<Date, Date>,
    val developedInvested: Double,
    val emergingInvested: Double,
    val crbInvested: Double,
    val goldInvested: Double,
    val developedFinal: Double,
    val emergingFinal: Double,
    val crbFinal: Double,
    val goldFinal: Double,
    val totalTransactions: Int,
) : java.io.Serializable {

    override fun toString(): String {
        return "{$period, invested: ${developedInvested.round()}, ${
            emergingInvested.round()
        }, ${crbInvested.round()}, ${goldInvested.round()}" +
                ", final: ${developedFinal.round()}, ${emergingFinal.round()}, ${crbFinal.round()}, ${
                    goldFinal.round()
                }, yearly adjusted profits: ${getDevelopedProfitPercentage(period.first.getMonthsBetween(period.second)).round()}%, ${
                    getEmergingProfitPercentage(period.first.getMonthsBetween(period.second)).round()
                }%, ${getCrbProfitPercentage(period.first.getMonthsBetween(period.second)).round()}%, ${
                    getGoldProfitPercentage(
                        period.first.getMonthsBetween(period.second)
                    ).round()
                }%, total transactions: $totalTransactions}"
    }

    private fun getDevelopedProfitPercentage(months: Int): Double = if (developedInvested > 0.0) {
        (developedFinal / developedInvested - 1.0) * 100.0 * (12.0 / months.toDouble())
    } else {
        0.0
    }

    private fun getEmergingProfitPercentage(months: Int): Double = if (emergingInvested > 0.0) {
        (emergingFinal / emergingInvested - 1.0) * 100.0 * (12.0 / months.toDouble())
    } else {
        0.0
    }

    private fun getCrbProfitPercentage(months: Int): Double = if (crbInvested > 0.0) {
        (crbFinal / crbInvested - 1.0) * 100.0 * (12.0 / months.toDouble())
    } else {
        0.0
    }

    private fun getGoldProfitPercentage(months: Int): Double = if (goldInvested > 0.0) {
        (goldFinal / goldInvested - 1.0) * 100.0 * (12.0 / months.toDouble())
    } else {
        0.0
    }
}
