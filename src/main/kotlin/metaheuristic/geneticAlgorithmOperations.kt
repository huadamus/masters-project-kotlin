@file:Suppress("UNCHECKED_CAST")

package metaheuristic

import simulation.GenomeGenerator
import ELITISM
import NTGA2_GS_GENERATIONS
import POPULATION_SIZE
import SPEA2_NEAREST_DISTANCE
import TOURNAMENT_PICKS
import model.Genome
import model.OffensiveGenome
import simulation.SimulationOutcome
import kotlin.math.pow
import kotlin.math.sqrt

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

fun getNewGenerationOffensiveGenomes(outcomes: List<OffensiveGenome>, mutateBoth: Boolean): List<OffensiveGenome> {
    val selectedGenomes = selectOffensiveGenomeWithTournament(outcomes).shuffled()
    if (mutateBoth) {
        return getMutatedOffensiveGenomes(getCrossoverOffensiveGenomes(selectedGenomes))
    }
    return getMutatedGenomes(getCrossoverOffensiveGenomes(selectedGenomes)) as List<OffensiveGenome>
}

fun getNewGenerationDefensiveGenomesByRankAndVolume(outcomes: List<SimulationOutcome>): List<Genome> {
    val rankedOutcomes = getRankedDefensiveOutcomes(outcomes)
    val rankedOutcomesWithVolume = getRankedDefensiveOutcomesVolume(rankedOutcomes)
    val selectedGenomes = selectWithTournamentByRankAndVolume(rankedOutcomesWithVolume)
    return getMutatedGenomes(getCrossoverDefensiveGenomes(selectedGenomes))
}

fun getNewGenerationOffensiveGenomesByRankAndVolume(
    outcomes: List<OffensiveGenome>,
    mutateBoth: Boolean
): List<OffensiveGenome> {
    val rankedOutcomes = getRankedOffensiveOutcomes(outcomes)
    val rankedOutcomesWithVolume = getRankedOffensiveOutcomesVolume(rankedOutcomes)
    val selectedGenomes = selectWithTournamentByRankAndVolume(rankedOutcomesWithVolume)
    if (mutateBoth) {
        return getMutatedOffensiveGenomes(getCrossoverOffensiveGenomes(selectedGenomes))
    }
    return getMutatedGenomes(getCrossoverOffensiveGenomes(selectedGenomes)) as List<OffensiveGenome>
}

fun getNewGenerationDefensiveGenomesByStrength(outcomes: List<SimulationOutcome>): List<Genome> {
    val outcomesWithStrength = evaluateDefensiveGenomesStrength(outcomes)
    val outcomesWithFitness = evaluateDefensiveGenomesStrengthRawFitness(outcomesWithStrength)
    val outcomesWithCorrectFitness =
        outcomesWithFitness.map { case ->
            Pair(
                case.first,
                case.second + evaluateNNearestDistanceSum(
                    SPEA2_NEAREST_DISTANCE,
                    Pair(case.first.profits, case.first.risk),
                    (outcomesWithFitness - case).map { Pair(it.first.profits, it.first.risk) }
                )
            )
        }
    val binarySelectedGenomes = mutableListOf<Genome>()
    while (binarySelectedGenomes.size != POPULATION_SIZE) {
        val outcome1 = outcomesWithCorrectFitness.random()
        val outcome2 = outcomesWithCorrectFitness.random()
        binarySelectedGenomes += if (outcome1.second > outcome2.second) {
            outcome1.first.genome.clone()
        } else {
            outcome2.first.genome.clone()
        }
    }
    return getMutatedGenomes(getCrossoverDefensiveGenomes(binarySelectedGenomes))
}

fun getNewGenerationOffensiveGenomesByStrength(
    outcomes: List<OffensiveGenome>,
    mutateBoth: Boolean
): List<OffensiveGenome> {
    val outcomesWithStrength = evaluateOffensiveGenomesStrength(outcomes)
    val outcomesWithFitness = evaluateOffensiveGenomesStrengthRawFitness(outcomesWithStrength)
    val outcomesWithCorrectFitness =
        outcomesWithFitness.map { case ->
            Pair(
                case.first,
                case.second + evaluateNNearestDistanceSum(
                    SPEA2_NEAREST_DISTANCE,
                    Pair(case.first.profitsWithDefensiveGenome!!, case.first.riskWithDefensiveGenome!!),
                    (outcomesWithFitness - case).map {
                        Pair(
                            it.first.profitsWithDefensiveGenome!!,
                            it.first.riskWithDefensiveGenome!!
                        )
                    }
                )
            )
        }
    val binarySelectedGenomes = mutableListOf<OffensiveGenome>()
    while (binarySelectedGenomes.size != POPULATION_SIZE) {
        val outcome1 = outcomesWithCorrectFitness.random()
        val outcome2 = outcomesWithCorrectFitness.random()
        binarySelectedGenomes += if (outcome1.second > outcome2.second) {
            outcome2.first.clone()
        } else {
            outcome1.first.clone()
        }
    }
    if (mutateBoth) {
        return getMutatedOffensiveGenomes(getCrossoverOffensiveGenomes(binarySelectedGenomes))
    }
    return getMutatedGenomes(getCrossoverOffensiveGenomes(binarySelectedGenomes)) as List<OffensiveGenome>
}

fun getNewGenerationDefensiveGenomesByNtgaMethod(
    generation: Int,
    outcomes: Collection<SimulationOutcome>,
    archive: Collection<SimulationOutcome>
): List<Genome> {
    val combined = outcomes + archive
    val selection = if (generation.mod(2 * NTGA2_GS_GENERATIONS) < NTGA2_GS_GENERATIONS) {
        val rankedOutcomes = getRankedDefensiveOutcomes(combined)
        val rankedOutcomesWithVolume = getRankedDefensiveOutcomesVolume(rankedOutcomes)
        selectWithTournamentByRankAndVolume(rankedOutcomesWithVolume)
    } else {
        val selected = mutableListOf<Genome>()
        while (selected.size < POPULATION_SIZE) {
            selected += selectDefensiveGenomesWithGapSelection(combined).toList()
                .map { it.genome.clone() }
        }
        selected
    }
    val children = mutableListOf<Genome>()
    for (i in 0 until POPULATION_SIZE / 2) {
        children += selection[i * 2].modularCrossover(selection[i * 2 + 1]).toList()
    }
    children.forEach { it.mutate() }
    return children
}

fun getNewGenerationOffensiveGenomesByNtgaMethod(
    generation: Int,
    outcomes: Collection<OffensiveGenome>,
    archive: Collection<OffensiveGenome>,
    mutateBoth: Boolean
): List<OffensiveGenome> {
    val combined = outcomes + archive
    val selection = if (generation.mod(2 * NTGA2_GS_GENERATIONS) < NTGA2_GS_GENERATIONS) {
        val rankedOutcomes = getRankedOffensiveOutcomes(combined)
        val rankedOutcomesWithVolume = getRankedOffensiveOutcomesVolume(rankedOutcomes)
        selectWithTournamentByRankAndVolume(rankedOutcomesWithVolume)
    } else {
        val selected = mutableListOf<OffensiveGenome>()
        while (selected.size < POPULATION_SIZE) {
            selected += selectOffensiveGenomesWithGapSelection(combined).toList()
                .map { it.clone() }
        }
        selected
    }
    val children = mutableListOf<OffensiveGenome>()
    for (i in 0 until POPULATION_SIZE / 2) {
        children += selection[i * 2].modularCrossover(selection[i * 2 + 1]).toList()
    }
    children.forEach { it.mutate() }
    if (mutateBoth) {
        children.forEach { genome ->
            genome.bestDefensiveGenome?.mutate()
        }
    }
    return children
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

fun evaluateDefensiveGenomesStrength(outcomes: List<SimulationOutcome>): Set<Pair<SimulationOutcome, Int>> {
    val output = mutableSetOf<Pair<SimulationOutcome, Int>>()
    for (outcome in outcomes) {
        var strength = 0
        for (comparedOutcome in outcomes) {
            if (outcome != comparedOutcome) {
                if (!(comparedOutcome.profits >= outcome.profits && comparedOutcome.risk < outcome.risk)
                    && !(comparedOutcome.profits > outcome.profits && comparedOutcome.risk <= outcome.risk)
                ) {
                    strength++
                }
            }
        }
        output += Pair(outcome.clone(), strength)
    }
    return output
}

fun evaluateOffensiveGenomesStrength(outcomes: List<OffensiveGenome>): Set<Pair<OffensiveGenome, Int>> {
    val output = mutableSetOf<Pair<OffensiveGenome, Int>>()
    for (outcome in outcomes) {
        var strength = 0
        for (comparedOutcome in outcomes) {
            if (!outcome.isSame(comparedOutcome)) {
                if (!(comparedOutcome.profitsWithDefensiveGenome!! >= outcome.profitsWithDefensiveGenome!!
                            && comparedOutcome.riskWithDefensiveGenome!! < outcome.riskWithDefensiveGenome!!)
                    && !(comparedOutcome.profitsWithDefensiveGenome!! > outcome.profitsWithDefensiveGenome!!
                            && comparedOutcome.riskWithDefensiveGenome!! <= outcome.riskWithDefensiveGenome!!)
                ) {
                    strength++
                }
            }
        }
        output += Pair(outcome.clone(), strength)
    }
    return output
}

fun evaluateDefensiveGenomesStrengthRawFitness(outcomesWithStrength: Set<Pair<SimulationOutcome, Int>>):
        Set<Pair<SimulationOutcome, Int>> {
    val output = mutableSetOf<Pair<SimulationOutcome, Int>>()
    for (outcome in outcomesWithStrength) {
        var rawFitness = 0
        for (comparedOutcome in outcomesWithStrength) {
            if (outcome.first != comparedOutcome.first) {
                if ((comparedOutcome.first.profits >= outcome.first.profits
                            && comparedOutcome.first.risk < outcome.first.risk)
                    || (comparedOutcome.first.profits > outcome.first.profits
                            && comparedOutcome.first.risk <= outcome.first.risk)
                ) {
                    rawFitness += comparedOutcome.second
                }
            }
        }
        output += Pair(outcome.first, rawFitness)
    }
    return output
}

fun evaluateOffensiveGenomesStrengthRawFitness(outcomesWithStrength: Set<Pair<OffensiveGenome, Int>>):
        Set<Pair<OffensiveGenome, Int>> {
    val output = mutableSetOf<Pair<OffensiveGenome, Int>>()
    for (outcome in outcomesWithStrength) {
        var rawFitness = 0
        for (comparedOutcome in outcomesWithStrength) {
            if (outcome.first != comparedOutcome.first) {
                if ((comparedOutcome.first.profitsWithDefensiveGenome!! >= outcome.first.profitsWithDefensiveGenome!!
                            && comparedOutcome.first.riskWithDefensiveGenome!! < outcome.first.riskWithDefensiveGenome!!)
                    || (comparedOutcome.first.profitsWithDefensiveGenome!! > outcome.first.profitsWithDefensiveGenome!!
                            && comparedOutcome.first.riskWithDefensiveGenome!! <= outcome.first.riskWithDefensiveGenome!!)
                ) {
                    rawFitness += comparedOutcome.second
                }
            }
        }
        output += Pair(outcome.first, rawFitness)
    }
    return output
}

fun evaluateNNearestDistanceSum(n: Int, case: Pair<Double, Double>, otherCases: List<Pair<Double, Double>>): Double {
    val distances = mutableListOf<Double>()
    for (otherCase in otherCases) {
        val distance = sqrt((otherCase.first - case.first).pow(2) + (otherCase.second - case.second).pow(2))
        distances += distance
    }
    return distances.sortedBy { it }
        .take(n)
        .sum()
}

fun calculateParetoPurity(
    frontToCalculatePurity: List<SimulationOutcome>,
    mainFront: List<SimulationOutcome>
): Double {
    var undominated = 0
    for (outcome in frontToCalculatePurity) {
        if (outcome in mainFront) {
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
        val taken = outcomes.shuffled()
            .take(TOURNAMENT_PICKS)
        output += taken
            .filter { individual -> individual.second == taken.map { it.second }.minByOrNull { it } }
            .maxByOrNull { it.third }!!
            .first
    }
    return output
}

fun selectDefensiveGenomesWithGapSelection(
    population: List<SimulationOutcome>
): Pair<SimulationOutcome, SimulationOutcome> {
    val firstParent = selectWithTournamentByGaps(population).clone()
    val neighbors = getEuclideanNeighbors(firstParent, population)
    var secondParent = neighbors.random()?.first?.clone()
    if (secondParent == null) {
        secondParent = selectWithTournamentByGaps(population)
    }
    return Pair(firstParent, secondParent)
}

fun selectOffensiveGenomesWithGapSelection(
    population: List<OffensiveGenome>
): Pair<OffensiveGenome, OffensiveGenome> {
    val firstParent = selectOffensiveGenomesWithTournamentByGaps(population).clone()
    val neighbors = getEuclideanNeighbors(firstParent, population)
    var secondParent = neighbors.random()?.first?.clone()
    if (secondParent == null) {
        secondParent = selectOffensiveGenomesWithTournamentByGaps(population)
    }
    return Pair(firstParent, secondParent)
}

fun selectWithTournamentByGaps(outcomes: List<SimulationOutcome>): SimulationOutcome {
    return outcomes.shuffled()
        .take(TOURNAMENT_PICKS)
        .map { outcome ->
            val neighbors = getEuclideanNeighbors(outcome, outcomes)
            val distance = if (neighbors.contains(null)) {
                Double.MAX_VALUE
            } else {
                neighbors.maxOf { it!!.second }
            }
            Pair(outcome, distance)
        }
        .maxByOrNull { it.second }!!.first
}

fun selectOffensiveGenomesWithTournamentByGaps(outcomes: List<OffensiveGenome>): OffensiveGenome {
    return outcomes.shuffled()
        .take(TOURNAMENT_PICKS)
        .map { outcome ->
            val neighbors = getEuclideanNeighbors(outcome, outcomes)
            val distance = if (neighbors.contains(null)) {
                Double.MAX_VALUE
            } else {
                neighbors.maxOf { it!!.second }
            }
            Pair(outcome, distance)
        }
        .maxByOrNull { it.second }!!.first
}

private fun getEuclideanNeighbors(
    individual: SimulationOutcome,
    outcomes: List<SimulationOutcome>
): List<Pair<SimulationOutcome, Double>?> {
    val outcomesWithDistances = (outcomes - individual).map {
        Pair(it, sqrt((individual.profits - it.profits).pow(2) + (individual.risk - it.risk).pow(2)))
    }.sortedBy { it.second }
    if (individual == outcomesWithDistances.first().first || individual == outcomesWithDistances.last().first) {
        val output = outcomesWithDistances.take(1).toMutableList<Pair<SimulationOutcome, Double>?>()
        output += null
        return output
    }
    return outcomesWithDistances.take(2)
        .map { Pair(it.first.clone(), it.second) }
        .toList()
}

private fun getEuclideanNeighbors(
    individual: OffensiveGenome,
    outcomes: List<OffensiveGenome>
): List<Pair<OffensiveGenome, Double>?> {
    val outcomesWithDistances = (outcomes - individual).map {
        Pair(
            it,
            sqrt(
                (individual.profitsWithDefensiveGenome!! - it.profitsWithDefensiveGenome!!).pow(2)
                        + (individual.riskWithDefensiveGenome!! - it.riskWithDefensiveGenome!!).pow(2)
            )
        )
    }.sortedBy { it.second }
    if (individual == outcomesWithDistances.first().first || individual == outcomesWithDistances.last().first) {
        val output = outcomesWithDistances.take(1).toMutableList<Pair<OffensiveGenome, Double>?>()
        output += null
        return output
    }
    return outcomesWithDistances.take(2).toList()
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

private fun getMutatedOffensiveGenomes(genomes: List<OffensiveGenome>): List<OffensiveGenome> {
    val output = mutableListOf<OffensiveGenome>()
    for (genome in genomes) {
        output += genome.clone()
    }
    for (genome in output) {
        genome.mutate()
        if (genome.bestDefensiveGenome != null) {
            genome.bestDefensiveGenome!!.mutate()
        }
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
