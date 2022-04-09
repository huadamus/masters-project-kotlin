package simulation

import kotlin.math.pow
import kotlin.math.sqrt

fun hvParetoFitnessFunction(profits: Double, risk: Double) = profits * (100.0 - risk)

fun hvParetoFitnessFunctionForSet(results: List<Pair<Double, Double>>): Double {
    val sortedResults = results.sortedByDescending { it.first }
    var totalVolume = 0.0
    var lastRisk = 100.0
    for (result in sortedResults) {
        totalVolume += result.first * (lastRisk - result.second)
        lastRisk = result.second
    }
    return totalVolume
}

fun invertedGenerationalDistanceForSet(results: List<Pair<Double, Double>>): Double {
    var totalDistance = 0.0
    for (result in results) {
        totalDistance += sqrt(result.second.pow(2.0) + (100.0 - result.first).pow(2))
    }
    return totalDistance / results.size
}

fun spacingForSet(results: List<Pair<Double, Double>>): Double {
    val sortedResults = results.sortedBy { it.first }
    val distances = mutableListOf<Double>()
    for (i in 0 until sortedResults.size - 1) {
        distances += sqrt(
            (sortedResults[i + 1].first - sortedResults[i].first).pow(2)
                    + (sortedResults[i + 1].second - sortedResults[i].second).pow(2)
        )
    }
    return distances.average()
}
