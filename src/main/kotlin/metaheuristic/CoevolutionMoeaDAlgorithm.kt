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
import model.Genome
import model.OffensiveGenome
import simulation.GenomeGenerator
import simulation.SimulationOutcome
import simulation.Simulator
import java.util.*

class CoevolutionMoeaDAlgorithm(
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
        return execute(runId, geneticAlgorithmState as CoevolutionGeneticAlgorithmState)
    }

    private fun execute(runId: Int, state: CoevolutionGeneticAlgorithmState): CoevolutionGeneticAlgorithmState {
        var offensiveGenomesPopulation = state.offensiveGenomesPopulation.toList()
        var defensiveGenomesPopulation = state.defensiveGenomesPopulation.toList()
        var archive = state.archive.toMutableSet()

        var bestLastDefensiveGenomes: MutableList<Genome>? = null

        for (i in 0 until GENERATIONS) {
            println("$logString, Run ${runId + 1}, Generation ${i + 1}/$GENERATIONS, archive size: ${archive.size}")
            val newPopulation = mutableListOf<OffensiveGenome>()
            val newPopulation2 = mutableListOf<OffensiveGenome>()
            for (j in offensiveGenomesPopulation.indices) {
                newPopulation += getCombinedRandomGenome(
                    offensiveGenomesPopulation[j],
                    getVectorNeighborhood(j).map { defensiveGenomesPopulation[it] })
                newPopulation2 += getCombinedRandomGenome(
                    offensiveGenomesPopulation[j],
                    getVectorNeighborhood(j).map { defensiveGenomesPopulation[it] })
            }
            val newGeneration1 = calculatePopulationFitness(newPopulation)
            val newGeneration2 = calculatePopulationFitness(newPopulation2)
            if (bestLastDefensiveGenomes == null) {
                bestLastDefensiveGenomes = mutableListOf()
                for (j in offensiveGenomesPopulation.indices) {
                    val newGeneration1VectorScore =
                        newGeneration1[j].profitsWithDefensiveGenome!! * MOEA_D_VECTORS[j].first -
                                newGeneration1[j].riskWithDefensiveGenome!! * MOEA_D_VECTORS[j].second
                    val newGeneration2VectorScore =
                        newGeneration2[j].profitsWithDefensiveGenome!! * MOEA_D_VECTORS[j].first -
                                newGeneration2[j].riskWithDefensiveGenome!! * MOEA_D_VECTORS[j].second
                    bestLastDefensiveGenomes += if (newGeneration1VectorScore > newGeneration2VectorScore) {
                        newGeneration1[j].bestDefensiveGenome!!.clone()
                    } else {
                        newGeneration2[j].bestDefensiveGenome!!.clone()
                    }
                }
            }
            val combinedOffensiveAndBestGenomes = mutableListOf<OffensiveGenome>()
            for (j in offensiveGenomesPopulation.indices) {
                val output = offensiveGenomesPopulation[j].clone()
                output.bestDefensiveGenome = bestLastDefensiveGenomes[j]
                combinedOffensiveAndBestGenomes += output
            }
            val generationWithBestOutcomes = calculatePopulationFitness(combinedOffensiveAndBestGenomes)

            val assignedOffensivePopulation = offensiveGenomesPopulation.map { it.clone() }
            for (offensiveGenome in assignedOffensivePopulation) {
                val randomOutcomeSolution = newGeneration1.first { it.isSame(offensiveGenome) }
                val randomOutcomeSolution2 = newGeneration2.first { it.isSame(offensiveGenome) }
                val bestOutcomeSolution = generationWithBestOutcomes.first { it.isSame(offensiveGenome) }
                val index = assignedOffensivePopulation.indexOf(offensiveGenome)

                val oldScore = if (offensiveGenome.bestDefensiveGenome != null) {
                    offensiveGenome.profitsWithDefensiveGenome!! * MOEA_D_VECTORS[index].first -
                            offensiveGenome.riskWithDefensiveGenome!! * MOEA_D_VECTORS[index].second
                } else -Double.MAX_VALUE
                val random1Score = randomOutcomeSolution.profitsWithDefensiveGenome!! * MOEA_D_VECTORS[index].first -
                        randomOutcomeSolution.riskWithDefensiveGenome!! * MOEA_D_VECTORS[index].second
                val random2Score = randomOutcomeSolution2.profitsWithDefensiveGenome!! * MOEA_D_VECTORS[index].first -
                        randomOutcomeSolution2.riskWithDefensiveGenome!! * MOEA_D_VECTORS[index].second
                val bestScore = bestOutcomeSolution.profitsWithDefensiveGenome!! * MOEA_D_VECTORS[index].first -
                        bestOutcomeSolution.riskWithDefensiveGenome!! * MOEA_D_VECTORS[index].second
                if (random1Score > oldScore) {
                    offensiveGenome.bestDefensiveGenome = randomOutcomeSolution.bestDefensiveGenome!!.clone()
                    offensiveGenome.profitsWithDefensiveGenome =
                        randomOutcomeSolution.profitsWithDefensiveGenome
                    offensiveGenome.riskWithDefensiveGenome = randomOutcomeSolution.riskWithDefensiveGenome
                    offensiveGenome.strategyDetailsWithDefensiveGenome =
                        randomOutcomeSolution.strategyDetailsWithDefensiveGenome
                }
                if (random2Score > random1Score) {
                    offensiveGenome.bestDefensiveGenome = randomOutcomeSolution2.bestDefensiveGenome!!.clone()
                    offensiveGenome.profitsWithDefensiveGenome =
                        randomOutcomeSolution2.profitsWithDefensiveGenome
                    offensiveGenome.riskWithDefensiveGenome = randomOutcomeSolution2.riskWithDefensiveGenome
                    offensiveGenome.strategyDetailsWithDefensiveGenome =
                        randomOutcomeSolution2.strategyDetailsWithDefensiveGenome
                }
                if (bestScore > random2Score && bestScore > random1Score) {
                    offensiveGenome.bestDefensiveGenome = bestOutcomeSolution.bestDefensiveGenome!!.clone()
                    offensiveGenome.profitsWithDefensiveGenome = bestOutcomeSolution.profitsWithDefensiveGenome
                    offensiveGenome.riskWithDefensiveGenome = bestOutcomeSolution.riskWithDefensiveGenome
                    offensiveGenome.strategyDetailsWithDefensiveGenome =
                        bestOutcomeSolution.strategyDetailsWithDefensiveGenome
                }
            }
            val defensiveOutcomes = assignedOffensivePopulation.map {
                val simulationOutcome =
                    SimulationOutcome(it.profitsWithDefensiveGenome!!, it.riskWithDefensiveGenome!!)
                simulationOutcome.genome = it.bestDefensiveGenome!!.clone()
                simulationOutcome
            }
            bestLastDefensiveGenomes = defensiveOutcomes.map { it.genome }.toMutableList()

            archive += paretoEvaluateOffensiveGenomes(assignedOffensivePopulation).toList()
            archive = paretoEvaluateOffensiveGenomes(archive.toList()).toMutableSet()

            offensiveGenomesPopulation = calculateVectorsOutputs(assignedOffensivePopulation)
            defensiveGenomesPopulation = getNewGenerationDefensiveGenomes(defensiveOutcomes).toMutableList()
        }
        return CoevolutionGeneticAlgorithmState(offensiveGenomesPopulation, defensiveGenomesPopulation, archive)
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

    private fun getCombinedRandomGenome(
        offensiveGenome: OffensiveGenome,
        potentialDefensiveGenomes: List<Genome>,
    ): OffensiveGenome {
        val output = offensiveGenome.clone()
        output.bestDefensiveGenome = potentialDefensiveGenomes.random().clone()
        return output
    }

    override fun getEmptyState(): CoevolutionGeneticAlgorithmState {
        return CoevolutionGeneticAlgorithmState(
            initializeMoeaDGenomes(periodLengthInMonths),
            initializeGenomes(periodLengthInMonths),
            mutableSetOf(),
        )
    }
}
