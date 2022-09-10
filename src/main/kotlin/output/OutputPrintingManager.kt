package output

import model.Date
import model.OffensiveGenome
import model.Parameter
import simulation.SimulationOutcome
import simulation.portfolio.Portfolio

object OutputPrintingManager {

    fun getReadableScore(outcomes: List<SimulationOutcome>): String {
        val sortedOutcomes = outcomes.sortedByDescending { it.getHvValue() }
        var output = ""
        for (i in sortedOutcomes.indices) {
            output += "Outcome ${i + 1}/${sortedOutcomes.size} - ROI: ${
                sortedOutcomes[i].profits.round()
            }%  |  maximum monthly downward volatility: ${
                sortedOutcomes[i].risk.round()
            }%"
            output += "  |  HV value: ${
                sortedOutcomes[i].getHvValue().round()
            }"
            output += System.lineSeparator()
        }
        return output
    }

    fun getReadableParameters(outcomes: List<SimulationOutcome>, endDate: Date): String {
        val sortedOutcomes = outcomes.sortedByDescending { it.getHvValue() }
        val offensiveGenomes = sortedOutcomes.map { it.genome as OffensiveGenome }
        var output = ""
        for (i in sortedOutcomes.indices) {
            output += "Solution ${i + 1}/${sortedOutcomes.size}" + System.lineSeparator()
            output += "Offensive genome:" + System.lineSeparator()
            output += "Hashcode = " + sortedOutcomes[i].hashCode() + System.lineSeparator()
            output += "Choice parameter = " + offensiveGenomes[i].getChoiceParameter().round() +
                    System.lineSeparator()
            for (parameter in Parameter.values()) {
                output += "$parameter = ${
                    offensiveGenomes[i].getParameter(parameter).round()
                }" + System.lineSeparator()
            }
            output += "Defensive genome:" + System.lineSeparator()
            output += "Hashcode = " + offensiveGenomes[i].bestDefensiveGenome.hashCode() + System.lineSeparator()
            for (parameter in Parameter.values()) {
                output += "$parameter = ${
                    offensiveGenomes[i].bestDefensiveGenome!!.getParameter(parameter).round()
                }" + System.lineSeparator()
            }
            output += "Strategy details:" + System.lineSeparator()
            for (strategyDetails in offensiveGenomes[i].strategyDetailsWithDefensiveGenome!!.withIndex()) {
                output += "Strategy details ${strategyDetails.index + 1}: ${strategyDetails.value}" + System.lineSeparator()
                output += "Asset details:" + System.lineSeparator()
                for (asset in strategyDetails.value.assetsList.sortedBy { it.purchaseDate }) {
                    output += "$asset" + System.lineSeparator()
                }
                if (strategyDetails.value.assetsList.isNotEmpty()) {
                    output += "Purchases by months:" + System.lineSeparator()
                    var date = strategyDetails.value.assetsList.minOf { it.purchaseDate }
                    while (date < endDate) {
                        output += "$date, ${
                            strategyDetails.value.assetsList.filter {
                                it.purchaseDate.year == date.year && it.purchaseDate.month == date.month
                            }.size
                        }" + System.lineSeparator()
                        date = date.getDatePlusMonths(1)
                    }
                    output += "Sales by months:" + System.lineSeparator()
                    if (strategyDetails.value.assetsList.any { it.isSold() }) {
                        date = strategyDetails.value.assetsList.minOf { it.purchaseDate }
                        while (date < endDate) {
                            output += "$date, ${
                                strategyDetails.value.assetsList.filter { it.isSold() }.filter {
                                    it.saleDate!!.year == date.year && it.saleDate!!.month == date.month
                                }.size
                            }" + System.lineSeparator()
                            date = date.getDatePlusMonths(1)
                        }
                    } else {
                        output += "No sales done." + System.lineSeparator()
                    }
                    val avgTimeOfHeld = strategyDetails.value.assetsList.map {
                        if (it.isSold()) {
                            it.purchaseDate.getMonthsBetween(it.saleDate!!)
                        } else {
                            it.purchaseDate.getMonthsBetween(endDate)
                        }
                    }.average()
                    output += "Average time of holding: $avgTimeOfHeld months" + System.lineSeparator()
                    val avgSoldProfits = strategyDetails.value.assetsList.filter { it.isSold() }
                        .map { it.saleValue!! / it.purchaseValue * 100.0 - 100.0 }.average()
                    output += "Average profit of sold assets: ${avgSoldProfits.round()}%" + System.lineSeparator()
                    val avgProfits = strategyDetails.value.assetsList
                        .map { it.saleValue!! / it.purchaseValue * 100.0 - 100.0 }.average()
                    output += "Average profit of all assets: ${avgProfits.round()}%" + System.lineSeparator()
                    output += "Total spent on brokerage fees: ${
                        (strategyDetails.value.assetsList.size * Portfolio.BROKERAGE_FEE +
                                strategyDetails.value.assetsList.filter { it.isSold() }.size * Portfolio.BROKERAGE_FEE)
                    }" + System.lineSeparator()
                } else {
                    output += "No assets bought." + System.lineSeparator()
                }
            }
            output += System.lineSeparator()
        }
        return output
    }
}

fun Double.round(): String {
    return String.format("%.2f", this)
}
