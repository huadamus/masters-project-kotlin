package metaheuristic

interface GeneticAlgorithmState {
    fun combineArchiveWith(geneticAlgorithmState: GeneticAlgorithmState): GeneticAlgorithmState
}
