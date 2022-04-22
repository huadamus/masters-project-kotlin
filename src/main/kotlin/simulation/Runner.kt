package simulation

import metaheuristic.GeneticAlgorithm
import metaheuristic.GeneticAlgorithmState

object Runner {

    fun runCombining(
        geneticAlgorithm: GeneticAlgorithm,
        initialState: GeneticAlgorithmState,
        runs: Int
    ): GeneticAlgorithmState {
        var output = initialState
        for (run in 0 until runs) {
            output = output.combineArchiveWith(geneticAlgorithm.execute(run, geneticAlgorithm.getEmptyState()))
        }
        return output
    }
}
