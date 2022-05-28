package experiment

import NTGA2_GS_GENERATIONS
import RUNS
import data.DataLoader
import metaheuristic.GenericGeneticAlgorithm
import metaheuristic.GenericGeneticAlgorithmState
import metaheuristic.SelectionMethod
import model.Date
import simulation.Runner
import simulation.SimulationOutcome
import simulation.hvParetoFitnessFunctionForSet

class ParametrizationExperimentNtga2 : Experiment("ntga2_parametrization") {
    private val ntga2GsGenerationsSet = IntRange(1, 20) step 2

    override fun run() {
        for (gsGenerations in ntga2GsGenerationsSet) {
            val name = "parametrization_ntga2_${gsGenerations}"
            NTGA2_GS_GENERATIONS = gsGenerations
            val geneticAlgorithm = GenericGeneticAlgorithm(
                name,
                "$logString $gsGenerations",
                listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
                360,
                true,
                SelectionMethod.NTGA2,
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
        for (gapGenerations in ntga2GsGenerationsSet) {
            val name = "parametrization_ntga2_${gapGenerations}"
            results += Result(gapGenerations, loadState(name))
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
        val gapGenerations: Int,
        val state: GenericGeneticAlgorithmState
    ) {
        override fun toString(): String {
            return "Gap generations: $gapGenerations"
        }
    }

    companion object {
        const val logString = "Simulation for NTGA2 parametrization - "
    }
}
