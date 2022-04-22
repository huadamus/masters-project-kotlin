package experiment

import RUNS
import data.DataLoader
import metaheuristic.GenericGeneticAlgorithm
import metaheuristic.GenericGeneticAlgorithmState
import metaheuristic.SelectionMethod
import model.Date
import simulation.Runner
import simulation.SimulationOutcome
import simulation.hvParetoFitnessFunctionForSet

class ParametrizationExperiment : Experiment("parametrization") {
    private val crossoverChanceSet = IntRange(70, 90) step 5
    private val mutationChanceSet = IntRange(6, 12) step 2
    private val tournamentPicksSet = IntRange(8, 24) step 2

    override fun run() {
        for (crossoverChance in crossoverChanceSet) {
            val realCrossoverChance = crossoverChance.toDouble() / 100.0
            for (mutationChance in mutationChanceSet) {
                val realMutationChance = mutationChance.toDouble() / 100.0
                for (tournamentPicks in tournamentPicksSet) {
                    val name = "parametrization_${
                        realCrossoverChance
                    }_${realMutationChance}_${tournamentPicks}"
                    val geneticAlgorithm = GenericGeneticAlgorithm(
                        name,
                        "$logString $realCrossoverChance $realMutationChance $tournamentPicks",
                        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
                        360,
                        true,
                        SelectionMethod.HV_PARETO,
                        DataLoader.loadDevelopedData(),
                        DataLoader.loadEmergingData(),
                        DataLoader.loadCrbAndOilData(),
                        DataLoader.loadGoldUsdData(),
                        DataLoader.loadShillerPESP500Ratio(),
                        DataLoader.loadDowToGoldData()
                    )
                    (Runner.runCombining(geneticAlgorithm, RUNS) as GenericGeneticAlgorithmState).save(name)
                }
            }
        }
    }

    override fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>> {
        val results = mutableListOf<Result>()
        for (crossoverChance in crossoverChanceSet) {
            val realCrossoverChance = crossoverChance.toDouble() / 100.0
            for (mutationChance in mutationChanceSet) {
                val realMutationChance = mutationChance.toDouble() / 100.0
                for (tournamentPicks in tournamentPicksSet) {
                    val name = "parametrization_${
                        realCrossoverChance
                    }_${realMutationChance}_${tournamentPicks}"
                    results += Result(realCrossoverChance, realMutationChance, tournamentPicks, loadState(name))
                }
            }
        }

        val hvValues = mutableListOf<Double>()
        for (result in results) {
            hvValues += hvParetoFitnessFunctionForSet(result.state.archive.map {
                Pair(
                    it.profitsWithDefensiveGenome!!,
                    it.riskWithDefensiveGenome!!
                )
            })
        }

        val combinedResults = results.zip(hvValues).sortedByDescending { it.second }
        for (combinedResult in combinedResults) {
            println("${combinedResult.first}, ${combinedResult.second}")
        }

        return listOf()
    }

    override fun drawChart(outcomes: List<List<SimulationOutcome>>) {
        throw Exception("This experiment does not draw charts.")
    }

    override fun saveChart(runId: Int, outcomes: List<List<SimulationOutcome>>) {
        throw Exception("This experiment does not draw charts.")
    }

    private fun loadState(name: String): GenericGeneticAlgorithmState {
        return GenericGeneticAlgorithmState.load(name)
            ?: throw Exception("There is a problem with reading the experiment results")
    }

    data class Result(
        val crossoverChance: Double,
        val mutationChance: Double,
        val tournamentPicks: Int,
        val state: GenericGeneticAlgorithmState
    ) {
        override fun toString(): String {
            return "cross: $crossoverChance | mut: $mutationChance | picks: $tournamentPicks"
        }
    }

    companion object {
        const val logString = "Simulation for parametrization - "
    }
}
