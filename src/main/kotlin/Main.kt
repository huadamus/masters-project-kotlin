@file:Suppress("FunctionName", "unused")

import devtools.getTrainingAndTestPeriods
import model.Date
import data.CROSS_VALIDATION_DATASET_72_72
import experiment.*

//metaheuristic
const val POPULATION_SIZE = 100
const val GENERATIONS = 5000
var CROSSOVER_CHANCE = 0.80
var MUTATION_CHANCE = 0.09
var TOURNAMENT_PICKS = 16
var SPEA2_NEAREST_DISTANCE = 8
var NTGA2_GS_GENERATIONS = 15
const val ELITISM = 0

//simulation
val CROSS_VALIDATION_DATASET = CROSS_VALIDATION_DATASET_72_72
const val TESTING_PERIODS = 1
const val RUNS = 5

fun main() {
    //runParametrizationExperiment()
    //runParametrizationSpea2Experiment()
    //runParametrizationNtga2Experiment()
    //runConfigurationsExperiment()
    //runCrossValidationExperiment()
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

private fun runParametrizationSpea2Experiment() {
    val parametrizationExperiment = ParametrizationExperimentSpea2()
    parametrizationExperiment.run()
    parametrizationExperiment.calculateAndPrintOutcomes()
}

private fun runParametrizationNtga2Experiment() {
    val parametrizationExperiment = ParametrizationExperimentNtga2()
    parametrizationExperiment.run()
    parametrizationExperiment.calculateAndPrintOutcomes()
}

private fun runConfigurationsExperiment() {
    val configurationExperiment = ConfigurationExperiment()
    //configurationExperiment.run()
    configurationExperiment.showResults()
}

private fun runCrossValidationExperiment() {
    val crossValidationExperiment = CrossValidationExperiment(CROSS_VALIDATION_DATASET)
    //crossValidationExperiment.run()
    crossValidationExperiment.showResults()
}
