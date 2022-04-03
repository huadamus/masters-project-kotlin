package metaheuristic

import model.Date

abstract class GeneticAlgorithm(
    protected val experimentName: String,
    protected val logString: String,
    protected val periods: List<Pair<Date, Date>>,
    protected val periodLengthInMonths: Int,
    protected val selling: Boolean,
    protected val developedData: Map<Date, Double>,
    protected val emergingData: Map<Date, Double>,
    protected val crbData: Map<Date, Double>,
    protected val goldUsdData: Map<Date, Double>,
    protected val dowToGoldData: Map<Date, Double>
) {

    abstract fun execute(runId: Int, geneticAlgorithmState: GeneticAlgorithmState): GeneticAlgorithmState

    abstract fun calculateFinalPopulationFitness(genomes: Any): List<Any>

    abstract fun getEmptyState(): GeneticAlgorithmState
}
