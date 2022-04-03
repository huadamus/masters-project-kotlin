@file:Suppress("UNCHECKED_CAST")

package metaheuristic

import simulation.SimulationOutcome
import model.Date
import model.OffensiveGenome
import simulation.Simulator

class Tester(
    private val periods: List<Pair<Date, Date>>,
    private val selling: Boolean,
    private val developedData: Map<Date, Double>,
    private val emergingData: Map<Date, Double>,
    private val crbData: Map<Date, Double>,
    private val goldUsdData: Map<Date, Double>,
    private val shillerPESP500Data: Map<Date, Double>,
    private val dowToGoldData: Map<Date, Double>,
) {

    fun test(initialSimulationOutcomes: List<OffensiveGenome>): List<Pair<OffensiveGenome, SimulationOutcome>> {
        val output = mutableListOf<Pair<OffensiveGenome, SimulationOutcome>>()
        for (initialSimulationOutcome in initialSimulationOutcomes) {
            val testedSimulationOutcome = Simulator.getCombinedScoreForDoubleGenome(
                initialSimulationOutcome.clone(),
                initialSimulationOutcome.bestDefensiveGenome!!.clone(),
                selling,
                periods,
                developedData,
                emergingData,
                crbData,
                goldUsdData,
                shillerPESP500Data,
                dowToGoldData
            )
            output += Pair(initialSimulationOutcome.clone(), testedSimulationOutcome)
        }
        val tested = paretoEvaluate(output.map { it.second })
        return output.filter { it.second in tested }.sortedByDescending { it.second.profits }
    }
}
