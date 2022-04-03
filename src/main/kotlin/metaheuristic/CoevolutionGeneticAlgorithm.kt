package metaheuristic

import ELITISM
import GENERATIONS
import model.Genome
import model.OffensiveGenome
import simulation.CombinedSimulationOutcome
import simulation.SimulationOutcome
import simulation.SingularSimulationOutcome
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
                    .maxByOrNull { it.getHvValue() }!!.defensiveGenome.clone()
            }
            val matchWithBestOutcomes = calculateFinalPopulationFitness(
                getCombinedBestGenomes(offensiveGenomesPopulation, bestLastDefensiveGenome)
            )

            val assignedOffensivePopulation = offensiveGenomesPopulation.map { it.clone() }
            when (selectionMethod) {
                SelectionMethod.HV_PARETO -> for (offensiveGenome in assignedOffensivePopulation) {
                    val randomOutcomeSolution = matchWithRandomOutcomes.first { it.offensiveGenome == offensiveGenome }
                    val randomOutcomeSolution2 =
                        matchWithRandomOutcomes2.first { it.offensiveGenome == offensiveGenome }
                    val bestOutcomeSolution = matchWithBestOutcomes.first { it.offensiveGenome == offensiveGenome }
                    if (randomOutcomeSolution.getHvValue() > offensiveGenome.getHvValue()) {
                        offensiveGenome.bestDefensiveGenome = randomOutcomeSolution.defensiveGenome.clone()
                        offensiveGenome.profitsWithDefensiveGenome = randomOutcomeSolution.profits
                        offensiveGenome.riskWithDefensiveGenome = randomOutcomeSolution.risk
                        offensiveGenome.strategyDetailsWithDefensiveGenome = randomOutcomeSolution.strategyDetails
                    }
                    if (randomOutcomeSolution2.getHvValue() > offensiveGenome.getHvValue()) {
                        offensiveGenome.bestDefensiveGenome = randomOutcomeSolution2.defensiveGenome.clone()
                        offensiveGenome.profitsWithDefensiveGenome = randomOutcomeSolution2.profits
                        offensiveGenome.riskWithDefensiveGenome = randomOutcomeSolution2.risk
                        offensiveGenome.strategyDetailsWithDefensiveGenome = randomOutcomeSolution2.strategyDetails
                    }
                    if (bestOutcomeSolution.getHvValue() > offensiveGenome.getHvValue()) {
                        offensiveGenome.bestDefensiveGenome = bestOutcomeSolution.defensiveGenome.clone()
                        offensiveGenome.profitsWithDefensiveGenome = bestOutcomeSolution.profits
                        offensiveGenome.riskWithDefensiveGenome = bestOutcomeSolution.risk
                        offensiveGenome.strategyDetailsWithDefensiveGenome = bestOutcomeSolution.strategyDetails
                    }
                }
                SelectionMethod.NSGA_II -> for (offensiveGenome in assignedOffensivePopulation) {
                    val randomOutcomeSolution = matchWithRandomOutcomes.first { it.offensiveGenome == offensiveGenome }
                    val randomOutcomeSolution2 =
                        matchWithRandomOutcomes2.first { it.offensiveGenome == offensiveGenome }
                    val bestOutcomeSolution = matchWithBestOutcomes.first { it.offensiveGenome == offensiveGenome }
                    val bestSolutions = paretoEvaluate(
                        listOf(
                            randomOutcomeSolution,
                            randomOutcomeSolution2,
                            bestOutcomeSolution,
                        )
                    ).toMutableList() as MutableList<CombinedSimulationOutcome>
                    if (offensiveGenome.profitsWithDefensiveGenome != null) {
                        bestSolutions += CombinedSimulationOutcome(
                            offensiveGenome,
                            offensiveGenome.bestDefensiveGenome!!,
                            offensiveGenome.profitsWithDefensiveGenome!!,
                            offensiveGenome.riskWithDefensiveGenome!!,
                            offensiveGenome.strategyDetailsWithDefensiveGenome!!
                        )
                    }
                    val randomBest = bestSolutions.random()
                    offensiveGenome.profitsWithDefensiveGenome = randomBest.profits
                    offensiveGenome.riskWithDefensiveGenome = randomBest.risk
                    offensiveGenome.bestDefensiveGenome = randomBest.defensiveGenome
                    offensiveGenome.strategyDetailsWithDefensiveGenome = randomBest.strategyDetails
                }
            }

            val defensiveOutcomes = assignedOffensivePopulation.map {
                val singularSimulationOutcome =
                    SingularSimulationOutcome(it.profitsWithDefensiveGenome!!, it.riskWithDefensiveGenome!!)
                singularSimulationOutcome.genome = it.bestDefensiveGenome!!.clone()
                singularSimulationOutcome
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

    override fun calculateFinalPopulationFitness(genomes: Any): List<SimulationOutcome> {
        @Suppress("UNCHECKED_CAST")
        return calculateFinalPopulationFitness(genomes as List<Pair<OffensiveGenome, Genome>>)
    }

    private fun calculateFinalPopulationFitness(
        combinedGenomes: List<Pair<OffensiveGenome, Genome>>,
    ): List<CombinedSimulationOutcome> {
        lateinit var genomesWithScores: List<CombinedSimulationOutcome>
        runBlocking {
            genomesWithScores = calculateFitness(combinedGenomes)
        }
        return genomesWithScores
    }

    private suspend fun calculateFitness(
        combinedGenomes: List<Pair<OffensiveGenome, Genome>>,
    ): List<CombinedSimulationOutcome> {
        val output = Collections.synchronizedList(mutableListOf<CombinedSimulationOutcome>())
        withContext(Dispatchers.Default) {
            for (i in combinedGenomes.indices) {
                launch {
                    output += Simulator.getCombinedScoreForDoubleGenome(
                        combinedGenomes[i].first,
                        combinedGenomes[i].second,
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
    ): List<Pair<OffensiveGenome, Genome>> {
        val shuffledDefensiveGenomes = defensiveGenomes.shuffled().toMutableList()
        return offensiveGenomes.map {
            Pair(it.clone(), shuffledDefensiveGenomes.removeFirst().clone())
        }
    }

    private fun getCombinedBestGenomes(
        offensiveGenomes: List<OffensiveGenome>,
        bestDefensiveGenome: Genome,
    ) = offensiveGenomes.map { Pair(it.clone(), bestDefensiveGenome.clone()) }

    override fun getEmptyState(): CoevolutionGeneticAlgorithmState {
        return CoevolutionGeneticAlgorithmState(
            initializeOffensiveGenomes(periodLengthInMonths),
            initializeGenomes(periodLengthInMonths),
            mutableSetOf(),
        )
    }
}
