package experiment

import CROSSOVER_CHANCE
import MUTATION_CHANCE
import RUNS
import TOURNAMENT_PICKS
import data.DataLoader
import eaHvConfigurationParameters
import log
import metaheuristic.*
import model.Date
import moeaDConfigurationParameters
import nsgaIIConfigurationParameters
import ntga2ConfigurationParameters
import output.ConfigurationChartDrawer
import output.ConfigurationTestChartDrawer
import simulation.*
import spea2ConfigurationParameters
import kotlin.system.measureTimeMillis

class ConfigurationTestExperiment : Experiment("configuration") {
    private val labels = arrayOf(
        "NTGA2",
        "cNTGA2",
    )

    private val genericNtgaName = "$name-generic-ntga2"
    private val coevolutionNtgaName = "$name-coevolution-ntga2"
    private val genericGeneticAlgorithmNtga2 = GenericGeneticAlgorithm(
        genericNtgaName,
        "${logString}No coevolution, NTGA2",
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
    private val coevolutionGeneticAlgorithmNtga2 = CoevolutionGeneticAlgorithm(
        coevolutionNtgaName,
        "${logString}Coevolution, NTGA2",
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
    private lateinit var purityValues: List<Double>
    private var timeValues = mutableListOf<Long>()

    override fun run() {
        val state = loadState()
        timeValues = mutableListOf()
        lateinit var genericGeneticAlgorithmNtga2State: GenericGeneticAlgorithmState
        lateinit var coevolutionGeneticAlgorithmNtga2State: CoevolutionGeneticAlgorithmState
        CROSSOVER_CHANCE = ntga2ConfigurationParameters[0] as Double
        MUTATION_CHANCE = ntga2ConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = ntga2ConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            genericGeneticAlgorithmNtga2State =
                (Runner.runCombining(genericGeneticAlgorithmNtga2, state[0], RUNS) as GenericGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = ntga2ConfigurationParameters[0] as Double
        MUTATION_CHANCE = ntga2ConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = ntga2ConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            coevolutionGeneticAlgorithmNtga2State = (Runner.runCombining(
                coevolutionGeneticAlgorithmNtga2, state[1], RUNS
            ) as CoevolutionGeneticAlgorithmState)
        } / 1000
        genericGeneticAlgorithmNtga2State.save(genericNtgaName)
        coevolutionGeneticAlgorithmNtga2State.save(coevolutionNtgaName)
    }

    @Suppress("UNCHECKED_CAST")
    override fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>> {
        val state = loadState()
        val geneticAlgorithmOutcomeNtga2 = state[0] as GenericGeneticAlgorithmState
        val coevolutionGeneticAlgorithmOutcomeNtga2 = state[1] as CoevolutionGeneticAlgorithmState

        val outcomes = mutableListOf(
            geneticAlgorithmOutcomeNtga2.archive.toList(),
            coevolutionGeneticAlgorithmOutcomeNtga2.archive.toList(),
        )

        val purityValues = mutableListOf<Double>()
        val mainFront = outcomes[0] + outcomes[1]
        val evaluatedMainFront = paretoEvaluateOffensiveGenomes(mainFront).map {
            SimulationOutcome(
                it.profitsWithDefensiveGenome!!,
                it.riskWithDefensiveGenome!!
            )
        }
        for (i in outcomes.indices) {
            purityValues += calculateParetoPurity(outcomes[i].map {
                SimulationOutcome(
                    it.profitsWithDefensiveGenome!!,
                    it.riskWithDefensiveGenome!!
                )
            }, evaluatedMainFront)
        }
        this.purityValues = purityValues

        val output = outcomes.map {
            it.map { offensiveGenome ->
                SimulationOutcome(
                    offensiveGenome.profitsWithDefensiveGenome!!,
                    offensiveGenome.riskWithDefensiveGenome!!
                )
            }
        }

        val chartinfo = buildString {
            for (i in 0 until 2) {
                append(
                    "${labels[i]} - Purity: ${"%.3f".format(purityValues[i])}, Pareto Front size: ${output[i].size}, " +
                            "Inverted Generational Distance: ${
                                "%.3f".format(
                                    invertedGenerationalDistanceForSet(output[i].map {
                                        Pair(
                                            it.profits,
                                            it.risk
                                        )
                                    })
                                )
                            }, Hypervolume: ${
                                "%.3f".format(
                                    hvParetoFitnessFunctionForSet(output[i].map {
                                        Pair(
                                            it.profits,
                                            it.risk
                                        )
                                    })
                                )
                            }, Spacing: ${
                                "%.3f".format(
                                    spacingForSet(output[i].map {
                                        Pair(
                                            it.profits,
                                            it.risk
                                        )
                                    })
                                )
                            } Time: ${if(timeValues.size > i) timeValues[i] else "N/A"}s" +
                            System.lineSeparator()
                )
            }
        }
        log(chartinfo)

        return output
    }

    override fun drawChart(outcomes: List<List<SimulationOutcome>>) {
        ConfigurationTestChartDrawer(purityValues.toTypedArray(), timeValues.map { it.toInt() }.toTypedArray()).drawChart(
            outcomes
        )
    }

    override fun saveChart(outcomes: List<List<SimulationOutcome>>) {
        ConfigurationTestChartDrawer(
            purityValues.toTypedArray(),
            timeValues.map { it.toInt() }.toTypedArray()
        ).saveChart("configuration", outcomes)
    }

    private fun loadState(): List<GeneticAlgorithmState> {
        var genericGeneticAlgorithmStateNtga = GenericGeneticAlgorithmState.load(genericNtgaName)
        if (genericGeneticAlgorithmStateNtga == null) {
            genericGeneticAlgorithmStateNtga = genericGeneticAlgorithmNtga2.getEmptyState()
        }
        var coevolutionGeneticAlgorithmStateNtga = CoevolutionGeneticAlgorithmState.load(coevolutionNtgaName)
        if (coevolutionGeneticAlgorithmStateNtga == null) {
            coevolutionGeneticAlgorithmStateNtga = coevolutionGeneticAlgorithmNtga2.getEmptyState()
        }
        return listOf(
            genericGeneticAlgorithmStateNtga,
            coevolutionGeneticAlgorithmStateNtga,
        )
    }

    companion object {
        const val logString = "Simulation for configurations - "
    }
}
