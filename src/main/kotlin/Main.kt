@file:Suppress("FunctionName", "unused")

import devtools.getTrainingAndTestPeriods
import experiment.CrossValidationExperiment
import experiment.FourTypesExperiment
import model.Date
import data.CROSS_VALIDATION_DATASET_72_72

//metaheuristic
const val POPULATION_SIZE = 140
const val GENERATIONS = 3
const val CROSSOVER_CHANCE = 0.86
const val MUTATION_CHANCE = 0.085
const val ELITISM = 0
const val TOURNAMENT_PICKS = 22

//simulation
val CROSS_VALIDATION_DATASET = CROSS_VALIDATION_DATASET_72_72
const val TESTING_PERIODS = 1
const val RUNS = 1

fun main() {
    runFourTypesExperiment()
    //runCrossValidationExperiment()
}

private fun generatePeriods() {
    val periods = getTrainingAndTestPeriods(
        Date(1, 1, 1988), Date(1, 1, 2018), 18, 2)
    println(periods)
}

private fun runFourTypesExperiment() {
    val fourTypesExperiment = FourTypesExperiment()
    fourTypesExperiment.run()
    fourTypesExperiment.showResults()
}

private fun runCrossValidationExperiment() {
    val crossValidationExperiment = CrossValidationExperiment(CROSS_VALIDATION_DATASET)
    crossValidationExperiment.run()
    crossValidationExperiment.showResults()
}
