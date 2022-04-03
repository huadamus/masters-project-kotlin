package metaheuristic

import GENERATIONS
import model.CombinedGenome
import simulation.SimulationOutcome
import simulation.SingularSimulationOutcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import model.Date
import simulation.Simulator
import java.util.*

class GenericGeneticAlgorithm(
    experimentName: String,
    logString: String,
    periods: List<Pair<Date, Date>>,
    periodLengthInMonths: Int,
    selling: Boolean,
    private val selectionMethod: SelectionMethod,
    developedData: Map<Date, Double>,
    emergingData: Map<Date, Double>,
    crbData: Map<Date, Double>,
    goldUsdData: Map<Date, Double>,
    private val shillerPESP500Data: Map<Date, Double>,
    dowToGoldData: Map<Date, Double>,
) : GeneticAlgorithm(
    experimentName,
    logString,
    periods,
    periodLengthInMonths,
    selling,
    developedData,
    emergingData,
    crbData,
    goldUsdData,
    dowToGoldData
) {

    override fun execute(runId: Int, geneticAlgorithmState: GeneticAlgorithmState): GeneticAlgorithmState {
        return execute(runId, geneticAlgorithmState as GenericGeneticAlgorithmState)
    }

    @Suppress("UNCHECKED_CAST")
    fun execute(runId: Int, state: GenericGeneticAlgorithmState): GeneticAlgorithmState {
        var population = state.population
        var archive = state.archive.toMutableSet()

        for (i in 0 until GENERATIONS) {
            val generation = calculatePopulationFitness(population)
            println("$logString, Run ${runId + 1}, Generation ${i + 1}/$GENERATIONS, archive size: ${archive.size}")
            archive += generation
            archive = paretoEvaluate(archive.toList())
                .map { it as SingularSimulationOutcome }
                .toMutableSet()
            val newPopulation = mutableListOf<CombinedGenome>()
            population = when (selectionMethod) {
                SelectionMethod.HV_PARETO -> (newPopulation + getNewGenerationCombinedGenomes(generation))
                    .toMutableList()
                    .shuffled()
                SelectionMethod.NSGA_II -> (newPopulation + getNewGenerationCombinedGenomesByRankAndVolume(generation))
                    .toMutableList()
                    .shuffled()
            }
        }
        return GenericGeneticAlgorithmState(population, archive)
    }

    @Suppress("UNCHECKED_CAST")
    override fun calculateFinalPopulationFitness(genomes: Any): List<SimulationOutcome> {
        return calculateFinalPopulationFitness(genomes as List<CombinedGenome>)
    }

    private fun calculateFinalPopulationFitness(
        genomes: List<CombinedGenome>,
    ): List<SingularSimulationOutcome> {
        return calculatePopulationFitness(genomes)
    }

    private fun calculatePopulationFitness(
        genomes: List<CombinedGenome>,
    ): List<SingularSimulationOutcome> {
        lateinit var genomesWithScores: List<SingularSimulationOutcome>
        runBlocking {
            genomesWithScores = calculateFitness(genomes)
        }
        return genomesWithScores
    }

    private suspend fun calculateFitness(
        genomes: List<CombinedGenome>,
    ): List<SingularSimulationOutcome> {
        val output = Collections.synchronizedList(mutableListOf<SingularSimulationOutcome>())
        withContext(Dispatchers.Default) {
            for (i in genomes.indices) {
                launch {
                    val score = Simulator.getCombinedScoreForDoubleGenome(
                        genomes[i].offensiveGenome,
                        genomes[i],
                        selling,
                        periods,
                        developedData,
                        emergingData,
                        crbData,
                        goldUsdData,
                        shillerPESP500Data,
                        dowToGoldData
                    )
                    val outcome = SingularSimulationOutcome(score.profits, score.risk)
                    outcome.genome = genomes[i]
                    output += outcome
                }
            }
        }
        return output
    }

    override fun getEmptyState() =
        GenericGeneticAlgorithmState(initializeCombinedGenomes(periodLengthInMonths), mutableSetOf())
}
