package simulation

import SAVE_MIDPOINT
import metaheuristic.GenericGeneticAlgorithmState
import metaheuristic.GeneticAlgorithm
import metaheuristic.GeneticAlgorithmState

object Runner {

    fun runCombining(
        experiment: String,
        geneticAlgorithm: GeneticAlgorithm,
        initialState: GeneticAlgorithmState,
        runs: Int
    ): GeneticAlgorithmState {
        var output = initialState
        for (run in 0 until runs) {
            val outcome = geneticAlgorithm.execute(run, geneticAlgorithm.getEmptyState())
            if(SAVE_MIDPOINT) {
                (outcome as GenericGeneticAlgorithmState).save("$experiment $run")
            }
            output = output.combineArchiveWith(outcome)
        }
        return output
    }
}
