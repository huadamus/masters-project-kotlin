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
) : java.io.Serializable {

    override fun toString(): String {
        return "{$period, invested: ${developedInvested.round()}, ${
            emergingInvested.round()
        }, ${crbInvested.round()}, ${goldInvested.round()}" +
                ", final: ${developedFinal.round()}, ${emergingFinal.round()}, ${crbFinal.round()}, ${
                    goldFinal.round()
                }, profits: ${getDevelopedProfitPercentage().round()}%, ${
                    getEmergingProfitPercentage().round()
                }%, ${getCrbProfitPercentage().round()}%, ${getGoldProfitPercentage().round()}%}"
    }

    private fun getDevelopedProfitPercentage(): Double = if (developedInvested > 0.0) {
        (developedFinal / developedInvested - 1.0) * 100.0
    } else {
        0.0
    }

    private fun getEmergingProfitPercentage(): Double = if (emergingInvested > 0.0) {
        (emergingFinal / emergingInvested - 1.0) * 100.0
    } else {
        0.0
    }

    private fun getCrbProfitPercentage(): Double = if (crbInvested > 0.0) {
        (crbFinal / crbInvested - 1.0) * 100.0
    } else {
        0.0
    }

    private fun getGoldProfitPercentage(): Double = if (goldInvested > 0.0) {
        (goldFinal / goldInvested - 1.0) * 100.0
    } else {
        0.0
    }
}
