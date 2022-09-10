@file:Suppress("FunctionName", "unused")

import data.CROSS_VALIDATION_DATASET_72_72
import devtools.getTrainingAndTestPeriods
import model.Date
import data.CROSS_VALIDATION_DATASET_DAILY_LONG
import data.DataLoader
import experiment.*
import java.io.File
import kotlin.system.measureTimeMillis

//metaheuristic
const val POPULATION_SIZE = 100
const val MOEA_D_VECTORS_COUNT = 100
const val GENERATIONS = 5000
var CROSSOVER_CHANCE = 0.80
var MUTATION_CHANCE = 0.09
var TOURNAMENT_PICKS = 16
var SPEA2_NEAREST_DISTANCE = 8
var NTGA2_GS_GENERATIONS = 2
var MOEA_D_T = 5
var GAUSS_MUTATION = true
const val ELITISM = 0

val eaHvConfigurationParameters = listOf(0.79, 0.087, 15)
val nsgaIIConfigurationParameters = listOf(0.76, 0.087, 18)
val spea2ConfigurationParameters = listOf(0.77, 0.069, 17)
val ntga2ConfigurationParameters = listOf(0.73, 0.087, 18)
val moeaDConfigurationParameters = listOf(0.83, 0.111)

//simulation
val CROSS_VALIDATION_DATASETS = Pair(CROSS_VALIDATION_DATASET_72_72, CROSS_VALIDATION_DATASET_DAILY_LONG)
const val TESTING_PERIODS = 1
const val RUNS = 3
const val DAILY = true

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
        //runParametrizationExperiment()
        //log("gauss")
        //runGaussMutationExperiment()
        //log("conf")
        //runConfigurationsExperiment()
        //runConfigurationsTestExperiment()
        runCrossValidationExperiment()
        //measureMaxSp500Falls()
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
    val configurationExperiment = ConfigurationTestExperiment()
    configurationExperiment.run()
    configurationExperiment.showResults()
}

private fun runCrossValidationExperiment() {
    val dataset = if(DAILY) {
        CROSS_VALIDATION_DATASETS.second
    } else {
        CROSS_VALIDATION_DATASETS.first
    }
    val crossValidationExperiment = CrossValidationExperiment(dataset)
    crossValidationExperiment.run()
    crossValidationExperiment.showResults()
}

private fun measureMaxSp500Falls() {
    val sp500Data = DataLoader.loadStockMarketData("data/sp500.csv")
        .filter { it.key >= Date(1, 6, 2019) && it.key < Date(1, 1, 2021) }

    var maxFall = 0.0
    var currentMonth = 1

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

    println(maxFall)
}
