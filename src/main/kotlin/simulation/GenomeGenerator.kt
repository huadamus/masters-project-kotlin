package simulation

import model.DefensiveGenome
import model.OffensiveGenome
import model.Parameter
import kotlin.random.Random

object GenomeGenerator {

    fun generateDefensiveGenome(periodMonths: Int): DefensiveGenome {
        return DefensiveGenome(
            getRandomChoiceParameter(),
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
