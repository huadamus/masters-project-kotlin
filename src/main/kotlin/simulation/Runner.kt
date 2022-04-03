package simulation

import metaheuristic.GeneticAlgorithm
import metaheuristic.GeneticAlgorithmState

object Runner {

    fun runCombining(geneticAlgorithm: GeneticAlgorithm, runs: Int): GeneticAlgorithmState {
        var output: GeneticAlgorithmState? = null
        for (run in 0 until runs) {
            output = output?.combineArchiveWith(geneticAlgorithm.execute(run, geneticAlgorithm.getEmptyState()))
                ?: geneticAlgorithm.execute(run, geneticAlgorithm.getEmptyState())
        }
        return output!!
    }
}
