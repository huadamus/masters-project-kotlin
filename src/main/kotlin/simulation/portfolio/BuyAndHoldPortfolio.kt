package simulation.portfolio

import simulation.HistoricalDataStore
import data.RECESSIONS
import model.Date

open class BuyAndHoldPortfolio(
    startDate: Date,
    developedData: Map<Date, Double>,
    emergingData: Map<Date, Double>,
    crbData: Map<Date, Double>,
    goldUsdData: Map<Date, Double>,
    dowToGoldData: Map<Date, Double>,
    shillerSP500PERatioData: Map<Date, Double>,
) : Portfolio(startDate) {

    private var boughtInitialDeveloped = false
    private var boughtInitialEmerging = false
    private var boughtInitialCrb = false
    private var boughtInitialGold = false

    init {
        historicalDataStore = HistoricalDataStore(
            developedData,
            emergingData,
            crbData,
            goldUsdData,
            dowToGoldData,
            shillerSP500PERatioData,
        )
    }

    override fun handleDetails(date: Date) {
        manageDevelopedPurchases(date)
        manageEmergingPurchases(date)
        manageCrbPurchases(date)
        manageGoldPurchases(date)
    }

    protected fun manageDevelopedPurchases(date: Date) {
        if (!boughtInitialDeveloped || (!isInRecession(date)
                    && historicalDataStore.lastShillerSP500PERatio <= 20.0)
        ) {
            boughtInitialDeveloped = true
            buyDeveloped(date.copy())
        }
    }

    protected fun manageEmergingPurchases(date: Date) {
        if (!boughtInitialEmerging || (!isInRecession(date)
                    && historicalDataStore.lastShillerSP500PERatio >= 25.0)
        ) {
            boughtInitialEmerging = true
            buyEmerging(date.copy())
        }
    }

    protected fun manageCrbPurchases(date: Date) {
        if (!boughtInitialCrb || (!isInRecession(date)
                    && historicalDataStore.lastCrbPrice <= 260.0)
        ) {
            boughtInitialCrb = true
            buyCrb(date.copy())
        }
    }

    protected fun manageGoldPurchases(date: Date) {
        if (!boughtInitialGold || (isInRecession(date)
                    && historicalDataStore.lastDowToGoldRatio <= 15.0)
        ) {
            boughtInitialGold = true
            buyGold(date.copy())
        }
    }

    private fun isInRecession(date: Date): Boolean {
        RECESSIONS.forEach {
            if (date >= it.first && date < it.second) {
                return true
            }
        }
        return false
    }
}
