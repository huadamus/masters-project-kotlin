package metaheuristic

import GENERATIONS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import model.Date
import model.OffensiveGenome
import simulation.GenomeGenerator
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
            archive = paretoEvaluateOffensiveGenomes(archive.toList()).toMutableSet()
            val newPopulation = mutableListOf<OffensiveGenome>()
            population = when (selectionMethod) {
                SelectionMethod.HV_PARETO -> (newPopulation + getNewGenerationOffensiveGenomes(generation))
                    .toMutableList()
                    .shuffled()
                SelectionMethod.NSGA_II -> (newPopulation + getNewGenerationOffensiveGenomesByRankAndVolume(generation))
                    .toMutableList()
                    .shuffled()
                SelectionMethod.SPEA2 -> (newPopulation + getNewGenerationOffensiveGenomesByStrength(generation))
                    .toMutableList()
                    .shuffled()
                SelectionMethod.NTGA2 -> (newPopulation + getNewGenerationOffensiveGenomesByNtgaMethod(
                    i,
                    generation,
                    archive
                )).toMutableList()
                    .shuffled()
            }
        }
        return GenericGeneticAlgorithmState(population, archive)
    }

    @Suppress("UNCHECKED_CAST")
    override fun calculateFinalPopulationFitness(genomes: Any): List<OffensiveGenome> {
        return calculateFinalPopulationFitness(genomes as List<OffensiveGenome>)
    }

    private fun calculateFinalPopulationFitness(
        genomes: List<OffensiveGenome>,
    ): List<OffensiveGenome> {
        return calculatePopulationFitness(genomes)
    }

    private fun calculatePopulationFitness(
        genomes: List<OffensiveGenome>,
    ): List<OffensiveGenome> {
        lateinit var genomesWithScores: List<OffensiveGenome>
        runBlocking {
            genomesWithScores = calculateFitness(genomes)
        }
        return genomesWithScores
    }

    private suspend fun calculateFitness(
        genomes: List<OffensiveGenome>,
    ): List<OffensiveGenome> {
        val output = Collections.synchronizedList(mutableListOf<OffensiveGenome>())
        withContext(Dispatchers.Default) {
            for (i in genomes.indices) {
                launch {
                    val score = Simulator.getScoreForOffensiveGenome(
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
                    output += score
                }
            }
        }
        return output
    }

    override fun getEmptyState(): GenericGeneticAlgorithmState {
        val genomes = initializeOffensiveGenomes(periodLengthInMonths)
        for (genome in genomes) {
            genome.bestDefensiveGenome = GenomeGenerator.generateDefensiveGenome(periodLengthInMonths)
        }
        return GenericGeneticAlgorithmState(genomes, mutableSetOf())
    }
}
