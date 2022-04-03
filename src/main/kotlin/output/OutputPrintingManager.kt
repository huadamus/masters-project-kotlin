package output

import model.OffensiveGenome
import model.Parameter
import simulation.SimulationOutcome

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

    fun getReadableParametersForSingular(outcomes: List<SimulationOutcome>): String {
        val sortedOutcomes = outcomes.sortedByDescending { it.getHvValue() }
        var output = ""
        for (i in sortedOutcomes.indices) {
            output += "Solution ${i + 1}/${sortedOutcomes.size}" + System.lineSeparator()
            output += "Genome:" + System.lineSeparator()
            for (parameter in Parameter.values()) {
                output += "$parameter = ${
                    sortedOutcomes[i].genome.getParameter(parameter)
                }" + System.lineSeparator()
            }
            output += System.lineSeparator()
        }
        return output
    }

    fun getReadableParametersForCombined(outcomes: List<OffensiveGenome>): String {
        val sortedOutcomes = outcomes.sortedByDescending { it.getHvValue() }
        var output = ""
        for (i in sortedOutcomes.indices) {
            output += "Solution ${i + 1}/${sortedOutcomes.size}" + System.lineSeparator()
            output += "Offensive genome:" + System.lineSeparator()
            output += "Hashcode = " + sortedOutcomes[i].hashCode() + System.lineSeparator()
            output += "Choice parameter = " + sortedOutcomes[i].getChoiceParameter().round() +
                    System.lineSeparator()
            for (parameter in Parameter.values()) {
                output += "$parameter = ${
                    sortedOutcomes[i].getParameter(parameter).round()
                }" + System.lineSeparator()
            }
            output += "Defensive genome:" + System.lineSeparator()
            output += "Hashcode = " + sortedOutcomes[i].bestDefensiveGenome.hashCode() + System.lineSeparator()
            for (parameter in Parameter.values()) {
                output += "$parameter = ${
                    sortedOutcomes[i].bestDefensiveGenome!!.getParameter(parameter).round()
                }" + System.lineSeparator()
            }
            output += "Strategy details:" + System.lineSeparator()
            for (strategyDetails in sortedOutcomes[i].strategyDetailsWithDefensiveGenome!!.withIndex()) {
                output += "Strategy details ${strategyDetails.index + 1}: ${strategyDetails.value}" + System.lineSeparator()
            }
            output += System.lineSeparator()
        }
        return output
    }
}

fun Double.round(): String {
    return String.format("%.2f", this)
}
