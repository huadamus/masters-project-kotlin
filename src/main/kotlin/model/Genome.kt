package model

import CROSSOVER_CHANCE
import MUTATION_CHANCE
import java.io.Serializable
import kotlin.random.Random

open class Genome(protected val parameters: MutableMap<Parameter, Double>, val periodMonths: Int) :
    Serializable {

    init {
        if (parameters.size != Parameter.values().size) {
            throw Exception("Invalid genome generated!")
        }
    }

    fun getRawParameters() = parameters.toMutableMap()

    fun getParameter(parameter: Parameter) =
        (parameters[parameter]!! * (parameter.restriction2 - parameter.restriction1)) + parameter.restriction1

    fun modularCrossover(otherGenome: Genome): Pair<Genome, Genome> {
        val roll = Random.nextDouble()
        if (roll <= CROSSOVER_CHANCE) {
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
            val genome1 = Genome(child1Parameters, periodMonths)
            val genome2 = Genome(child2Parameters, periodMonths)
            return Pair(genome1.clone(), genome2.clone())
        }
        return Pair(this.clone(), otherGenome.clone())
    }

    fun uniformCrossover(otherGenome: Genome): Pair<Genome, Genome> {
        val roll = Random.nextDouble()
        if (roll <= CROSSOVER_CHANCE) {
            val child1Parameters = mutableMapOf<Parameter, Double>()
            val child2Parameters = mutableMapOf<Parameter, Double>()
            for (parameter in parameters) {
                val parameterRoll = Random.nextDouble()
                if (parameterRoll < 0.5) {
                    child1Parameters[parameter.key] = parameter.value
                    child2Parameters[parameter.key] = otherGenome.parameters[parameter.key]!!
                } else {
                    child1Parameters[parameter.key] = otherGenome.parameters[parameter.key]!!
                    child2Parameters[parameter.key] = parameter.value
                }
            }
            val genome1 = Genome(child1Parameters, periodMonths)
            val genome2 = Genome(child2Parameters, periodMonths)
            return Pair(genome1, genome2)
        }
        return Pair(this.clone(), otherGenome.clone())
    }

    open fun mutate() {
        for (parameter in Parameter.values()) {
            val roll = Random.nextDouble()
            if (roll <= MUTATION_CHANCE) {
                parameters[parameter] = Random.nextDouble()
            }
        }
    }

    open fun clone(): Genome {
        val newGenomeParameters = mutableMapOf<Parameter, Double>()
        for (parameter in parameters) {
            newGenomeParameters[parameter.key] = parameter.value
        }
        return Genome(newGenomeParameters, periodMonths)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Genome) {
            return false
        }
        if (periodMonths != other.periodMonths) {
            return false
        }
        for (parameter in parameters) {
            if (parameter.value != other.parameters[parameter.key]) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = parameters.hashCode()
        result = 31 * result + periodMonths
        return result
    }
}
