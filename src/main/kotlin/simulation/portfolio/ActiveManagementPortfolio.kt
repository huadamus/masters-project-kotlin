package simulation.portfolio

import model.Date

class ActiveManagementPortfolio(
    startDate: Date,
    developedData: Map<Date, Double>,
    emergingData: Map<Date, Double>,
    crbData: Map<Date, Double>,
    goldUsdData: Map<Date, Double>,
    dowToGoldData: Map<Date, Double>,
    shillerSP500PERatioData: Map<Date, Double>,
) : BuyAndHoldPortfolio(
    startDate,
    developedData,
    emergingData,
    crbData,
    goldUsdData,
    dowToGoldData,
    shillerSP500PERatioData
) {

    override fun handleDetails(date: Date) {
        manageDevelopedPurchases(date)
        manageEmergingPurchases(date)
        manageCrbPurchases(date)
        manageGoldPurchases(date)
        manageDevelopedSales(date)
        manageEmergingSales(date)
        manageCrbSales(date)
        manageGoldSales(date)
    }

    private fun manageDevelopedSales(date: Date) {
        if (historicalDataStore.lastShillerSP500PERatio > 35.0) {
            sellAsset(date, Asset.Type.DEVELOPED)
        }
    }

    private fun manageEmergingSales(date: Date) {
        if (historicalDataStore.lastShillerSP500PERatio < 18.0) {
            sellAsset(date, Asset.Type.EMERGING)
        }
    }

    private fun manageCrbSales(date: Date) {
        if (historicalDataStore.lastCrbPrice >= 300.0) {
            sellAsset(date, Asset.Type.CRB)
        }
    }

    private fun manageGoldSales(date: Date) {
        if (historicalDataStore.lastDowToGoldRatio > 19.0) {
            sellAsset(date, Asset.Type.GOLD)
        }
    }
}
