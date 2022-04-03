@file:Suppress("UNCHECKED_CAST")

package metaheuristic

import simulation.GenomeGenerator
import ELITISM
import POPULATION_SIZE
import TOURNAMENT_PICKS
import model.Genome
import model.OffensiveGenome
import simulation.SimulationOutcome

fun initializeGenomes(periodMonths: Int): List<Genome> {
    val output = mutableListOf<Genome>()
    for (i in 0 until POPULATION_SIZE) {
        output += GenomeGenerator.generateDefensiveGenome(periodMonths)
    }
    return output
}

fun initializeOffensiveGenomes(periodMonths: Int): List<OffensiveGenome> {
    val output = mutableListOf<OffensiveGenome>()
    for (i in 0 until POPULATION_SIZE) {
        output += GenomeGenerator.generateOffensiveGenome(periodMonths)
    }
    return output
}

fun getNewGenerationDefensiveGenomes(outcomes: List<SimulationOutcome>): List<Genome> {
    val selectedGenomes = selectWithTournament(outcomes).shuffled()
    return getMutatedGenomes(getCrossoverDefensiveGenomes(selectedGenomes))
}

fun getNewGenerationOffensiveGenomes(outcomes: List<OffensiveGenome>): List<OffensiveGenome> {
    val selectedGenomes = selectOffensiveGenomeWithTournament(outcomes).shuffled()
    return getMutatedGenomes(getCrossoverOffensiveGenomes(selectedGenomes)) as List<OffensiveGenome>
}

fun getNewGenerationDefensiveGenomesByRankAndVolume(outcomes: List<SimulationOutcome>): List<Genome> {
    val rankedOutcomes = getRankedDefensiveOutcomes(outcomes)
    val rankedOutcomesWithVolume = getRankedDefensiveOutcomesVolume(rankedOutcomes)
    val selectedGenomes = selectWithTournamentByRankAndVolume(rankedOutcomesWithVolume)
    return getMutatedGenomes(getCrossoverDefensiveGenomes(selectedGenomes))
}

@Suppress("UNCHECKED_CAST")
fun getNewGenerationOffensiveGenomesByRankAndVolume(outcomes: List<OffensiveGenome>): List<OffensiveGenome> {
    val rankedOutcomes = getRankedOffensiveOutcomes(outcomes)
    val rankedOutcomesWithVolume = getRankedOffensiveOutcomesVolume(rankedOutcomes)
    val selectedGenomes = selectWithTournamentByRankAndVolume(rankedOutcomesWithVolume)
    return getMutatedGenomes(getCrossoverOffensiveGenomes(selectedGenomes)) as List<OffensiveGenome>
}

fun paretoEvaluateOffensiveGenomes(outcomes: List<OffensiveGenome>): List<OffensiveGenome> {
    val output = mutableSetOf<OffensiveGenome>()
    outer@ for (outcome in outcomes) {
        if (outcome.getHvValue() == -Double.MAX_VALUE) {
            continue@outer
        }
        for (comparedOutcome in outcomes) {
            if (outcome != comparedOutcome) {
                if ((comparedOutcome.profitsWithDefensiveGenome!! >= outcome.profitsWithDefensiveGenome!!
                            && comparedOutcome.riskWithDefensiveGenome!! < outcome.riskWithDefensiveGenome!!)
                    || (comparedOutcome.profitsWithDefensiveGenome!! > outcome.profitsWithDefensiveGenome!!
                            && comparedOutcome.riskWithDefensiveGenome!! <= outcome.riskWithDefensiveGenome!!)
                ) {
                    continue@outer
                }
            }
        }
        output.add(outcome.clone())
    }
    return output.sortedByDescending { it.profitsWithDefensiveGenome!! }
        .distinctBy { it.profitsWithDefensiveGenome!! }
        .sortedByDescending { it.riskWithDefensiveGenome!! }
        .distinctBy { it.riskWithDefensiveGenome!! }
}

fun paretoEvaluate(outcomes: List<SimulationOutcome>): List<SimulationOutcome> {
    val output = mutableSetOf<SimulationOutcome>()
    outer@ for (outcome in outcomes) {
        for (comparedOutcome in outcomes) {
            if (outcome != comparedOutcome) {
                if ((comparedOutcome.profits >= outcome.profits && comparedOutcome.risk < outcome.risk)
                    || (comparedOutcome.profits > outcome.profits && comparedOutcome.risk <= outcome.risk)
                ) {
                    continue@outer
                }
            }
        }
        output.add(outcome.clone())
    }
    return output.sortedByDescending { it.profits }
        .distinctBy { it.profits }
        .sortedByDescending { it.risk }
        .distinctBy { it.risk }
}

fun calculateParetoPurity(frontToCalculatePurity: List<SimulationOutcome>, mainFront: List<SimulationOutcome>): Double {
    var undominated = 0
    for (outcome in frontToCalculatePurity) {
        if(outcome in mainFront) {
            undominated++
        }
    }
    return undominated.toDouble() / mainFront.size.toDouble()
}

private fun getRankedDefensiveOutcomes(outcomes: List<SimulationOutcome>):
        MutableList<Pair<SimulationOutcome, Int>> {
    val remainingOutcomes = outcomes.toMutableList()
    val output = mutableListOf<Pair<SimulationOutcome, Int>>()
    var currentFront = 1
    while (remainingOutcomes.isNotEmpty()) {
        val nextFront = paretoEvaluate(remainingOutcomes)
        output += nextFront.map { Pair(it, currentFront) }
        remainingOutcomes -= nextFront.toSet()
        currentFront++
    }
    return output
}

private fun getRankedOffensiveOutcomes(outcomes: List<OffensiveGenome>): MutableList<Pair<OffensiveGenome, Int>> {
    val remainingOutcomes = outcomes.toMutableList()
    val output = mutableListOf<Pair<OffensiveGenome, Int>>()
    var currentFront = 1
    while (remainingOutcomes.isNotEmpty()) {
        val nextFront = paretoEvaluateOffensiveGenomes(remainingOutcomes)
        output += nextFront.map { Pair(it, currentFront) }
        remainingOutcomes -= nextFront.toSet()
        currentFront++
    }
    return output
}

private fun getRankedDefensiveOutcomesVolume(outcomes: List<Pair<SimulationOutcome, Int>>):
        List<Triple<Genome, Int, Double>> {
    val output = mutableListOf<Triple<Genome, Int, Double>>()
    var currentFront = 1
    outer@ while (output.size != outcomes.size) {
        val outcomesFromCurrentFront = outcomes.filter { it.second == currentFront }
        if (outcomesFromCurrentFront.size == 1) {
            output += Triple(
                outcomesFromCurrentFront[0].first.genome.clone(),
                outcomesFromCurrentFront[0].second,
                1.0
            )
            continue@outer
        }
        val sortedByProfitsOutcomes = outcomesFromCurrentFront.sortedBy { it.first.profits }
        for (i in sortedByProfitsOutcomes.indices) {
            output += Triple(
                sortedByProfitsOutcomes[i].first.genome.clone(),
                sortedByProfitsOutcomes[i].second,
                when (i) {
                    0 -> sortedByProfitsOutcomes[1].first.profits * sortedByProfitsOutcomes[1].first.risk
                    sortedByProfitsOutcomes.size - 1 ->
                        (sortedByProfitsOutcomes[i].first.profits
                                - sortedByProfitsOutcomes[i - 1].first.profits) * (sortedByProfitsOutcomes[i].first.risk
                                - sortedByProfitsOutcomes[i - 1].first.risk)
                    else ->
                        (sortedByProfitsOutcomes[i + 1].first.profits
                                - sortedByProfitsOutcomes[i - 1].first.profits) * (sortedByProfitsOutcomes[i + 1].first.risk
                                - sortedByProfitsOutcomes[i - 1].first.risk)
                }
            )
        }
        currentFront++
    }
    return output
}

private fun getRankedOffensiveOutcomesVolume(outcomes: List<Pair<OffensiveGenome, Int>>): List<Triple<OffensiveGenome, Int, Double>> {
    val output = mutableListOf<Triple<OffensiveGenome, Int, Double>>()
    var currentFront = 1
    outer@ while (output.size != outcomes.size) {
        val outcomesFromCurrentFront = outcomes.filter { it.second == currentFront }
        if (outcomesFromCurrentFront.size == 1) {
            output += Triple(outcomesFromCurrentFront[0].first.clone(), outcomesFromCurrentFront[0].second, 1.0)
            continue@outer
        }
        val sortedByProfitsOutcomes = outcomesFromCurrentFront.sortedBy { it.first.profitsWithDefensiveGenome!! }
        for (i in sortedByProfitsOutcomes.indices) {
            output += Triple(
                sortedByProfitsOutcomes[i].first.clone(), sortedByProfitsOutcomes[i].second, when (i) {
                    0 -> sortedByProfitsOutcomes[1].first.profitsWithDefensiveGenome!! * sortedByProfitsOutcomes[1].first.riskWithDefensiveGenome!!
                    sortedByProfitsOutcomes.size - 1 ->
                        (sortedByProfitsOutcomes[i].first.profitsWithDefensiveGenome!!
                                - sortedByProfitsOutcomes[i - 1].first.profitsWithDefensiveGenome!!) * (sortedByProfitsOutcomes[i].first.riskWithDefensiveGenome!!
                                - sortedByProfitsOutcomes[i - 1].first.riskWithDefensiveGenome!!)
                    else ->
                        (sortedByProfitsOutcomes[i + 1].first.profitsWithDefensiveGenome!!
                                - sortedByProfitsOutcomes[i - 1].first.profitsWithDefensiveGenome!!) * (sortedByProfitsOutcomes[i + 1].first.riskWithDefensiveGenome!!
                                - sortedByProfitsOutcomes[i - 1].first.riskWithDefensiveGenome!!)
                }
            )
        }
        currentFront++
    }
    return output
}

fun selectWithTournament(outcomes: List<SimulationOutcome>): List<Genome> {
    val output = mutableListOf<Genome>()
    while (output.size < POPULATION_SIZE - ELITISM) {
        val tournament = outcomes.shuffled().take(TOURNAMENT_PICKS)
        output += tournament.maxByOrNull { it.getHvValue() }!!.genome.clone()
    }
    return output
}

fun selectOffensiveGenomeWithTournament(outcomes: List<OffensiveGenome>): List<OffensiveGenome> {
    val output = mutableListOf<OffensiveGenome>()
    while (output.size < POPULATION_SIZE - ELITISM) {
        output += outcomes.shuffled()
            .take(TOURNAMENT_PICKS)
            .maxByOrNull { it.getHvValue() }!!.clone()
    }
    return output
}

fun <T> selectWithTournamentByRankAndVolume(outcomes: List<Triple<T, Int, Double>>):
        List<T> {
    val output = mutableListOf<T>()
    while (output.size < POPULATION_SIZE - ELITISM) {
        output += outcomes.shuffled()
            .take(TOURNAMENT_PICKS)
            .sortedBy { it.second }
            .maxByOrNull { it.third }!!
            .first
    }
    return output
}

private fun getMutatedGenomes(genomes: List<Genome>): List<Genome> {
    val output = mutableListOf<Genome>()
    for (genome in genomes) {
        output += genome.clone()
    }
    for (genome in output) {
        genome.mutate()
    }
    return output
}

private fun getCrossoverDefensiveGenomes(genomes: List<Genome>): List<Genome> {
    val output = mutableListOf<Genome>()
    for (i in genomes.indices step 2) {
        if (i + 1 == genomes.size) {
            output += genomes[i].clone()
        } else {
            val childrenInstance = genomes[i].modularCrossover(genomes[i + 1])
            output += childrenInstance.first
            output += childrenInstance.second
        }
    }
    return output
}

private fun getCrossoverOffensiveGenomes(genomes: List<OffensiveGenome>): List<OffensiveGenome> {
    val output = mutableListOf<OffensiveGenome>()
    for (i in genomes.indices step 2) {
        if (i + 1 == genomes.size) {
            output += genomes[i].clone()
        } else {
            val childrenInstance = genomes[i].modularCrossover(genomes[i + 1])
            output += childrenInstance.first
            output += childrenInstance.second
        }
    }
    return output
}
