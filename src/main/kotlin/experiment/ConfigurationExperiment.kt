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
import simulation.*
import spea2ConfigurationParameters
import kotlin.system.measureTimeMillis

class ConfigurationExperiment : Experiment("configuration") {
    private val labels = arrayOf(
        "EA-HV",
        "NSGA-II",
        "SPEA2",
        "NTGA2",
        "cEA-HV",
        "cNSGA-II",
        "cSPEA2",
        "cNTGA2",
        "MOEA/D",
    )

    private val genericHvName = "$name-generic-hv"
    private val genericNsgaName = "$name-generic-nsga2"
    private val genericSpeaName = "$name-generic-spea2"
    private val genericNtgaName = "$name-generic-ntga2"
    private val coevolutionHvName = "$name-coevolution-hv"
    private val coevolutionNsgaName = "$name-coevolution-nsga2"
    private val coevolutionSpeaName = "$name-coevolution-spea2"
    private val coevolutionNtgaName = "$name-coevolution-ntga2"
    private val moeaDName = "$name-moead"
    private val genericGeneticAlgorithmHvPareto = GenericGeneticAlgorithm(
        genericHvName,
        "${logString}No coevolution, HV-Pareto",
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
    private val genericGeneticAlgorithmNsgaII = GenericGeneticAlgorithm(
        genericNsgaName,
        "${logString}No coevolution, NSGA-II",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
        SelectionMethod.NSGA_II,
        DataLoader.loadDevelopedData(),
        DataLoader.loadEmergingData(),
        DataLoader.loadCrbAndOilData(),
        DataLoader.loadGoldUsdData(),
        DataLoader.loadShillerPESP500Ratio(),
        DataLoader.loadDowToGoldData()
    )
    private val genericGeneticAlgorithmSpea2 = GenericGeneticAlgorithm(
        genericSpeaName,
        "${logString}No coevolution, SPEA2",
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
    private val coevolutionGeneticAlgorithmHvPareto = CoevolutionGeneticAlgorithm(
        coevolutionHvName,
        "${logString}Coevolution, HV-Pareto",
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
    private val coevolutionGeneticAlgorithmNsgaII = CoevolutionGeneticAlgorithm(
        coevolutionNsgaName,
        "${logString}Coevolution, NSGA-II",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
        SelectionMethod.NSGA_II,
        DataLoader.loadDevelopedData(),
        DataLoader.loadEmergingData(),
        DataLoader.loadCrbAndOilData(),
        DataLoader.loadGoldUsdData(),
        DataLoader.loadShillerPESP500Ratio(),
        DataLoader.loadDowToGoldData()
    )
    private val coevolutionGeneticAlgorithmSpea2 = CoevolutionGeneticAlgorithm(
        coevolutionNsgaName,
        "${logString}Coevolution, SPEA2",
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
    private val moeaDAlgorithm = MoeaDAlgorithm(
        moeaDName,
        "${logString}MOEA/D",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
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
        lateinit var genericGeneticAlgorithmHvParetoState: GenericGeneticAlgorithmState
        lateinit var genericGeneticAlgorithmNsgaIIState: GenericGeneticAlgorithmState
        lateinit var genericGeneticAlgorithmSpea2State: GenericGeneticAlgorithmState
        lateinit var genericGeneticAlgorithmNtga2State: GenericGeneticAlgorithmState
        lateinit var coevolutionGeneticAlgorithmHvParetoState: CoevolutionGeneticAlgorithmState
        lateinit var coevolutionGeneticAlgorithmNsgaIIState: CoevolutionGeneticAlgorithmState
        lateinit var coevolutionGeneticAlgorithmSpea2State: CoevolutionGeneticAlgorithmState
        lateinit var coevolutionGeneticAlgorithmNtga2State: CoevolutionGeneticAlgorithmState
        lateinit var moeaDState: GenericGeneticAlgorithmState
        CROSSOVER_CHANCE = eaHvConfigurationParameters[0] as Double
        MUTATION_CHANCE = eaHvConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = eaHvConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            genericGeneticAlgorithmHvParetoState =
                (Runner.runCombining(genericGeneticAlgorithmHvPareto, state[0], RUNS) as GenericGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = nsgaIIConfigurationParameters[0] as Double
        MUTATION_CHANCE = nsgaIIConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = nsgaIIConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            genericGeneticAlgorithmNsgaIIState =
                (Runner.runCombining(genericGeneticAlgorithmNsgaII, state[1], RUNS) as GenericGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = spea2ConfigurationParameters[0] as Double
        MUTATION_CHANCE = spea2ConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = spea2ConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            genericGeneticAlgorithmSpea2State =
                (Runner.runCombining(genericGeneticAlgorithmSpea2, state[2], RUNS) as GenericGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = ntga2ConfigurationParameters[0] as Double
        MUTATION_CHANCE = ntga2ConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = ntga2ConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            genericGeneticAlgorithmNtga2State =
                (Runner.runCombining(genericGeneticAlgorithmNtga2, state[3], RUNS) as GenericGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = eaHvConfigurationParameters[0] as Double
        MUTATION_CHANCE = eaHvConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = eaHvConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            coevolutionGeneticAlgorithmHvParetoState =
                (Runner.runCombining(
                    coevolutionGeneticAlgorithmHvPareto, state[4], RUNS
                ) as CoevolutionGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = nsgaIIConfigurationParameters[0] as Double
        MUTATION_CHANCE = nsgaIIConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = nsgaIIConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            coevolutionGeneticAlgorithmNsgaIIState =
                (Runner.runCombining(
                    coevolutionGeneticAlgorithmNsgaII, state[5], RUNS
                ) as CoevolutionGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = ntga2ConfigurationParameters[0] as Double
        MUTATION_CHANCE = ntga2ConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = ntga2ConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            coevolutionGeneticAlgorithmSpea2State =
                (Runner.runCombining(
                    coevolutionGeneticAlgorithmSpea2, state[6], RUNS
                ) as CoevolutionGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = ntga2ConfigurationParameters[0] as Double
        MUTATION_CHANCE = ntga2ConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = ntga2ConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            coevolutionGeneticAlgorithmNtga2State = (Runner.runCombining(
                coevolutionGeneticAlgorithmNtga2, state[7], RUNS
            ) as CoevolutionGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = moeaDConfigurationParameters[0]
        MUTATION_CHANCE = moeaDConfigurationParameters[1]
        timeValues += measureTimeMillis {
            moeaDState = (Runner.runCombining(
                moeaDAlgorithm, state[8], RUNS
            ) as GenericGeneticAlgorithmState)
        } / 1000
        genericGeneticAlgorithmHvParetoState.save(genericHvName)
        genericGeneticAlgorithmNsgaIIState.save(genericNsgaName)
        genericGeneticAlgorithmSpea2State.save(genericSpeaName)
        genericGeneticAlgorithmNtga2State.save(genericNtgaName)
        coevolutionGeneticAlgorithmHvParetoState.save(coevolutionHvName)
        coevolutionGeneticAlgorithmNsgaIIState.save(coevolutionNsgaName)
        coevolutionGeneticAlgorithmSpea2State.save(coevolutionSpeaName)
        coevolutionGeneticAlgorithmNtga2State.save(coevolutionNtgaName)
        moeaDState.save(moeaDName)
    }

    @Suppress("UNCHECKED_CAST")
    override fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>> {
        val state = loadState()
        val geneticAlgorithmOutcomeHv = state[0] as GenericGeneticAlgorithmState
        val geneticAlgorithmOutcomeNsga = state[1] as GenericGeneticAlgorithmState
        val geneticAlgorithmOutcomeSpea2 = state[2] as GenericGeneticAlgorithmState
        val geneticAlgorithmOutcomeNtga2 = state[3] as GenericGeneticAlgorithmState
        val coevolutionGeneticAlgorithmOutcomeHv = state[4] as CoevolutionGeneticAlgorithmState
        val coevolutionGeneticAlgorithmOutcomeNsga = state[5] as CoevolutionGeneticAlgorithmState
        val coevolutionGeneticAlgorithmOutcomeSpea2 = state[6] as CoevolutionGeneticAlgorithmState
        val coevolutionGeneticAlgorithmOutcomeNtga2 = state[7] as CoevolutionGeneticAlgorithmState
        val moeaDOutcome = state[8] as GenericGeneticAlgorithmState

        val outcomes = mutableListOf(
            geneticAlgorithmOutcomeHv.archive.toList(),
            geneticAlgorithmOutcomeNsga.archive.toList(),
            geneticAlgorithmOutcomeSpea2.archive.toList(),
            geneticAlgorithmOutcomeNtga2.archive.toList(),
            coevolutionGeneticAlgorithmOutcomeHv.archive.toList(),
            coevolutionGeneticAlgorithmOutcomeNsga.archive.toList(),
            coevolutionGeneticAlgorithmOutcomeSpea2.archive.toList(),
            coevolutionGeneticAlgorithmOutcomeNtga2.archive.toList(),
            moeaDOutcome.archive.toList()
        )

        val purityValues = mutableListOf<Double>()
        val mainFront = outcomes[0] + outcomes[1] + outcomes[2] + outcomes[3] + outcomes[4] + outcomes[5] +
                outcomes[6] + outcomes[7] + outcomes[8]
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
            for (i in 0 until 9) {
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
        ConfigurationChartDrawer(purityValues.toTypedArray(), timeValues.map { it.toInt() }.toTypedArray()).drawChart(
            outcomes
        )
    }

    override fun saveChart(outcomes: List<List<SimulationOutcome>>) {
        ConfigurationChartDrawer(
            purityValues.toTypedArray(),
            timeValues.map { it.toInt() }.toTypedArray()
        ).saveChart("configuration", outcomes)
    }

    private fun loadState(): List<GeneticAlgorithmState> {
        var genericGeneticAlgorithmStateHv = GenericGeneticAlgorithmState.load(genericHvName)
        if (genericGeneticAlgorithmStateHv == null) {
            genericGeneticAlgorithmStateHv = genericGeneticAlgorithmHvPareto.getEmptyState()
        }
        var genericGeneticAlgorithmStateNsga = GenericGeneticAlgorithmState.load(genericNsgaName)
        if (genericGeneticAlgorithmStateNsga == null) {
            genericGeneticAlgorithmStateNsga = genericGeneticAlgorithmNsgaII.getEmptyState()
        }
        var genericGeneticAlgorithmStateSpea = GenericGeneticAlgorithmState.load(genericSpeaName)
        if (genericGeneticAlgorithmStateSpea == null) {
            genericGeneticAlgorithmStateSpea = genericGeneticAlgorithmSpea2.getEmptyState()
        }
        var genericGeneticAlgorithmStateNtga = GenericGeneticAlgorithmState.load(genericNtgaName)
        if (genericGeneticAlgorithmStateNtga == null) {
            genericGeneticAlgorithmStateNtga = genericGeneticAlgorithmNtga2.getEmptyState()
        }
        var coevolutionGeneticAlgorithmStateHv = CoevolutionGeneticAlgorithmState.load(coevolutionHvName)
        if (coevolutionGeneticAlgorithmStateHv == null) {
            coevolutionGeneticAlgorithmStateHv = coevolutionGeneticAlgorithmHvPareto.getEmptyState()
        }
        var coevolutionGeneticAlgorithmStateNsga = CoevolutionGeneticAlgorithmState.load(coevolutionNsgaName)
        if (coevolutionGeneticAlgorithmStateNsga == null) {
            coevolutionGeneticAlgorithmStateNsga = coevolutionGeneticAlgorithmNsgaII.getEmptyState()
        }
        var coevolutionGeneticAlgorithmStateSpea = CoevolutionGeneticAlgorithmState.load(coevolutionSpeaName)
        if (coevolutionGeneticAlgorithmStateSpea == null) {
            coevolutionGeneticAlgorithmStateSpea = coevolutionGeneticAlgorithmSpea2.getEmptyState()
        }
        var coevolutionGeneticAlgorithmStateNtga = CoevolutionGeneticAlgorithmState.load(coevolutionNtgaName)
        if (coevolutionGeneticAlgorithmStateNtga == null) {
            coevolutionGeneticAlgorithmStateNtga = coevolutionGeneticAlgorithmNtga2.getEmptyState()
        }
        var moeaDState = GenericGeneticAlgorithmState.load(moeaDName)
        if (moeaDState == null) {
            moeaDState = moeaDAlgorithm.getEmptyState()
        }
        return listOf(
            genericGeneticAlgorithmStateHv,
            genericGeneticAlgorithmStateNsga,
            genericGeneticAlgorithmStateSpea,
            genericGeneticAlgorithmStateNtga,
            coevolutionGeneticAlgorithmStateHv,
            coevolutionGeneticAlgorithmStateNsga,
            coevolutionGeneticAlgorithmStateSpea,
            coevolutionGeneticAlgorithmStateNtga,
            moeaDState,
        )
    }

    companion object {
        const val logString = "Simulation for configurations - "
    }
}
