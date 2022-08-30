package simulation.portfolio

import model.*
import simulation.HistoricalDataStore

class ParameterizedPortfolio(
    startDate: Date,
    developedData: Map<Date, Double>,
    emergingData: Map<Date, Double>,
    crbData: Map<Date, Double>,
    goldUsdData: Map<Date, Double>,
    dowToGoldData: Map<Date, Double>,
    shillerSP500PERatioData: Map<Date, Double>,
    private val offensiveGenome: OffensiveGenome,
    private val defensiveGenome: DefensiveGenome,
    private val selling: Boolean,
) : Portfolio(startDate) {

    var genome: Genome = offensiveGenome
    var totalTransactions = 0

    init {
        historicalDataStore = HistoricalDataStore(
            developedData,
            emergingData,
            crbData,
            goldUsdData,
            dowToGoldData,
            shillerSP500PERatioData
        )
    }

    override fun handleDetails(date: Date) {
        if(date.isNewMonth()) {
            genome = if (historicalDataStore.lastShillerSP500PERatio > offensiveGenome.getChoiceParameter()) {
                defensiveGenome.clone()
            } else {
                offensiveGenome.clone()
            }
        }
        manageDeveloped(date)
        manageEmerging(date)
        manageCrb(date)
        manageGold(date)
        if (selling) {
            manageSales(date)
        }
    }

    private fun manageDeveloped(date: Date) {
        val trailingPeriod = genome.getParameter(Parameter.DEVELOPED_TRAILING_PERIOD_DAYS).toInt()
        val maximumRatioPercentage =
            ((historicalDataStore.getDevelopedTrailMaximum(trailingPeriod) / historicalDataStore.lastDevelopedPrice - 1.0) * 100.0)
        if (maximumRatioPercentage >= genome.getParameter(Parameter.DEVELOPED_BUYING_PERCENT_CHANGE)
            || historicalDataStore.lastShillerSP500PERatio
            <= genome.getParameter(Parameter.DEVELOPED_ALWAYS_BUYING_SHILLER_PE)
        ) {
            buyDeveloped(date.copy())
            totalTransactions++
        }
    }

    private fun manageEmerging(date: Date) {
        val trailingPeriod = genome.getParameter(Parameter.EMERGING_TRAILING_PERIOD_DAYS).toInt()
        val maximumRatioPercentage =
            ((historicalDataStore.getEmergingTrailMaximum(trailingPeriod) / historicalDataStore.lastEmergingPrice - 1.0) * 100.0)
        if (maximumRatioPercentage >= genome.getParameter(Parameter.EMERGING_BUYING_PERCENT_CHANGE)
            || historicalDataStore.lastShillerSP500PERatio
            >= genome.getParameter(Parameter.EMERGING_ALWAYS_BUYING_OVER_SHILLER_PE)
        ) {
            buyEmerging(date.copy())
            totalTransactions++
        }
    }

    private fun manageCrb(date: Date) {
        val trailingPeriod = genome.getParameter(Parameter.CRB_TRAILING_PERIOD_DAYS).toInt()
        val maximumRatioPercentage =
            ((historicalDataStore.getCrbTrailMaximum(trailingPeriod) / historicalDataStore.lastCrbPrice - 1.0) * 100.0)
        if (maximumRatioPercentage >= genome.getParameter(Parameter.CRB_BUYING_PERCENT_CHANGE)
            || historicalDataStore.lastCrbPrice
            <= genome.getParameter(Parameter.CRB_ALWAYS_BUYING_PRICE)
        ) {
            buyCrb(date.copy())
            totalTransactions++
        }
    }

    private fun manageGold(date: Date) {
        if (historicalDataStore.lastDowToGoldRatio >= genome.getParameter(Parameter.GOLD_BUYING_DOW_TO_GOLD_VALUE)) {
            buyGold(date.copy())
            totalTransactions++
        }
    }

    private fun manageSales(date: Date) {
        val lastDevelopedPrice = historicalDataStore.lastDevelopedPrice
        val lastEmergingPrice = historicalDataStore.lastEmergingPrice
        val lastCrbPrice = historicalDataStore.lastCrbPrice
        val lastGoldPrice = historicalDataStore.lastGoldPrice
        for (asset in assets) {
            if (!asset.isSold()) {
                var price: Double
                var parameter: Parameter
                when (asset.type) {
                    Asset.Type.DEVELOPED -> {
                        price = lastDevelopedPrice
                        parameter = Parameter.DEVELOPED_SELLING_PERCENT_CHANGE
                    }
                    Asset.Type.EMERGING -> {
                        price = lastEmergingPrice
                        parameter = Parameter.EMERGING_SELLING_PERCENT_CHANGE
                    }
                    Asset.Type.CRB -> {
                        price = lastCrbPrice
                        parameter = Parameter.CRB_SELLING_PERCENT_CHANGE
                    }
                    Asset.Type.GOLD -> {
                        price = lastGoldPrice
                        parameter = Parameter.GOLD_SELLING_PERCENT_CHANGE
                    }
                }
                if ((asset.getCurrentValue(price) / asset.purchaseValue - 1.0) * ONE_HUNDRED
                    >= genome.getParameter(parameter)
                ) {
                    asset.saleDate = date.copy()
                    asset.saleValue = asset.getCurrentValue(price)
                    totalTransactions++
                    when (asset.type) {
                        Asset.Type.DEVELOPED -> {
                            developedSaleProfits += asset.getCurrentValue(price)
                        }
                        Asset.Type.EMERGING -> {
                            emergingSaleProfits += asset.getCurrentValue(price)
                        }
                        Asset.Type.CRB -> {
                            crbSaleProfits += asset.getCurrentValue(price)
                        }
                        Asset.Type.GOLD -> {
                            goldSaleProfits += asset.getCurrentValue(price)
                        }
                    }
                }
            }
        }
    }
}
