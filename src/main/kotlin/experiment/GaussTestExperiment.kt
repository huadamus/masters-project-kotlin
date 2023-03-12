package experiment

import CROSSOVER_CHANCE
import GAUSS_MUTATION
import MUTATION_CHANCE
import RUNS
import TOURNAMENT_PICKS
import data.DataLoader
import log
import metaheuristic.GenericGeneticAlgorithm
import metaheuristic.GenericGeneticAlgorithmState
import metaheuristic.SelectionMethod
import model.Date
import simulation.Runner
import simulation.SimulationOutcome
import simulation.hvParetoFitnessFunctionForSet

class GaussMutationExperiment : Experiment("gauss") {
    private val methodsSet = listOf(
        SelectionMethod.NSGA_II,
        SelectionMethod.NTGA2
    )

    override fun run() {
        GAUSS_MUTATION = false
        for (method in methodsSet) {
            val name = "gauss_false_${method}_"
            val geneticAlgorithm = GenericGeneticAlgorithm(
                name,
                "$method",
                listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
                360,
                true,
                method,
                DataLoader.loadDevelopedData(),
                DataLoader.loadEmergingData(),
                DataLoader.loadCommodityData(),
                DataLoader.loadGoldUsdData(),
                DataLoader.loadShillerPESP500Ratio(),
                DataLoader.loadDowToGoldData()
            )
            (Runner.runCombining("gauss test gauss_off",
                geneticAlgorithm, geneticAlgorithm.getEmptyState(), RUNS
            ) as GenericGeneticAlgorithmState).save(name)
        }
        GAUSS_MUTATION = true
        for (method in methodsSet) {
            val name = "gauss_true_${method}_"
            val geneticAlgorithm = GenericGeneticAlgorithm(
                name,
                "$method",
                listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
                360,
                true,
                method,
                DataLoader.loadDevelopedData(),
                DataLoader.loadEmergingData(),
                DataLoader.loadCommodityData(),
                DataLoader.loadGoldUsdData(),
                DataLoader.loadShillerPESP500Ratio(),
                DataLoader.loadDowToGoldData()
            )
            (Runner.runCombining("gauss test gauss_on",
                geneticAlgorithm, geneticAlgorithm.getEmptyState(), RUNS
            ) as GenericGeneticAlgorithmState).save(name)
        }
    }

    override fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>> {
        for (method in methodsSet) {
            var name = "gauss_false_${method}_"
            val result = Result(
                CROSSOVER_CHANCE,
                MUTATION_CHANCE,
                TOURNAMENT_PICKS,
                loadState(name)
            )
            val hv = hvParetoFitnessFunctionForSet(result.state.archive.map {
                Pair(
                    it.profitsWithDefensiveGenome!!,
                    it.riskWithDefensiveGenome!!
                )
            })
            log("$hv")
        }
        for (method in methodsSet) {
            var name = "gauss_true_${method}_"
            val result = Result(
                CROSSOVER_CHANCE,
                MUTATION_CHANCE,
                TOURNAMENT_PICKS,
                loadState(name)
            )
            val hv = hvParetoFitnessFunctionForSet(result.state.archive.map {
                Pair(
                    it.profitsWithDefensiveGenome!!,
                    it.riskWithDefensiveGenome!!
                )
            })
            log("$hv")
        }
        return listOf()
    }

    override fun drawChart(outcomes: List<List<SimulationOutcome>>) {
        throw Exception("This experiment does not draw charts.")
    }

    override fun saveChart(outcomes: List<List<SimulationOutcome>>) {
        throw Exception("This experiment does not draw charts.")
    }

    private fun loadState(name: String): GenericGeneticAlgorithmState {
        return GenericGeneticAlgorithmState.load(name)
            ?: throw Exception("There is a problem with reading the experiment results")
    }

    open class Result(
        private val crossoverChance: Double,
        private val mutationChance: Double,
        private val tournamentPicks: Int,
        val state: GenericGeneticAlgorithmState
    ) {
        override fun toString(): String {
            return "cross: $crossoverChance | mut: $mutationChance | picks: $tournamentPicks"
        }
    }

    class Spea2Result(
        crossoverChance: Double,
        mutationChance: Double,
        tournamentPicks: Int,
        private val nearestDistanceParameter: Int,
        state: GenericGeneticAlgorithmState
    ) : Result(crossoverChance, mutationChance, tournamentPicks, state) {

        override fun toString(): String {
            return super.toString() + " | $nearestDistanceParameter"
        }
    }

    class Ntga2Result(
        crossoverChance: Double,
        mutationChance: Double,
        tournamentPicks: Int,
        private val gsGenerations: Int,
        state: GenericGeneticAlgorithmState
    ) : Result(crossoverChance, mutationChance, tournamentPicks, state) {

        override fun toString(): String {
            return super.toString() + " | $gsGenerations"
        }
    }

    companion object {
        const val logString = "Simulation for gauss - "
    }
}
