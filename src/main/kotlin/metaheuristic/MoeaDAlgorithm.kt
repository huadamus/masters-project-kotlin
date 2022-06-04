package metaheuristic

import GENERATIONS
import MOEA_D_T
import MOEA_D_VECTORS
import MOEA_D_VECTORS_COUNT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import model.Date
import model.OffensiveGenome
import simulation.GenomeGenerator
import simulation.Simulator
import java.util.*

class MoeaDAlgorithm(
    experimentName: String,
    logString: String,
    periods: List<Pair<Date, Date>>,
    periodLengthInMonths: Int,
    selling: Boolean,
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

    private fun execute(runId: Int, state: GenericGeneticAlgorithmState): GeneticAlgorithmState {
        var population = state.population
        var archive = state.archive.toMutableSet()

        for (i in 0 until GENERATIONS) {
            val generation = calculatePopulationFitness(population)
            println("$logString, Run ${runId + 1}, Generation ${i + 1}/$GENERATIONS, archive size: ${archive.size}")
            val newPopulation = calculateVectorsOutputs(generation)
            population = newPopulation
            archive += population
            archive = paretoEvaluateOffensiveGenomes(archive.toList()).toMutableSet()
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
        val genomesWithIds = genomes.map { Pair(it.clone(), genomes.indexOf(it)) }
        runBlocking {
            genomesWithScores = calculateFitness(genomesWithIds)
        }
        return genomesWithScores
    }

    private suspend fun calculateFitness(
        genomesWithIds: List<Pair<OffensiveGenome, Int>>
    ): List<OffensiveGenome> {
        val output = Collections.synchronizedList(mutableListOf<Pair<OffensiveGenome, Int>>())
        withContext(Dispatchers.Default) {
            for (i in genomesWithIds.indices) {
                launch {
                    val score = Simulator.getScoreForOffensiveGenome(
                        genomesWithIds[i].first,
                        selling,
                        periods,
                        developedData,
                        emergingData,
                        crbData,
                        goldUsdData,
                        shillerPESP500Data,
                        dowToGoldData
                    )
                    output += Pair(score, genomesWithIds[i].second)
                }
            }
        }
        return output.sortedBy { it.second }.map { it.first }
    }

    private fun calculateVectorsOutputs(
        genomes: List<OffensiveGenome>,
    ): List<OffensiveGenome> {
        lateinit var vectorsOutputs: List<OffensiveGenome>
        runBlocking {
            vectorsOutputs = calculateVectors(genomes)
        }
        return vectorsOutputs
    }

    private suspend fun calculateVectors(generation: List<OffensiveGenome>): List<OffensiveGenome> {
        val output = Collections.synchronizedList(mutableListOf<Triple<OffensiveGenome, Double, Int>>())
        withContext(Dispatchers.Default) {
            for (vectorId in 0 until MOEA_D_VECTORS.size - 1) {
                launch {
                    val neighborhood = getVectorNeighborhood(vectorId)
                    val individual1 = generation[vectorId].clone()
                    val individual2Id = neighborhood.random()
                    val individual2 = generation[individual2Id].clone()
                    val child = individual1.modularCrossover(individual2).first
                    child.mutate()
                    val childWithFitness = calculateFitness(listOf(Pair(child, 0)))[0]
                    val newFitness =
                        childWithFitness.profitsWithDefensiveGenome!! * MOEA_D_VECTORS[vectorId].first -
                                childWithFitness.riskWithDefensiveGenome!! * MOEA_D_VECTORS[vectorId].second
                    for (i in neighborhood) {
                        val oldFitness =
                            generation[i].profitsWithDefensiveGenome!! * MOEA_D_VECTORS[i].first -
                                    generation[i].riskWithDefensiveGenome!! * MOEA_D_VECTORS[i].second
                        if (newFitness > oldFitness) {
                            output += Triple(childWithFitness.clone(), newFitness, vectorId)
                        } else {
                            if (i == vectorId) {
                                output += Triple(individual1.clone(), oldFitness, vectorId)
                            }
                        }
                    }
                }
            }
        }
        val finalOutput = mutableListOf<OffensiveGenome>()
        for (vectorId in 0 until MOEA_D_VECTORS.size - 1) {
            finalOutput += output.filter { it.third == vectorId }.sortedByDescending { it.second }.first().first
        }
        return finalOutput
    }

    private fun getVectorNeighborhood(vectorId: Int): List<Int> {
        val output = mutableListOf<Int>()
        for (i in vectorId - MOEA_D_T / 2 until vectorId + MOEA_D_T / 2 + 1) {
            if (i >= 0 && i < MOEA_D_VECTORS.size - 1) {
                output += i
            }
        }
        return output
    }

    private fun initializeMoeaDGenomes(periodMonths: Int): List<OffensiveGenome> {
        val output = mutableListOf<OffensiveGenome>()
        for (i in 0 until MOEA_D_VECTORS_COUNT) {
            output += GenomeGenerator.generateOffensiveGenome(periodMonths)
        }
        return output
    }

    override fun getEmptyState(): GenericGeneticAlgorithmState {
        val genomes = initializeMoeaDGenomes(periodLengthInMonths)
        for (genome in genomes) {
            genome.bestDefensiveGenome = GenomeGenerator.generateDefensiveGenome(periodLengthInMonths)
        }
        return GenericGeneticAlgorithmState(genomes, mutableSetOf())
    }
}
