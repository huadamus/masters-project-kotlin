@file:Suppress("FunctionName", "unused")

import devtools.getTrainingAndTestPeriods
import model.Date
import data.CROSS_VALIDATION_DATASET_72_72
import experiment.*
import java.io.File
import kotlin.system.measureTimeMillis

//metaheuristic
const val POPULATION_SIZE = 100
const val MOEA_D_VECTORS_COUNT = 100
const val GENERATIONS = 500
var CROSSOVER_CHANCE = 0.80
var MUTATION_CHANCE = 0.09
var TOURNAMENT_PICKS = 16
var SPEA2_NEAREST_DISTANCE = 8
var NTGA2_GS_GENERATIONS = 15
var MOEA_D_T = 8
const val ELITISM = 0
const val GAUSS_MUTATION = true

var MOEA_D_VECTORS = mutableListOf<Pair<Double, Double>>().also {
    for (i in 0 until MOEA_D_VECTORS_COUNT + 1) {
        val currentValue = i.toDouble() / MOEA_D_VECTORS_COUNT.toDouble()
        it += Pair(currentValue, 1.0 - currentValue)
    }
}

//simulation
val CROSS_VALIDATION_DATASET = CROSS_VALIDATION_DATASET_72_72
const val TESTING_PERIODS = 1
const val RUNS = 1

//technical
val logFile = File("results/log.txt")
var writer = logFile.writer()

fun log(log: String) {
    println(log)
    writer.append(log + System.lineSeparator())
}

fun main(args: Array<String>) {
    val time = measureTimeMillis {
        runParametrizationExperiment()
        //runConfigurationsExperiment()
        //runCrossValidationExperiment()
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

private fun runConfigurationsExperiment() {
    val configurationExperiment = ConfigurationExperiment()
    configurationExperiment.run()
    configurationExperiment.showResults()
}

private fun runCrossValidationExperiment() {
    val crossValidationExperiment = CrossValidationExperiment(CROSS_VALIDATION_DATASET)
    crossValidationExperiment.run()
    crossValidationExperiment.showResults()
}
