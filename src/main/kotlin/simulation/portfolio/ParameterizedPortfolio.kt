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
    private var totalTransactions = 0

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
        if (date.isNewMonth()) {
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
        val trailMaximum = historicalDataStore.getDevelopedTrailMaximum(trailingPeriod)
        val maximumRatioPercentage =
            ((trailMaximum / historicalDataStore.lastDevelopedPrice - 1.0) * 100.0)
        if (maximumRatioPercentage >= genome.getParameter(Parameter.DEVELOPED_BUYING_PERCENT_CHANGE)) {
            buyDeveloped(
                date.copy(),
                "By percentage (developed maximum: $trailMaximum, last price: ${historicalDataStore.lastDevelopedPrice} maxRatioPerc: $maximumRatioPercentage, parameter: ${
                    genome.getParameter(
                        Parameter.DEVELOPED_BUYING_PERCENT_CHANGE
                    )
                }"
            )
        }
        if (historicalDataStore.lastShillerSP500PERatio <= genome.getParameter(Parameter.DEVELOPED_ALWAYS_BUYING_SHILLER_PE)) {
            buyDeveloped(
                date.copy(),
                "By always buying under shiller PE (shiller ratio: ${historicalDataStore.lastShillerSP500PERatio}, parameter: ${
                    genome.getParameter(Parameter.DEVELOPED_ALWAYS_BUYING_SHILLER_PE)
                }"
            )
        }
    }

    private fun manageEmerging(date: Date) {
        var trailingPeriod = genome.getParameter(Parameter.EMERGING_TRAILING_PERIOD_DAYS).toInt()
        val trailMaximum = historicalDataStore.getEmergingTrailMaximum(trailingPeriod)
        val maximumRatioPercentage =
            ((trailMaximum / historicalDataStore.lastEmergingPrice - 1.0) * 100.0)
        if (maximumRatioPercentage >= genome.getParameter(Parameter.EMERGING_BUYING_PERCENT_CHANGE)) {
            buyEmerging(
                date.copy(),
                "By percentage (developed maximum: $trailMaximum, last price: ${historicalDataStore.lastEmergingPrice} maxRatioPerc: $maximumRatioPercentage, parameter: ${
                    genome.getParameter(
                        Parameter.EMERGING_BUYING_PERCENT_CHANGE
                    )
                }"
            )
        }
        if (historicalDataStore.lastShillerSP500PERatio >= genome.getParameter(Parameter.EMERGING_ALWAYS_BUYING_OVER_SHILLER_PE)) {
            buyEmerging(
                date.copy(),
                "By always buying over shiller PE (shiller ratio: ${historicalDataStore.lastShillerSP500PERatio}, parameter: ${
                    genome.getParameter(Parameter.EMERGING_ALWAYS_BUYING_OVER_SHILLER_PE)
                }"
            )
        }
    }

    private fun manageCrb(date: Date) {
        var trailingPeriod = genome.getParameter(Parameter.CRB_TRAILING_PERIOD_DAYS).toInt()
        val trailMaximum = historicalDataStore.getCrbTrailMaximum(trailingPeriod)
        val maximumRatioPercentage =
            ((trailMaximum / historicalDataStore.lastCrbPrice - 1.0) * 100.0)
        if (maximumRatioPercentage >= genome.getParameter(Parameter.CRB_BUYING_PERCENT_CHANGE)) {
            buyCrb(date.copy(), "By percentage (crb maximum: $trailMaximum, last price: ${historicalDataStore.lastCrbPrice} maxRatioPerc: $maximumRatioPercentage, parameter: ${
                genome.getParameter(
                    Parameter.CRB_BUYING_PERCENT_CHANGE
                )
            }")
        }
        if (historicalDataStore.lastCrbPrice <= genome.getParameter(Parameter.CRB_ALWAYS_BUYING_PRICE)) {
            buyCrb(date.copy(), "By always buying below price (last price: ${historicalDataStore.lastCrbPrice}, parameter: ${
                genome.getParameter(Parameter.CRB_ALWAYS_BUYING_PRICE)
            }")
        }
    }

    private fun manageGold(date: Date) {
        if (historicalDataStore.lastDowToGoldRatio >= genome.getParameter(Parameter.GOLD_BUYING_DOW_TO_GOLD_VALUE)) {
            buyGold(date.copy(), "By always buying over dow to gold (dowtogold ratio: ${historicalDataStore.lastDowToGoldRatio}, parameter: ${
                genome.getParameter(Parameter.GOLD_BUYING_DOW_TO_GOLD_VALUE)
            }")
        }
    }

    private fun manageSales(date: Date) {
        val lastDevelopedPrice = historicalDataStore.lastDevelopedPrice
        val lastEmergingPrice = historicalDataStore.lastEmergingPrice
        val lastCrbPrice = historicalDataStore.lastCrbPrice
        val lastGoldPrice = historicalDataStore.lastGoldPrice
        for (asset in assetsAndReasonsForBuying) {
            if (!asset.first.isSold()) {
                var price: Double
                var parameter: Parameter
                when (asset.first.type) {
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
                if ((asset.first.getCurrentValue(price) / asset.first.purchaseValue - 1.0) * ONE_HUNDRED
                    >= genome.getParameter(parameter)
                ) {
                    asset.first.saleDate = date.copy()
                    asset.first.saleValue = asset.first.getCurrentValue(price)
                    totalTransactions++
                    when (asset.first.type) {
                        Asset.Type.DEVELOPED -> {
                            developedSaleProfits += asset.first.getCurrentValue(price)
                            cash += asset.first.getCurrentValue(price)
                            cash -= BROKERAGE_FEE
                            totalSpentOnBrokerageFees += BROKERAGE_FEE
                        }

                        Asset.Type.EMERGING -> {
                            emergingSaleProfits += asset.first.getCurrentValue(price)
                            cash += asset.first.getCurrentValue(price)
                            cash -= BROKERAGE_FEE
                            totalSpentOnBrokerageFees += BROKERAGE_FEE
                        }

                        Asset.Type.CRB -> {
                            crbSaleProfits += asset.first.getCurrentValue(price)
                            cash += asset.first.getCurrentValue(price)
                            cash -= BROKERAGE_FEE
                            totalSpentOnBrokerageFees += BROKERAGE_FEE
                        }

                        Asset.Type.GOLD -> {
                            goldSaleProfits += asset.first.getCurrentValue(price)
                            cash += asset.first.getCurrentValue(price)
                            cash -= BROKERAGE_FEE
                            totalSpentOnBrokerageFees += BROKERAGE_FEE
                        }
                    }
                }
            }
        }
    }
}
