@file:Suppress("FunctionName", "unused")

import devtools.getTrainingAndTestPeriods
import experiment.CrossValidationExperiment
import experiment.FourTypesExperiment
import model.Date
import data.CROSS_VALIDATION_DATASET_72_72
import experiment.ParametrizationExperiment

//metaheuristic
const val POPULATION_SIZE = 100
const val GENERATIONS = 7500
const val CROSSOVER_CHANCE = 0.85
const val MUTATION_CHANCE = 0.1
const val TOURNAMENT_PICKS = 19
const val ELITISM = 0

//simulation
val CROSS_VALIDATION_DATASET = CROSS_VALIDATION_DATASET_72_72
const val TESTING_PERIODS = 1
const val RUNS = 5

fun main() {
    //runParametrizationExperiment()
    //runFourTypesExperiment()
    runCrossValidationExperiment()
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

private fun runFourTypesExperiment() {
    val fourTypesExperiment = FourTypesExperiment()
    //fourTypesExperiment.run()
    fourTypesExperiment.showResults()
}

private fun runCrossValidationExperiment() {
    val crossValidationExperiment = CrossValidationExperiment(CROSS_VALIDATION_DATASET)
    //crossValidationExperiment.run()
    crossValidationExperiment.showResults()
}
