package experiment

import RUNS
import SPEA2_NEAREST_DISTANCE
import data.DataLoader
import metaheuristic.GenericGeneticAlgorithm
import metaheuristic.GenericGeneticAlgorithmState
import metaheuristic.SelectionMethod
import model.Date
import simulation.Runner
import simulation.SimulationOutcome
import simulation.hvParetoFitnessFunctionForSet

class ParametrizationExperimentSpea2 : Experiment("spea2_parametrization") {
    private val spea2NearestDistanceParameterSet = IntRange(1, 10)

    override fun run() {
        for (nearestDistanceParameter in spea2NearestDistanceParameterSet) {
            val name = "parametrization_spea2_${nearestDistanceParameter}"
            SPEA2_NEAREST_DISTANCE = nearestDistanceParameter
            val geneticAlgorithm = GenericGeneticAlgorithm(
                name,
                "$logString $nearestDistanceParameter",
                listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
                360,
                true,
                SelectionMethod.SPEA2,
                DataLoader.loadDevelopedData(),
                DataLoader.loadEmergingData(),
                DataLoader.loadCrbAndOilData(),
                DataLoader.loadGoldUsdData(),
                DataLoader.loadShillerPESP500Ratio(),
                DataLoader.loadDowToGoldData()
            )
            (Runner.runCombining(
                geneticAlgorithm, geneticAlgorithm.getEmptyState(), RUNS
            ) as GenericGeneticAlgorithmState).save(name)
        }
    }

    override fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>> {
        val results = mutableListOf<Result>()
        for (nearestDistanceParameter in spea2NearestDistanceParameterSet) {
            val name = "parametrization_spea2_${nearestDistanceParameter}"
            results += Result(nearestDistanceParameter, loadState(name))
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
        val nearestDistanceParameter: Int,
        val state: GenericGeneticAlgorithmState
    ) {
        override fun toString(): String {
            return "nearest distance: $nearestDistanceParameter"
        }
    }

    companion object {
        const val logString = "Simulation for SPEA2 parametrization - "
    }
}
