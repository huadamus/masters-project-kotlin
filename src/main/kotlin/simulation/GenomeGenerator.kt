package simulation

import model.CombinedGenome
import model.Genome
import model.OffensiveGenome
import model.Parameter
import kotlin.random.Random

object GenomeGenerator {

    fun generateDefensiveGenome(periodMonths: Int): Genome {
        return Genome(
            getRandomParameters(),
            periodMonths
        )
    }

    fun generateOffensiveGenome(periodMonths: Int): OffensiveGenome {
        return OffensiveGenome(
            getRandomChoiceParameter(),
            getRandomParameters(),
            periodMonths
        )
    }

    fun generateCombinedGenome(periodMonths: Int): CombinedGenome {
        return CombinedGenome(
            OffensiveGenome(
                getRandomChoiceParameter(),
                getRandomParameters(),
                periodMonths
            ),
            Genome(
                getRandomParameters(), periodMonths
            )
        )
    }

    private fun getRandomChoiceParameter(): Double {
        return Random.nextDouble()
    }

    private fun getRandomParameters(): MutableMap<Parameter, Double> {
        val output = mutableMapOf<Parameter, Double>()
        for (parameter in Parameter.values()) {
            output[parameter] = Random.nextDouble()
        }
        return output
    }
}
