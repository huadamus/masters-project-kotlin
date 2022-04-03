package model

import CROSSOVER_CHANCE
import kotlin.random.Random

class CombinedGenome(val offensiveGenome: OffensiveGenome, defensiveGenome: Genome) :
    Genome(defensiveGenome.getRawParameters(), defensiveGenome.periodMonths) {

    fun modularCrossover(otherGenome: CombinedGenome): Pair<CombinedGenome, CombinedGenome> {
        val roll = Random.nextDouble()
        if (roll <= CROSSOVER_CHANCE) {
            val offensiveGenomes = offensiveGenome.modularCrossover(otherGenome.offensiveGenome)
            val child1Parameters = mutableMapOf<Parameter, Double>()
            val child2Parameters = mutableMapOf<Parameter, Double>()
            for (i in 0..3) {
                val groupRoll = Random.nextDouble()
                if (groupRoll < 0.5) {
                    for (parameter in parameters) {
                        if (parameter.key.group == i) {
                            child1Parameters[parameter.key] = parameter.value
                            child2Parameters[parameter.key] = otherGenome.parameters[parameter.key]!!
                        }
                    }
                } else {
                    for (parameter in parameters) {
                        if (parameter.key.group == i) {
                            child1Parameters[parameter.key] = otherGenome.parameters[parameter.key]!!
                            child2Parameters[parameter.key] = parameter.value
                        }
                    }
                }
            }
            val genome1 = CombinedGenome(offensiveGenomes.first, Genome(child1Parameters, periodMonths))
            val genome2 = CombinedGenome(offensiveGenomes.second, Genome(child2Parameters, periodMonths))
            return Pair(genome1.clone(), genome2.clone())
        }
        return Pair(this.clone(), otherGenome.clone())
    }

    override fun mutate() {
        offensiveGenome.mutate()
        super.mutate()
    }

    override fun clone(): CombinedGenome {
        return CombinedGenome(offensiveGenome.clone(), super.clone())
    }
}
