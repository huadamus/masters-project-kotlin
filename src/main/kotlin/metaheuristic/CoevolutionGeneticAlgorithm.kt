package metaheuristic

import ELITISM
import GENERATIONS
import model.Genome
import model.OffensiveGenome
import simulation.SimulationOutcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import model.Date
import simulation.Simulator
import java.util.*

class CoevolutionGeneticAlgorithm(
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
        return execute(runId, geneticAlgorithmState as CoevolutionGeneticAlgorithmState)
    }

    @Suppress("UNCHECKED_CAST")
    fun execute(runId: Int, state: CoevolutionGeneticAlgorithmState): CoevolutionGeneticAlgorithmState {
        var offensiveGenomesPopulation = state.offensiveGenomesPopulation.toList()
        var defensiveGenomesPopulation = state.defensiveGenomesPopulation.toList()
        var archive = state.archive.toMutableSet()

        var bestLastDefensiveGenome: Genome? = null

        for (i in 0 until GENERATIONS) {
            val matchWithRandomOutcomes = calculateFinalPopulationFitness(
                getCombinedRandomGenomes(offensiveGenomesPopulation, defensiveGenomesPopulation)
            )
            val matchWithRandomOutcomes2 = calculateFinalPopulationFitness(
                getCombinedRandomGenomes(offensiveGenomesPopulation, defensiveGenomesPopulation)
            )
            if (bestLastDefensiveGenome == null) {
                bestLastDefensiveGenome = (matchWithRandomOutcomes + matchWithRandomOutcomes2)
                    .maxByOrNull { it.getHvValue() }!!.bestDefensiveGenome!!.clone()
            }
            val matchWithBestOutcomes = calculateFinalPopulationFitness(
                getCombinedBestGenomes(offensiveGenomesPopulation, bestLastDefensiveGenome)
            )

            val assignedOffensivePopulation = offensiveGenomesPopulation.map { it.clone() }
            when (selectionMethod) {
                SelectionMethod.HV_PARETO -> for (offensiveGenome in assignedOffensivePopulation) {
                    val randomOutcomeSolution = matchWithRandomOutcomes.first { it.isSame(offensiveGenome) }
                    val randomOutcomeSolution2 =
                        matchWithRandomOutcomes2.first { it.isSame(offensiveGenome) }
                    val bestOutcomeSolution = matchWithBestOutcomes.first { it.isSame(offensiveGenome) }
                    if (randomOutcomeSolution.getHvValue() > offensiveGenome.getHvValue()) {
                        offensiveGenome.bestDefensiveGenome = randomOutcomeSolution.bestDefensiveGenome!!.clone()
                        offensiveGenome.profitsWithDefensiveGenome = randomOutcomeSolution.profitsWithDefensiveGenome
                        offensiveGenome.riskWithDefensiveGenome = randomOutcomeSolution.riskWithDefensiveGenome
                        offensiveGenome.strategyDetailsWithDefensiveGenome =
                            randomOutcomeSolution.strategyDetailsWithDefensiveGenome
                    }
                    if (randomOutcomeSolution2.getHvValue() > offensiveGenome.getHvValue()) {
                        offensiveGenome.bestDefensiveGenome = randomOutcomeSolution2.bestDefensiveGenome!!.clone()
                        offensiveGenome.profitsWithDefensiveGenome = randomOutcomeSolution2.profitsWithDefensiveGenome
                        offensiveGenome.riskWithDefensiveGenome = randomOutcomeSolution2.riskWithDefensiveGenome
                        offensiveGenome.strategyDetailsWithDefensiveGenome =
                            randomOutcomeSolution2.strategyDetailsWithDefensiveGenome
                    }
                    if (bestOutcomeSolution.getHvValue() > offensiveGenome.getHvValue()) {
                        offensiveGenome.bestDefensiveGenome = bestOutcomeSolution.bestDefensiveGenome!!.clone()
                        offensiveGenome.profitsWithDefensiveGenome = bestOutcomeSolution.profitsWithDefensiveGenome
                        offensiveGenome.riskWithDefensiveGenome = bestOutcomeSolution.riskWithDefensiveGenome
                        offensiveGenome.strategyDetailsWithDefensiveGenome =
                            bestOutcomeSolution.strategyDetailsWithDefensiveGenome
                    }
                }
                SelectionMethod.NSGA_II -> for (offensiveGenome in assignedOffensivePopulation) {
                    val randomOutcomeSolution = matchWithRandomOutcomes.first { it.isSame(offensiveGenome) }
                    val randomOutcomeSolution2 =
                        matchWithRandomOutcomes2.first { it.isSame(offensiveGenome) }
                    val bestOutcomeSolution = matchWithBestOutcomes.first { it.isSame(offensiveGenome) }
                    val bestSolutions = paretoEvaluateOffensiveGenomes(
                        listOf(
                            randomOutcomeSolution,
                            randomOutcomeSolution2,
                            bestOutcomeSolution,
                        )
                    ).toMutableList()
                    if (offensiveGenome.profitsWithDefensiveGenome != null) {
                        bestSolutions += offensiveGenome.clone()
                    }
                    val randomBest = bestSolutions.random()
                    offensiveGenome.profitsWithDefensiveGenome = randomBest.profitsWithDefensiveGenome
                    offensiveGenome.riskWithDefensiveGenome = randomBest.riskWithDefensiveGenome
                    offensiveGenome.bestDefensiveGenome = randomBest.bestDefensiveGenome!!.clone()
                    offensiveGenome.strategyDetailsWithDefensiveGenome = randomBest.strategyDetailsWithDefensiveGenome
                }
            }

            val defensiveOutcomes = assignedOffensivePopulation.map {
                val simulationOutcome =
                    SimulationOutcome(it.profitsWithDefensiveGenome!!, it.riskWithDefensiveGenome!!)
                simulationOutcome.genome = it.bestDefensiveGenome!!.clone()
                simulationOutcome
            }
            val bestDefensiveOutcome = defensiveOutcomes.maxByOrNull { it.getHvValue() }
            bestLastDefensiveGenome = bestDefensiveOutcome!!.genome.clone()

            println(
                "$logString, Run ${runId + 1}, Generation ${i + 1}/$GENERATIONS, archive size: ${
                    archive.size
                }"
            )

            archive += paretoEvaluateOffensiveGenomes(assignedOffensivePopulation).toList()
            archive = paretoEvaluateOffensiveGenomes(archive.toList()).toMutableSet()

            val newOffensivePopulation = mutableListOf<OffensiveGenome>()
            val newDefensivePopulation = mutableListOf<Genome>()
            newOffensivePopulation.addAll(assignedOffensivePopulation
                .sortedByDescending { it.getHvValue() }
                .take(ELITISM)
                .map { it.clone() })
            newDefensivePopulation.addAll(
                defensiveOutcomes.sortedByDescending { it.getHvValue() }
                    .take(ELITISM)
                    .map { it.genome.clone() })
            when (selectionMethod) {
                SelectionMethod.HV_PARETO -> {
                    offensiveGenomesPopulation = newOffensivePopulation +
                            getNewGenerationOffensiveGenomes(assignedOffensivePopulation).toMutableList()
                                .shuffled()
                    defensiveGenomesPopulation = newDefensivePopulation +
                            getNewGenerationDefensiveGenomes(defensiveOutcomes).toMutableList()
                                .shuffled()
                }
                SelectionMethod.NSGA_II -> {
                    offensiveGenomesPopulation = newOffensivePopulation +
                            getNewGenerationOffensiveGenomesByRankAndVolume(assignedOffensivePopulation).toMutableList()
                                .shuffled()
                    defensiveGenomesPopulation = newDefensivePopulation +
                            getNewGenerationDefensiveGenomesByRankAndVolume(defensiveOutcomes).toMutableList()
                                .shuffled()
                }
            }
        }
        return CoevolutionGeneticAlgorithmState(offensiveGenomesPopulation, defensiveGenomesPopulation, archive)
    }

    override fun calculateFinalPopulationFitness(genomes: Any): List<OffensiveGenome> {
        @Suppress("UNCHECKED_CAST")
        return calculateFinalPopulationFitness(genomes as List<OffensiveGenome>)
    }

    private fun calculateFinalPopulationFitness(
        combinedGenomes: List<OffensiveGenome>,
    ): List<OffensiveGenome> {
        lateinit var genomesWithScores: List<OffensiveGenome>
        runBlocking {
            genomesWithScores = calculateFitness(combinedGenomes)
        }
        return genomesWithScores
    }

    private suspend fun calculateFitness(
        combinedGenomes: List<OffensiveGenome>,
    ): List<OffensiveGenome> {
        val output = Collections.synchronizedList(mutableListOf<OffensiveGenome>())
        withContext(Dispatchers.Default) {
            for (i in combinedGenomes.indices) {
                launch {
                    output += Simulator.getScoreForOffensiveGenome(
                        combinedGenomes[i],
                        selling,
                        periods,
                        developedData,
                        emergingData,
                        crbData,
                        goldUsdData,
                        shillerPESP500Data,
                        dowToGoldData
                    )
                }
            }
        }
        return output
    }

    private fun getCombinedRandomGenomes(
        offensiveGenomes: List<OffensiveGenome>,
        defensiveGenomes: List<Genome>,
    ): List<OffensiveGenome> {
        val shuffledDefensiveGenomes = defensiveGenomes.shuffled().toMutableList()
        return offensiveGenomes.map {
            val out = it.clone()
            out.bestDefensiveGenome = shuffledDefensiveGenomes.removeFirst().clone()
            out
        }
    }

    private fun getCombinedBestGenomes(
        offensiveGenomes: List<OffensiveGenome>,
        bestDefensiveGenome: Genome,
    ) = offensiveGenomes.map {
        val out = it.clone()
        out.bestDefensiveGenome = bestDefensiveGenome.clone()
        out
    }

    override fun getEmptyState(): CoevolutionGeneticAlgorithmState {
        return CoevolutionGeneticAlgorithmState(
            initializeOffensiveGenomes(periodLengthInMonths),
            initializeGenomes(periodLengthInMonths),
            mutableSetOf(),
        )
    }
}
