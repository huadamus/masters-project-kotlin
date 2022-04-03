package simulation

import simulation.portfolio.ActiveManagementPortfolio
import simulation.portfolio.BuyAndHoldPortfolio
import simulation.portfolio.ParameterizedPortfolio
import simulation.portfolio.Portfolio
import model.Date
import model.Genome
import model.OffensiveGenome
import model.StrategyDetails

object Simulator {

    fun getScoreForOffensiveGenome(
        offensiveGenome: OffensiveGenome,
        selling: Boolean,
        periods: List<Pair<Date, Date>>,
        developedData: Map<Date, Double>,
        emergingData: Map<Date, Double>,
        crbData: Map<Date, Double>,
        goldUsdData: Map<Date, Double>,
        shillerPESp500Data: Map<Date, Double>,
        dowToGoldData: Map<Date, Double>,
    ): OffensiveGenome {
        val calculationsOutcome = calculateScoreForDoubleGenome(
            offensiveGenome,
            offensiveGenome.bestDefensiveGenome!!,
            selling,
            periods,
            developedData,
            emergingData,
            crbData,
            goldUsdData,
            shillerPESp500Data,
            dowToGoldData
        )
        calculationsOutcome.let {
            val output = offensiveGenome.clone()
            output.bestDefensiveGenome = offensiveGenome.bestDefensiveGenome!!.clone()
            output.profitsWithDefensiveGenome = it.profits
            output.riskWithDefensiveGenome = it.risk
            output.strategyDetailsWithDefensiveGenome = it.strategyDetails
            return output
        }
    }

    private fun calculateScoreForDoubleGenome(
        offensiveGenome: OffensiveGenome,
        defensiveGenome: Genome,
        selling: Boolean,
        periods: List<Pair<Date, Date>>,
        developedData: Map<Date, Double>,
        emergingData: Map<Date, Double>,
        crbData: Map<Date, Double>,
        goldUsdData: Map<Date, Double>,
        shillerPESp500Data: Map<Date, Double>,
        dowToGoldData: Map<Date, Double>,
    ): CalculationsOutcome {
        var profits = 0.0
        var maxRisk = 0.0
        val strategyDetails = mutableListOf<StrategyDetails>()

        for (period in periods.withIndex()) {
            val score = computePortfolioScore(
                period.value.first.copy(),
                period.value.second.copy(),
                developedData,
                emergingData,
                crbData,
                goldUsdData,
                dowToGoldData,
                shillerPESp500Data,
                offensiveGenome,
                defensiveGenome,
                selling
            )
            profits += score.first
            if (maxRisk < score.second) {
                maxRisk = score.second
            }
            strategyDetails += score.third
        }
        return CalculationsOutcome(
            offensiveGenome,
            defensiveGenome,
            profits,
            maxRisk,
            strategyDetails
        )
    }

    fun getScoreForBuyAndHold(
        period: Pair<Date, Date>,
        developedData: Map<Date, Double>,
        emergingData: Map<Date, Double>,
        crbData: Map<Date, Double>,
        goldUsdData: Map<Date, Double>,
        dowToGoldData: Map<Date, Double>,
        shillerSP500PERatioData: Map<Date, Double>,
    ): Pair<Double, Double> {
        val score = computeBuyAndHoldScore(
            period.first.copy(),
            period.second.copy(),
            developedData,
            emergingData,
            crbData,
            goldUsdData,
            dowToGoldData,
            shillerSP500PERatioData
        )
        return Pair(score.first, score.second)
    }

    fun getScoreForActiveManagement(
        period: Pair<Date, Date>,
        developedData: Map<Date, Double>,
        emergingData: Map<Date, Double>,
        crbData: Map<Date, Double>,
        goldUsdData: Map<Date, Double>,
        dowToGoldData: Map<Date, Double>,
        shillerSP500PERatioData: Map<Date, Double>,
    ): Pair<Double, Double> {
        val score = computeActiveManagementScore(
            period.first.copy(),
            period.second.copy(),
            developedData,
            emergingData,
            crbData,
            goldUsdData,
            dowToGoldData,
            shillerSP500PERatioData
        )
        return Pair(score.first, score.second)
    }

    private fun computePortfolioScore(
        beginningDate: Date,
        endDate: Date,
        developedData: Map<Date, Double>,
        emergingData: Map<Date, Double>,
        crbData: Map<Date, Double>,
        goldUsdData: Map<Date, Double>,
        dowToGoldData: Map<Date, Double>,
        shillerSP500PERatioData: Map<Date, Double>,
        offensiveGenome: OffensiveGenome,
        defensiveGenome: Genome,
        selling: Boolean,
    ): Triple<Double, Double, StrategyDetails> {
        val portfolio = ParameterizedPortfolio(
            beginningDate.copy(),
            developedData,
            emergingData,
            crbData,
            goldUsdData,
            dowToGoldData,
            shillerSP500PERatioData,
            offensiveGenome,
            defensiveGenome,
            selling
        )
        var date = beginningDate.getDateMinusMonths(12).copy()
        while (date != endDate) {
            portfolio.handle(date.copy())
            date = date.getRelativeDay(1)
        }
        val totalInvestedValue = portfolio.getTotalInvestedValue()
        val profitsPercents = if (totalInvestedValue == 0.0) {
            0.0
        } else {
            (portfolio.getCurrentAssetsValue() / totalInvestedValue - 1.0) * 100
        } / (beginningDate.getMonthsBetween(endDate) / 12)
        val maxVolatility = portfolio.getMaxMonthlyDownwardVolatility()
        val strategyDetails = StrategyDetails(
            Pair(beginningDate.copy(), endDate.copy()),
            portfolio.getTotalInvestedByAssetType(Portfolio.Asset.Type.DEVELOPED),
            portfolio.getTotalInvestedByAssetType(Portfolio.Asset.Type.EMERGING),
            portfolio.getTotalInvestedByAssetType(Portfolio.Asset.Type.CRB),
            portfolio.getTotalInvestedByAssetType(Portfolio.Asset.Type.GOLD),
            portfolio.getCurrentAssetsValueByAssetType(Portfolio.Asset.Type.DEVELOPED),
            portfolio.getCurrentAssetsValueByAssetType(Portfolio.Asset.Type.EMERGING),
            portfolio.getCurrentAssetsValueByAssetType(Portfolio.Asset.Type.CRB),
            portfolio.getCurrentAssetsValueByAssetType(Portfolio.Asset.Type.GOLD),
        )
        return Triple(profitsPercents, maxVolatility, strategyDetails)
    }

    private fun computeBuyAndHoldScore(
        beginningDate: Date,
        endDate: Date,
        developedData: Map<Date, Double>,
        emergingData: Map<Date, Double>,
        crbData: Map<Date, Double>,
        goldUsdData: Map<Date, Double>,
        dowToGoldData: Map<Date, Double>,
        shillerSP500PERatioData: Map<Date, Double>,
    ): Pair<Double, Double> {
        val portfolio = BuyAndHoldPortfolio(
            beginningDate.copy(),
            developedData,
            emergingData,
            crbData,
            goldUsdData,
            dowToGoldData,
            shillerSP500PERatioData
        )
        var date = beginningDate.getDateMinusMonths(12).copy()
        while (date != endDate) {
            portfolio.handle(date.copy())
            date = date.getRelativeDay(1)
        }
        val totalInvestedValue = portfolio.getTotalInvestedValue()
        val profitsPercents = if (totalInvestedValue == 0.0) {
            0.0
        } else {
            (portfolio.getCurrentAssetsValue() / totalInvestedValue - 1.0) * 100
        } / (beginningDate.getMonthsBetween(endDate) / 12)
        val maxVolatility = portfolio.getMaxMonthlyDownwardVolatility()
        return Pair(profitsPercents, maxVolatility)
    }

    private fun computeActiveManagementScore(
        beginningDate: Date,
        endDate: Date,
        developedData: Map<Date, Double>,
        emergingData: Map<Date, Double>,
        crbData: Map<Date, Double>,
        goldUsdData: Map<Date, Double>,
        dowToGoldData: Map<Date, Double>,
        shillerSP500PERatioData: Map<Date, Double>,
    ): Pair<Double, Double> {
        val portfolio = ActiveManagementPortfolio(
            beginningDate.copy(),
            developedData,
            emergingData,
            crbData,
            goldUsdData,
            dowToGoldData,
            shillerSP500PERatioData
        )
        var date = beginningDate.getDateMinusMonths(12).copy()
        while (date != endDate) {
            portfolio.handle(date.copy())
            date = date.getRelativeDay(1)
        }
        val totalInvestedValue = portfolio.getTotalInvestedValue()
        val profitsPercents = if (totalInvestedValue == 0.0) {
            0.0
        } else {
            (portfolio.getCurrentAssetsValue() / totalInvestedValue - 1.0) * 100
        } / (beginningDate.getMonthsBetween(endDate) / 12)
        val maxVolatility = portfolio.getMaxMonthlyDownwardVolatility()
        return Pair(profitsPercents, maxVolatility)
    }

    private data class CalculationsOutcome(
        val offensiveGenome: OffensiveGenome,
        val defensiveGenome: Genome,
        val profits: Double,
        val risk: Double,
        val strategyDetails: List<StrategyDetails>,
    )
}
