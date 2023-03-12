package experiment

import CROSSOVER_CHANCE
import MUTATION_CHANCE
import RUNS
import SAVE_MIDPOINT
import TOURNAMENT_PICKS
import data.DataLoader
import eaHvConfigurationParameters
import log
import metaheuristic.*
import model.Date
import model.OffensiveGenome
import moeaDConfigurationParameters
import nsgaIIConfigurationParameters
import ntga2ConfigurationParameters
import output.ConfigurationChartDrawer
import simulation.*
import simulation.portfolio.Portfolio
import spea2ConfigurationParameters
import java.io.File
import kotlin.system.measureTimeMillis

class ConfigurationExperiment : Experiment("configuration") {
    private val labels = arrayOf(
        "EA-HV",
        "NSGA-II",
        "SPEA2",
        "NTGA2",
        "MOEA-D",
    )

    private val genericHvName = "$name-generic-hv"
    private val genericNsgaName = "$name-generic-nsga2"
    private val genericSpeaName = "$name-generic-spea2"
    private val genericNtgaName = "$name-generic-ntga2"
    private val genericMoeaDName = "$name-generic-moead"
    private val genericGeneticAlgorithmHvPareto = GenericGeneticAlgorithm(
        genericHvName,
        "${logString}No coevolution, HV-Pareto",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
        SelectionMethod.HV_PARETO,
        DataLoader.loadDevelopedData(),
        DataLoader.loadEmergingData(),
        DataLoader.loadCommodityData(),
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
        DataLoader.loadCommodityData(),
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
        DataLoader.loadCommodityData(),
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
        DataLoader.loadCommodityData(),
        DataLoader.loadGoldUsdData(),
        DataLoader.loadShillerPESP500Ratio(),
        DataLoader.loadDowToGoldData()
    )
    private val genericMoeaDAlgorithm = MoeaDAlgorithm(
        genericMoeaDName,
        "${logString}No coevolution, MOEA/D",
        listOf(Pair(Date(1, 1, 1988), Date(1, 1, 2018))),
        360,
        true,
        DataLoader.loadDevelopedData(),
        DataLoader.loadEmergingData(),
        DataLoader.loadCommodityData(),
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
        lateinit var genericMoeaDState: GenericGeneticAlgorithmState
        CROSSOVER_CHANCE = eaHvConfigurationParameters[0] as Double
        MUTATION_CHANCE = eaHvConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = eaHvConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            genericGeneticAlgorithmHvParetoState =
                (Runner.runCombining(labels[0], genericGeneticAlgorithmHvPareto, state[0], RUNS) as GenericGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = nsgaIIConfigurationParameters[0] as Double
        MUTATION_CHANCE = nsgaIIConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = nsgaIIConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            genericGeneticAlgorithmNsgaIIState =
                (Runner.runCombining(labels[1], genericGeneticAlgorithmNsgaII, state[1], RUNS) as GenericGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = spea2ConfigurationParameters[0] as Double
        MUTATION_CHANCE = spea2ConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = spea2ConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            genericGeneticAlgorithmSpea2State =
                (Runner.runCombining(labels[2], genericGeneticAlgorithmSpea2, state[2], RUNS) as GenericGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = ntga2ConfigurationParameters[0] as Double
        MUTATION_CHANCE = ntga2ConfigurationParameters[1] as Double
        TOURNAMENT_PICKS = ntga2ConfigurationParameters[2] as Int
        timeValues += measureTimeMillis {
            genericGeneticAlgorithmNtga2State =
                (Runner.runCombining(labels[3], genericGeneticAlgorithmNtga2, state[3], RUNS) as GenericGeneticAlgorithmState)
        } / 1000
        CROSSOVER_CHANCE = moeaDConfigurationParameters[0]
        MUTATION_CHANCE = moeaDConfigurationParameters[1]
        timeValues += measureTimeMillis {
            genericMoeaDState = (Runner.runCombining(labels[4],
                genericMoeaDAlgorithm, state[4], RUNS
            ) as GenericGeneticAlgorithmState)
        } / 1000
        genericGeneticAlgorithmHvParetoState.save(genericHvName)
        genericGeneticAlgorithmNsgaIIState.save(genericNsgaName)
        genericGeneticAlgorithmSpea2State.save(genericSpeaName)
        genericGeneticAlgorithmNtga2State.save(genericNtgaName)
        genericMoeaDState.save(genericMoeaDName)
    }

    @Suppress("UNCHECKED_CAST")
    override fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>> {
        val state = loadState()
        val geneticAlgorithmOutcomeHv = state[0] as GenericGeneticAlgorithmState
        val geneticAlgorithmOutcomeNsga = state[1] as GenericGeneticAlgorithmState
        val geneticAlgorithmOutcomeSpea2 = state[2] as GenericGeneticAlgorithmState
        val geneticAlgorithmOutcomeNtga2 = state[3] as GenericGeneticAlgorithmState
        val moeaDOutcome = state[4] as GenericGeneticAlgorithmState

        val outcomes = mutableListOf(
            geneticAlgorithmOutcomeHv.archive.toList(),
            geneticAlgorithmOutcomeNsga.archive.toList(),
            geneticAlgorithmOutcomeSpea2.archive.toList(),
            geneticAlgorithmOutcomeNtga2.archive.toList(),
            moeaDOutcome.archive.toList(),
        )

        saveCsv(outcomes)

        val purityValues = mutableListOf<Double>()
        val mainFront = outcomes[0] + outcomes[1] + outcomes[2] + outcomes[3] + outcomes[4]
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
                val out = SimulationOutcome(
                    offensiveGenome.profitsWithDefensiveGenome!!,
                    offensiveGenome.riskWithDefensiveGenome!!
                )
                out.genome = offensiveGenome
                out
            }
        }

        val chartinfo = buildString {
            for (i in 0 until 5) {
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
                            } Time: ${if (timeValues.size > i) timeValues[i] else "N/A"}s" +
                            System.lineSeparator()
                )
            }
        }
        log(chartinfo)

        if(SAVE_MIDPOINT) {
            val states = loadMiddleStates()
            for (label in labels.withIndex()) {
                for (i in 0 until RUNS) {
                    val currentState = states[i][label.index] as GenericGeneticAlgorithmState
                    val currentPurityValue = calculateParetoPurity(currentState.archive.map {
                        SimulationOutcome(
                            it.profitsWithDefensiveGenome!!,
                            it.riskWithDefensiveGenome!!
                        )
                    }, evaluatedMainFront)
                    log("Run $i, method ${label.value} individual performance:")
                    log("Purity: ${"%.3f".format(currentPurityValue)}, Pareto Front size: ${currentState.archive.size}, " +
                            "Inverted Generational Distance: ${
                                "%.3f".format(
                                    invertedGenerationalDistanceForSet(currentState.archive.map {
                                        Pair(
                                            it.profitsWithDefensiveGenome!!,
                                            it.riskWithDefensiveGenome!!
                                        )
                                    })
                                )
                            }, Hypervolume: ${
                                "%.3f".format(
                                    hvParetoFitnessFunctionForSet(currentState.archive.map {
                                        Pair(
                                            it.profitsWithDefensiveGenome!!,
                                            it.riskWithDefensiveGenome!!
                                        )
                                    })
                                )
                            }, Spacing: ${
                                "%.3f".format(
                                    spacingForSet(currentState.archive.map {
                                        Pair(
                                            it.profitsWithDefensiveGenome!!,
                                            it.riskWithDefensiveGenome!!
                                        )
                                    })
                                )
                            } Time: ${if (timeValues.size > i) timeValues[i] else "N/A"}s")
                }
            }
        }

        lateinit var maximumProfits: SimulationOutcome
        var assignedGroupProfits = 0
        var max = 0.0
        for (group in output.indices) {
            for (case in output[group]) {
                if (case.profits > max) {
                    max = case.profits
                    maximumProfits = case
                    assignedGroupProfits = group + 1
                }
            }
        }

        lateinit var maximumTransactions: SimulationOutcome
        var assignedGroupTransactions = 0
        var maxT = 0
        for (group in output.indices) {
            for (case in output[group]) {
                if ((case.genome as OffensiveGenome).strategyDetailsWithDefensiveGenome!![0].assetsList.size > maxT) {
                    maxT = (case.genome as OffensiveGenome).strategyDetailsWithDefensiveGenome!![0].assetsList.size
                    maximumTransactions = case
                    assignedGroupTransactions = group + 1
                }
            }
        }

        lateinit var lowestRisk: SimulationOutcome
        var assignedGroupRisk = 0
        var minL = 100.0
        for (group in output.indices) {
            for (case in output[group]) {
                if ((case.genome as OffensiveGenome).riskWithDefensiveGenome!! < minL) {
                    minL = (case.genome as OffensiveGenome).riskWithDefensiveGenome!!
                    lowestRisk = case
                    assignedGroupRisk = group + 1
                }
            }
        }

        saveCsvOfAssets("maxprofits_group_$assignedGroupProfits", maximumProfits)
        saveCsvOfAssets("maxtransactions_group_$assignedGroupTransactions", maximumTransactions)
        saveCsvOfAssets("lowestrisk_group_$assignedGroupRisk", lowestRisk)

        return output
    }

    private fun saveCsv(outcomes: List<List<OffensiveGenome>>) {
        for(type in outcomes.withIndex()) {
            val logFile = File("results/30_years_${labels[type.index]}.csv")
            val writer = logFile.writer()
            writer.append("Id,Profits,Risk" + System.lineSeparator())
            for(outcome in type.value.withIndex()) {
                writer.append("${outcome.index},${outcome.value.profitsWithDefensiveGenome!!},${outcome.value.riskWithDefensiveGenome!!}" + System.lineSeparator())
            }
            writer.close()
        }
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
        var genericMoeaDState = GenericGeneticAlgorithmState.load(genericMoeaDName)
        if (genericMoeaDState == null) {
            genericMoeaDState = genericMoeaDAlgorithm.getEmptyState()
        }
        return listOf(
            genericGeneticAlgorithmStateHv,
            genericGeneticAlgorithmStateNsga,
            genericGeneticAlgorithmStateSpea,
            genericGeneticAlgorithmStateNtga,
            genericMoeaDState,
        )
    }

    private fun loadMiddleStates(): List<List<GeneticAlgorithmState>> {
        val output = mutableListOf<List<GeneticAlgorithmState>>()
        for(i in 0 until RUNS) {
            val methodOutput = mutableListOf<GeneticAlgorithmState>()
            for(j in labels.indices) {
                val state = GenericGeneticAlgorithmState.load("${labels[j]} $i")!!
                methodOutput += state
            }
            output += methodOutput
        }
        return output
    }

    private fun saveCsvOfAssets(filename: String, simulationOutcome: SimulationOutcome) {
        val logFile = File("results/$filename.csv")
        val writer = logFile.writer()
        writer.append("Date,Cash,Developed,Emerging,Commodities,Gold,CAPE" + System.lineSeparator())
        val strategyDetails = (simulationOutcome.genome as OffensiveGenome).strategyDetailsWithDefensiveGenome!![0]
        var date = Date(1, 1, 1988)
        val developed = DataLoader.loadDevelopedData()
        val emerging = DataLoader.loadEmergingData()
        val commodity = DataLoader.loadCommodityData()
        val gold = DataLoader.loadGoldUsdData()
        val cape = DataLoader.loadShillerPESP500Ratio()
        do {
            val balance = strategyDetails.cashStatus.first { it.first == date }
            val assetsUntilNow =
                strategyDetails.assetsList.filter {
                    it.first.purchaseDate <= date && (it.first.saleDate == null || it.first.saleDate!! > date)
                }
            val developedValue = assetsUntilNow.filter { it.first.type == Portfolio.Asset.Type.DEVELOPED }
                .sumOf { it.first.getCurrentValue(developed[date]!!) }
            val emergingValue = assetsUntilNow.filter { it.first.type == Portfolio.Asset.Type.EMERGING }
                .sumOf { it.first.getCurrentValue(emerging[date]!!) }
            val commoditiesValue = assetsUntilNow.filter { it.first.type == Portfolio.Asset.Type.CRB }
                .sumOf { it.first.getCurrentValue(commodity[date]!!) }
            val goldValue = assetsUntilNow.filter { it.first.type == Portfolio.Asset.Type.GOLD }
                .sumOf { it.first.getCurrentValue(gold[date]!!) }
            val cash = balance.second - developedValue - emergingValue - commoditiesValue - goldValue
            val capeValue = cape[date]!!
            writer.append("$date,$cash,$developedValue,$emergingValue,$commoditiesValue,$goldValue,$capeValue" + System.lineSeparator())
            date = date.getDatePlusMonths(1)
        } while (date != Date(1, 1, 2018))
        writer.close()
    }

    companion object {
        const val logString = "Simulation for configurations - "
    }
}
