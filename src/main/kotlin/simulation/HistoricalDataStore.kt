package simulation

import model.Date

class HistoricalDataStore(
    private val developedData: Map<Date, Double>,
    private val emergingData: Map<Date, Double>,
    private val crbData: Map<Date, Double>,
    private val goldUsdData: Map<Date, Double>,
    private val dowToGoldData: Map<Date, Double>,
    private val shillerSP500PERatioData: Map<Date, Double>,
) {
    var lastDevelopedPrice = 0.0
        private set
    var lastEmergingPrice = 0.0
        private set
    var lastCrbPrice = 0.0
        private set
    var lastGoldPrice = 0.0
        private set
    var lastDowToGoldRatio = 0.0
        private set
    var lastShillerSP500PERatio = 0.0
        private set
    private var developedTrail = mutableListOf<Pair<Date, Double>>()
    private var emergingTrail = mutableListOf<Pair<Date, Double>>()
    private var crbTrail = mutableListOf<Pair<Date, Double>>()

    fun updateLastPrices(date: Date) {
        if (developedData.contains(date)) {
            val value = developedData[date]!!
            if (value != 0.0) {
                lastDevelopedPrice = value
            }
        }
        if (emergingData.contains(date)) {
            val value = emergingData[date]!!
            if (value != 0.0) {
                lastEmergingPrice = value
            }
        }
        if (crbData.contains(date)) {
            val value = crbData[date]!!
            if (value != 0.0) {
                lastCrbPrice = value
            }
        }
        if (goldUsdData.contains(date)) {
            val value = goldUsdData[date]!!
            if (value != 0.0) {
                lastGoldPrice = value
            }
        }
        if (dowToGoldData.contains(date)) {
            val value = dowToGoldData[date]!!
            if (value != 0.0) {
                lastDowToGoldRatio = value
            }
        }
        if (shillerSP500PERatioData.contains(date)) {
            val value = shillerSP500PERatioData[date]!!
            if (value != 0.0) {
                lastShillerSP500PERatio = value
            }
        }
    }

    fun updateTrails(date: Date) {
        updateDevelopedTrail(date)
        updateEmergingTrail(date)
        updateCrbTrail(date)
    }

    fun getDevelopedTrailMaximum(days: Int): Double {
        if (developedTrail.isEmpty()) {
            return lastDevelopedPrice
        }
        if (developedTrail.size < days) {
            return developedTrail.maxOf { it.second }
        }
        return developedTrail
            .takeLast(days)
            .maxOf { it.second }
    }

    fun getEmergingTrailMaximum(days: Int): Double {
        if (emergingTrail.isEmpty()) {
            return lastEmergingPrice
        }
        if (emergingTrail.size < days) {
            return emergingTrail.maxOf { it.second }
        }
        return emergingTrail.takeLast(days)
            .maxOf { it.second }
    }

    fun getCrbTrailMaximum(days: Int): Double {
        if (crbTrail.isEmpty()) {
            return lastCrbPrice
        }
        if (crbTrail.size < days) {
            return crbTrail.maxOf { it.second }
        }
        return crbTrail.takeLast(days)
            .maxOf { it.second }
    }

    //TODO: If conducting DAILY operations, this needs to be adjusted
    private fun updateDevelopedTrail(date: Date) {
        if(lastDevelopedPrice > 0.0) {
            developedTrail += Pair(date, lastDevelopedPrice)
            if (developedTrail.size > 12) {
                developedTrail.removeFirst()
            }
        }
    }

    private fun updateCrbTrail(date: Date) {
        if(lastCrbPrice > 0.0) {
            crbTrail.add(Pair(date, lastCrbPrice))
            if (crbTrail.size > 12) {
                crbTrail.removeFirst()
            }
        }
    }

    private fun updateEmergingTrail(date: Date) {
        if(lastEmergingPrice > 0.0) {
            emergingTrail.add(Pair(date, lastEmergingPrice))
            if (emergingTrail.size > 12) {
                emergingTrail.removeFirst()
            }
        }
    }
}
