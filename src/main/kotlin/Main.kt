@file:Suppress("FunctionName", "unused")

import data.*
import devtools.getTrainingAndTestPeriods
import model.Date
import experiment.*
import simulation.Simulator
import simulation.portfolio.Portfolio
import java.io.File
import kotlin.system.measureTimeMillis

//metaheuristic
const val POPULATION_SIZE = 100
const val MOEA_D_VECTORS_COUNT = 100
const val GENERATIONS = 500
var CROSSOVER_CHANCE = 0.80
var MUTATION_CHANCE = 0.11
var TOURNAMENT_PICKS = 16
var GAUSS_MUTATION = true
const val ELITISM = 0

const val DRAW_CHART = true
const val SAVE_MIDPOINT = false

val eaHvConfigurationParameters = listOf(0.77, 0.11, 11)
val nsgaIIConfigurationParameters = listOf(0.77, 0.08, 19)
val spea2ConfigurationParameters = listOf(0.87, 0.07, 11)
var SPEA2_NEAREST_DISTANCE = 7
val ntga2ConfigurationParameters = listOf(0.80, 0.11, 16)
var NTGA2_GS_GENERATIONS = 7
val moeaDConfigurationParameters = listOf(0.73, 0.11)
var MOEA_D_T = 12

//simulation
val CROSS_VALIDATION_DATASETS = Pair(CROSS_VALIDATION_DATASET_72_72, CROSS_VALIDATION_DATASET_DAILY_LONG)
const val TESTING_PERIODS = 1
const val RUNS = 1
const val DAILY = false

//technical
val logFile = File("results/log.txt")
var writer = logFile.writer()
var MOEA_D_VECTORS = mutableListOf<Pair<Double, Double>>().also {
    for (i in 0 until MOEA_D_VECTORS_COUNT + 1) {
        val currentValue = i.toDouble() / MOEA_D_VECTORS_COUNT.toDouble()
        it += Pair(currentValue, 1.0 - currentValue)
    }
}

fun log(log: String) {
    println(log)
    writer.append(log + System.lineSeparator())
}

fun main(args: Array<String>) {
    val time = measureTimeMillis {
        //DataConverter.convert()
        //runParametrizationExperiment()
        //log("gauss")
        //runGaussMutationExperiment()
        //log("conf")
        runConfigurationsExperiment()
        //runConfigurationsTestExperiment()
        //runCrossValidationExperiment()
        //measureMaxSp500ProfitsAndFalls()
    }
    log("Total time: ${time / 1000}s")
    writer.close()
}

private fun generatePeriods() {
    val periods = getTrainingAndTestPeriods(
        Date(1, 1, 1988), Date(1, 1, 2018), 18, 2
    )
    println(periods)
}

private fun runParametrizationExperiment() {
    val parametrizationExperiment = ParametrizationExperiment()
    parametrizationExperiment.run()
    parametrizationExperiment.calculateAndPrintOutcomes()
}

private fun runGaussMutationExperiment() {
    val gaussMutationExperiment = GaussMutationExperiment()
    gaussMutationExperiment.run()
    gaussMutationExperiment.calculateAndPrintOutcomes()
}

private fun runConfigurationsExperiment() {
    val configurationExperiment = ConfigurationExperiment()
    configurationExperiment.run()
    configurationExperiment.showResults()
}

private fun runConfigurationsTestExperiment() {
//    val configurationExperiment = ConfigurationTestExperiment()
//    configurationExperiment.run()
//    configurationExperiment.showResults()
}

private fun runCrossValidationExperiment() {
    val dataset = if (DAILY) {
        CROSS_VALIDATION_DATASETS.second
    } else {
        CROSS_VALIDATION_DATASETS.first
    }
    val crossValidationExperiment = CrossValidationExperiment(dataset)
    crossValidationExperiment.run()
    crossValidationExperiment.showResults()
}

private fun measureMaxSp500ProfitsAndFalls() {
    val sp500Data = DataLoader.loadStockMarketData("data/sp500.csv")
        .filter { it.key >= Date(1, 1, 2018) && it.key <= Date(1, 1, 2021) }

    var maxFall = 0.0
    var currentMonth = 0

    var lastPrice = 0.0
    var currentPrice = 0.0

    val finalPeriods = mutableListOf<Pair<Date, Double>>()

    for (data in sp500Data) {
        if (data.key.month != currentMonth) {
            finalPeriods += Pair(data.key, data.value)
            currentMonth = data.key.month
        }
    }

    for (data in finalPeriods) {
        if (lastPrice == 0.0) {
            lastPrice = data.second
            continue
        } else {
            lastPrice = currentPrice
            currentPrice = data.second
            if (currentPrice < lastPrice) {
                val fall = 1.0 - (currentPrice / lastPrice)
                println("${data.first}, $fall")
                if (fall > maxFall) {
                    maxFall = fall
                }
            }
        }
    }

    val finalRatio = 100 *
            ((finalPeriods.last().second * (Simulator.getInflation(
                finalPeriods.last().first,
                finalPeriods.first().first
            )) / finalPeriods.first().second) - 1.0) /
            (finalPeriods.first().first.getMonthsBetween(finalPeriods.last().first) / 12)

    println("initial value: ${finalPeriods.first()}")
    println("last value: ${finalPeriods.last()}")
    println("ratio: $finalRatio")
    println("max falls:$maxFall")
}
