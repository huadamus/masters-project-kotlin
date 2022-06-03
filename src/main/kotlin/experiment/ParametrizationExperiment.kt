package experiment

import CROSSOVER_CHANCE
import MUTATION_CHANCE
import NTGA2_GS_GENERATIONS
import RUNS
import SPEA2_NEAREST_DISTANCE
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

class ParametrizationExperiment : Experiment("parametrization") {
    private val methodsSet = listOf(
        SelectionMethod.HV_PARETO,
        SelectionMethod.NSGA_II,
        SelectionMethod.SPEA2,
        SelectionMethod.NTGA2
    )
    private val crossoverChanceSet = IntRange(70, 90) step 5
    private val mutationChanceSet = IntRange(6, 12) step 2
    private val tournamentPicksSet = IntRange(8, 24) step 2
    private val spea2NearestDistanceParameterSet = IntRange(1, 11) step 2
    private val ntga2GsGenerationsSet = IntRange(1, 51) step 10

    override fun run() {
        for (method in methodsSet) {
            for (crossoverChance in crossoverChanceSet) {
                val realCrossoverChance = crossoverChance.toDouble() / 100.0
                for (mutationChance in mutationChanceSet) {
                    val realMutationChance = mutationChance.toDouble() / 100.0
                    for (tournamentPicks in tournamentPicksSet) {
                        var name = "parametrization_${method}_${
                            realCrossoverChance
                        }_${realMutationChance}_${tournamentPicks}"
                        CROSSOVER_CHANCE = realCrossoverChance
                        MUTATION_CHANCE = realMutationChance
                        TOURNAMENT_PICKS = tournamentPicks
                        when (method) {
                            SelectionMethod.HV_PARETO, SelectionMethod.NSGA_II -> {
                                val geneticAlgorithm = GenericGeneticAlgorithm(
                                    name,
                                    "$logString $method $realCrossoverChance $realMutationChance $tournamentPicks",
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
                                println("Running algorithm for $method $CROSSOVER_CHANCE $MUTATION_CHANCE $TOURNAMENT_PICKS")
                                (Runner.runCombining(
                                    geneticAlgorithm, geneticAlgorithm.getEmptyState(), RUNS
                                ) as GenericGeneticAlgorithmState).save(name)
                            }
                            SelectionMethod.SPEA2 -> {
                                for (nearestDistanceParameter in spea2NearestDistanceParameterSet) {
                                    SPEA2_NEAREST_DISTANCE = nearestDistanceParameter
                                    name += "_${nearestDistanceParameter}"
                                    val geneticAlgorithm = GenericGeneticAlgorithm(
                                        name,
                                        "Parameterization SPEA2 $crossoverChance $mutationChance $tournamentPicks $nearestDistanceParameter",
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
                            SelectionMethod.NTGA2 -> {
                                for (gsGenerations in ntga2GsGenerationsSet) {
                                    NTGA2_GS_GENERATIONS = gsGenerations
                                    name += "_${gsGenerations}"
                                    val geneticAlgorithm = GenericGeneticAlgorithm(
                                        name,
                                        "Parameterization NTGA2 $crossoverChance $mutationChance $tournamentPicks $gsGenerations",
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
                        }
                    }
                }
            }
        }
    }

    override fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>> {
        for (method in methodsSet) {
            val results = mutableListOf<Result>()
            for (crossoverChance in crossoverChanceSet) {
                val realCrossoverChance = crossoverChance.toDouble() / 100.0
                for (mutationChance in mutationChanceSet) {
                    val realMutationChance = mutationChance.toDouble() / 100.0
                    for (tournamentPicks in tournamentPicksSet) {
                        var name =
                            "parametrization_${method}_${realCrossoverChance}_${realMutationChance}_${tournamentPicks}"
                        when (method) {
                            SelectionMethod.HV_PARETO, SelectionMethod.NSGA_II ->
                                results += Result(
                                    realCrossoverChance,
                                    realMutationChance,
                                    tournamentPicks,
                                    loadState(name)
                                )
                            SelectionMethod.SPEA2 -> {
                                for (nearestDistanceParameter in spea2NearestDistanceParameterSet) {
                                    name += "_${nearestDistanceParameter}"
                                    results += Spea2Result(
                                        realCrossoverChance,
                                        realMutationChance,
                                        tournamentPicks,
                                        nearestDistanceParameter,
                                        loadState(name)
                                    )
                                }
                            }
                            SelectionMethod.NTGA2 -> {
                                for (gsGenerations in ntga2GsGenerationsSet) {
                                    name += "_${gsGenerations}"
                                    results += Ntga2Result(
                                        realCrossoverChance,
                                        realMutationChance,
                                        tournamentPicks,
                                        gsGenerations,
                                        loadState(name)
                                    )
                                }
                            }
                        }
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
            log(method.toString())
            for (combinedResult in combinedResults) {
                log("${combinedResult.first}, ${combinedResult.second}")
            }
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
        const val logString = "Simulation for parametrization - "
    }
}
