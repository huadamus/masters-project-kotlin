@file:Suppress("UNCHECKED_CAST")

package metaheuristic

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

    fun test(initialSimulationOutcomes: List<OffensiveGenome>): List<OffensiveGenome> {
        val output = mutableListOf<OffensiveGenome>()
        for (initialSimulationOutcome in initialSimulationOutcomes) {
            val testedSimulationOutcome = Simulator.getScoreForOffensiveGenome(
                initialSimulationOutcome.clone(),
                selling,
                periods,
                developedData,
                emergingData,
                crbData,
                goldUsdData,
                shillerPESP500Data,
                dowToGoldData
            )
            output += testedSimulationOutcome
        }
        val tested = paretoEvaluateOffensiveGenomes(output)
        return tested.sortedByDescending { it.profitsWithDefensiveGenome!! }
    }
}
