package experiment

import RUNS
import TESTING_PERIODS
import data.DataLoader
import log
import metaheuristic.*
import model.Date
import model.OffensiveGenome
import model.StrategyDetails
import output.CrossValidation18to21ChartDrawer
import output.CrossValidationChartDrawer
import output.OutputPrintingManager
import simulation.*

class CrossValidationExperiment(private val dataset: Triple<Int, Int, List<Pair<Date, Date>>>) :
    Experiment("cross_validation") {

    private val finalTestPeriods = mutableListOf<List<Pair<Date, Date>>>()
    private val geneticAlgorithms = mutableListOf<GeneticAlgorithm>()
    private val validationOutcomes = mutableListOf<List<SimulationOutcome>>()

    override fun run() {
        geneticAlgorithms.clear()
        for (i in 0 until dataset.third.size / dataset.second) {
            val testPeriods = mutableListOf<Pair<Date, Date>>()
            for (j in 0 until dataset.second) {
                testPeriods += dataset.third[i * dataset.second + j]
            }
            val trainingPeriods = dataset.third - testPeriods.toSet()
            val name = "${name}_iteration_${i + 1}"
            val algorithm = GenericGeneticAlgorithm(
                name,
                "Simulation for cross-validation - iteration ${i + 1}",
                trainingPeriods,
                dataset.first,
                true,
                SelectionMethod.NTGA2,
                DataLoader.loadDevelopedData(),
                DataLoader.loadEmergingData(),
                DataLoader.loadCrbAndOilData(),
                DataLoader.loadGoldUsdData(),
                DataLoader.loadShillerPESP500Ratio(),
                DataLoader.loadDowToGoldData()
            )
            geneticAlgorithms += algorithm
            var state = loadState(i)
            state = Runner.runCombining(algorithm, state, RUNS) as GenericGeneticAlgorithmState
            state.save(name)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun calculateAndPrintOutcomes(): List<List<SimulationOutcome>> {
        log("=====CROSS-VALIDATION=====")
        finalTestPeriods.clear()
        val iterationOutcomes = mutableListOf<List<SimulationOutcome>>()
        val tested = mutableListOf<List<OffensiveGenome>>()
        for (i in 0 until dataset.third.size / dataset.second) {
            val state = loadState(i)
            val testPeriods = mutableListOf<Pair<Date, Date>>()
            for (j in 0 until dataset.second) {
                testPeriods += dataset.third[i * dataset.second + j]
            }
            finalTestPeriods.add(testPeriods)

            val tester = Tester(
                testPeriods,
                true,
                DataLoader.loadDevelopedData(),
                DataLoader.loadEmergingData(),
                DataLoader.loadCrbAndOilData(),
                DataLoader.loadGoldUsdData(),
                DataLoader.loadShillerPESP500Ratio(),
                DataLoader.loadDowToGoldData()
            )
            val buyAndHoldScore = Simulator.getScoreForBuyAndHold(
                Pair(
                    Date(testPeriods[0].first.day, testPeriods[0].first.month, testPeriods[0].first.year),
                    Date(
                        testPeriods[dataset.second - 1].second.day,
                        testPeriods[dataset.second - 1].second.month,
                        testPeriods[dataset.second - 1].second.year
                    )
                ),
                DataLoader.loadDevelopedData(),
                DataLoader.loadEmergingData(),
                DataLoader.loadCrbAndOilData(),
                DataLoader.loadGoldUsdData(),
                DataLoader.loadDowToGoldData(),
                DataLoader.loadShillerPESP500Ratio(),
            )
            val activeManagementScore = Simulator.getScoreForActiveManagement(
                Pair(
                    Date(testPeriods[0].first.day, testPeriods[0].first.month, testPeriods[0].first.year),
                    Date(
                        testPeriods[dataset.second - 1].second.day,
                        testPeriods[dataset.second - 1].second.month,
                        testPeriods[dataset.second - 1].second.year
                    )
                ),
                DataLoader.loadDevelopedData(),
                DataLoader.loadEmergingData(),
                DataLoader.loadCrbAndOilData(),
                DataLoader.loadGoldUsdData(),
                DataLoader.loadDowToGoldData(),
                DataLoader.loadShillerPESP500Ratio(),
            )

            val testedIteration = tester.test(state.archive.toList())
            tested += testedIteration
            iterationOutcomes.add(testedIteration.map {
                val out = SimulationOutcome(
                    it.profitsWithDefensiveGenome!!,
                    it.riskWithDefensiveGenome!!
                )
                out.genome = it
                out
            })
            iterationOutcomes.add(listOf(SimulationOutcome(buyAndHoldScore.first, buyAndHoldScore.second)))
            iterationOutcomes.add(
                listOf(
                    SimulationOutcome(
                        activeManagementScore.first,
                        activeManagementScore.second
                    )
                )
            )
        }

        for (i in 0 until iterationOutcomes.size / 3) {
            log("")
            log("${finalTestPeriods[i]} genomes:")
            log(
                OutputPrintingManager.getReadableParameters(iterationOutcomes[i * 3])
            )
            log(
                "${finalTestPeriods[i]} scores: ${System.lineSeparator()}${
                    OutputPrintingManager.getReadableScore(iterationOutcomes[i * 3])
                }"
            )
            printParametersTable(
                iterationOutcomes[0] + iterationOutcomes[3] + iterationOutcomes[6] + iterationOutcomes[9],// + iterationOutcomes[12],
                iterationOutcomes[i * 3]
            )
            log("${finalTestPeriods[i]} buy-and-hold score: ${iterationOutcomes[i * 3 + 1][0]}")
            log("${finalTestPeriods[i]} active management score: ${iterationOutcomes[i * 3 + 2][0]}")
        }

        showValidationOutcome(tested)

        return iterationOutcomes
    }

    private fun printParametersTable(
        allIndividuals: List<SimulationOutcome>,
        individuals: List<SimulationOutcome>
    ) {
        val mappedIndividuals = individuals.map { Pair(it.profits, it.risk) }
        log(
            "Purity: ${
                calculateParetoPurity(
                    individuals,
                    paretoEvaluate(allIndividuals)
                )
            }, PFS: ${individuals.size}, GD: ${invertedGenerationalDistanceForSet(mappedIndividuals)}, HV: ${
                hvParetoFitnessFunctionForSet(
                    mappedIndividuals
                )
            }, Spacing: ${spacingForSet(mappedIndividuals)}"
        )
    }

    private fun showValidationOutcome(archive: List<List<OffensiveGenome>>) {
        log("=====18 TO 21=====")
        val iterationOutcomes = mutableListOf<List<SimulationOutcome>>()

        val buyAndHoldScore = Simulator.getScoreForBuyAndHold(
            Pair(Date(1, 1, 2018), Date(1, 1, 2021)),
            DataLoader.loadDevelopedData(),
            DataLoader.loadEmergingData(),
            DataLoader.loadCrbAndOilData(),
            DataLoader.loadGoldUsdData(),
            DataLoader.loadDowToGoldData(),
            DataLoader.loadShillerPESP500Ratio(),
        )
        val activeManagementScore = Simulator.getScoreForActiveManagement(
            Pair(Date(1, 1, 2018), Date(1, 1, 2021)),
            DataLoader.loadDevelopedData(),
            DataLoader.loadEmergingData(),
            DataLoader.loadCrbAndOilData(),
            DataLoader.loadGoldUsdData(),
            DataLoader.loadDowToGoldData(),
            DataLoader.loadShillerPESP500Ratio(),
        )

        iterationOutcomes.add(listOf(SimulationOutcome(buyAndHoldScore.first, buyAndHoldScore.second)))
        iterationOutcomes.add(
            listOf(
                SimulationOutcome(
                    activeManagementScore.first,
                    activeManagementScore.second
                )
            )
        )
        for (i in 0 until dataset.third.size / dataset.second) {
            val tester = Tester(
                if (TESTING_PERIODS == 1) {
                    listOf(Pair(Date(1, 1, 2018), Date(1, 1, 2021)))
                } else {
                    listOf(
                        Pair(Date(1, 1, 2018), Date(1, 7, 2019)),
                        Pair(Date(1, 7, 2019), Date(1, 1, 2021)),
                    )
                },
                true,
                DataLoader.loadDevelopedData(),
                DataLoader.loadEmergingData(),
                DataLoader.loadCrbAndOilData(),
                DataLoader.loadGoldUsdData(),
                DataLoader.loadShillerPESP500Ratio(),
                DataLoader.loadDowToGoldData()
            )
            iterationOutcomes += tester.test(archive[i].toList()).map { offensiveGenome ->
                val output = SimulationOutcome(
                    offensiveGenome.strategyDetailsWithDefensiveGenome!!.map { it.getTotalYearlyAverageProfit(18) }
                        .average(),
                    offensiveGenome.riskWithDefensiveGenome!!
                )
                output.genome = offensiveGenome
                output
            }
        }

        log("Buy-and-hold score: ${iterationOutcomes[0]}")
        log("Active management score: ${iterationOutcomes[1]}")
        for (i in 2 until 2 + dataset.third.size / dataset.second) {
            log("Iteration ${i - 1}: ${iterationOutcomes[i]}")
            log("Iteration ${i - 1} genomes:")
            log(OutputPrintingManager.getReadableParameters(iterationOutcomes[i]))
        }

        validationOutcomes.clear()
        validationOutcomes += iterationOutcomes
    }

    override fun drawChart(outcomes: List<List<SimulationOutcome>>) {
        CrossValidationChartDrawer(finalTestPeriods).drawChart(outcomes)
        CrossValidation18to21ChartDrawer().drawChart(validationOutcomes)
    }

    override fun saveChart(outcomes: List<List<SimulationOutcome>>) {
        CrossValidationChartDrawer(finalTestPeriods).saveChart("cross_validation", outcomes)
        CrossValidation18to21ChartDrawer().saveChart("cross_validation_validation_set", validationOutcomes)
    }

    private fun loadState(iteration: Int): GenericGeneticAlgorithmState {
        var coevolutionGeneticAlgorithmState =
            GenericGeneticAlgorithmState.load("${name}_iteration_${iteration + 1}")
        if (coevolutionGeneticAlgorithmState == null) {
            coevolutionGeneticAlgorithmState =
                geneticAlgorithms[iteration].getEmptyState() as GenericGeneticAlgorithmState
        }
        return coevolutionGeneticAlgorithmState
    }

    fun StrategyDetails.getTotalYearlyAverageProfit(months: Int): Double {
        if(developedInvested + emergingInvested + crbInvested + goldInvested == 0.0) {
            return 0.0
        }
        return ((developedFinal + emergingFinal + crbFinal + goldFinal) / (developedInvested + emergingInvested + crbInvested + goldInvested) - 1.0) * 100.0 * (12.0 / months.toDouble())
    }
}
