package model

import CROSSOVER_CHANCE
import GAUSS_MUTATION
import MUTATION_CHANCE
import simulation.hvParetoFitnessFunction
import kotlin.random.Random

class OffensiveGenome(
    private var choiceParameter: Double,
    parameters: MutableMap<Parameter, Double>,
    periodMonths: Int,
) : Genome(parameters, periodMonths) {
    var bestDefensiveGenome: DefensiveGenome? = null
    var profitsWithDefensiveGenome: Double? = null
    var riskWithDefensiveGenome: Double? = null
    var strategyDetailsWithDefensiveGenome: List<StrategyDetails>? = null

    fun getHvValue(): Double {
        return if (profitsWithDefensiveGenome == null || riskWithDefensiveGenome == null) {
            -Double.MAX_VALUE
        } else {
            hvParetoFitnessFunction(profitsWithDefensiveGenome!!, riskWithDefensiveGenome!!)
        }
    }

    fun isTimeToSwitch(shillersValue: Double) = shillersValue > getChoiceParameter()

    fun getChoiceParameter() =
        (choiceParameter * (CHOICE_PARAMETER_RESTRICTIONS.second - CHOICE_PARAMETER_RESTRICTIONS.first)) +
                CHOICE_PARAMETER_RESTRICTIONS.first

    fun modularCrossover(otherGenome: OffensiveGenome): Pair<OffensiveGenome, OffensiveGenome> {
        val roll = Random.nextDouble()
        if (roll <= CROSSOVER_CHANCE) {
            val child1ChoiceParameter: Double
            val child2ChoiceParameter: Double
            val child1Parameters = mutableMapOf<Parameter, Double>()
            val child2Parameters = mutableMapOf<Parameter, Double>()
            val choiceParameterRoll = Random.nextDouble()
            if (choiceParameterRoll < 0.5) {
                child1ChoiceParameter = choiceParameter
                child2ChoiceParameter = otherGenome.choiceParameter
            } else {
                child1ChoiceParameter = otherGenome.choiceParameter
                child2ChoiceParameter = choiceParameter
            }
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
            val genome1 = OffensiveGenome(child1ChoiceParameter, child1Parameters, periodMonths)
            genome1.bestDefensiveGenome = bestDefensiveGenome
            val genome2 = OffensiveGenome(child2ChoiceParameter, child2Parameters, periodMonths)
            genome2.bestDefensiveGenome = otherGenome.bestDefensiveGenome
            return Pair(genome1.clone(), genome2.clone())
        }
        return Pair(this.clone(), otherGenome.clone())
    }

    fun uniformCrossover(otherGenome: OffensiveGenome): Pair<OffensiveGenome, OffensiveGenome> {
        val roll = Random.nextDouble()
        if (roll <= CROSSOVER_CHANCE) {
            val child1ChoiceParameter: Double
            val child2ChoiceParameter: Double
            val child1Parameters = mutableMapOf<Parameter, Double>()
            val child2Parameters = mutableMapOf<Parameter, Double>()
            val choiceParameterRoll = Random.nextDouble()
            if (choiceParameterRoll < 0.5) {
                child1ChoiceParameter = choiceParameter
                child2ChoiceParameter = otherGenome.choiceParameter
            } else {
                child1ChoiceParameter = otherGenome.choiceParameter
                child2ChoiceParameter = choiceParameter
            }
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
            val genome1 = OffensiveGenome(child1ChoiceParameter, child1Parameters, periodMonths)
            genome1.bestDefensiveGenome = bestDefensiveGenome
            val genome2 = OffensiveGenome(child2ChoiceParameter, child2Parameters, periodMonths)
            genome2.bestDefensiveGenome = otherGenome.bestDefensiveGenome
            return Pair(genome1, genome2)
        }
        return Pair(this.clone(), otherGenome.clone())
    }

    override fun mutate() {
        val choiceParameterRoll = Random.nextDouble()
        if (choiceParameterRoll <= MUTATION_CHANCE) {
            choiceParameter = if(GAUSS_MUTATION) {
                java.util.Random().nextGaussian(
                    choiceParameter, 0.3
                )
            } else {
                Random.nextDouble()
            }
        }
        for (parameter in Parameter.values()) {
            val roll = Random.nextDouble()
            if (roll <= MUTATION_CHANCE) {
                if(GAUSS_MUTATION) {
                    val newParameterValue = java.util.Random().nextGaussian(
                        parameters[parameter]!!, 0.3
                    )
                    parameters[parameter] =
                        maxOf(
                            0.0, minOf(
                                1.0, newParameterValue
                            )
                        )
                } else {
                    parameters[parameter] = Random.nextDouble()
                }
            }
        }
    }

    override fun clone(): OffensiveGenome {
        val newGenomeParameters = mutableMapOf<Parameter, Double>()
        for (parameter in parameters) {
            newGenomeParameters[parameter.key] = parameter.value
        }
        val output = OffensiveGenome(choiceParameter, newGenomeParameters, periodMonths)
        output.bestDefensiveGenome = null
        bestDefensiveGenome?.let {
            output.bestDefensiveGenome = it.clone()
        }
        output.profitsWithDefensiveGenome = profitsWithDefensiveGenome
        output.riskWithDefensiveGenome = riskWithDefensiveGenome
        output.strategyDetailsWithDefensiveGenome = strategyDetailsWithDefensiveGenome
        return output
    }

    override fun equals(other: Any?): Boolean {
        if (other !is OffensiveGenome) {
            return false
        }
        if (choiceParameter != other.choiceParameter) {
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
        if (bestDefensiveGenome != other.bestDefensiveGenome) {
            return false
        }
        if (profitsWithDefensiveGenome != other.profitsWithDefensiveGenome) {
            return false
        }
        if (riskWithDefensiveGenome != other.riskWithDefensiveGenome) {
            return false
        }
        if (strategyDetailsWithDefensiveGenome != other.strategyDetailsWithDefensiveGenome) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = choiceParameter.hashCode()
        result = 19 * result + parameters.hashCode()
        result = 3 * result + periodMonths
        result = 5 * result + (bestDefensiveGenome?.hashCode() ?: 0)
        result = 7 * result + (profitsWithDefensiveGenome?.hashCode() ?: 0)
        result = 11 * result + (riskWithDefensiveGenome?.hashCode() ?: 0)
        result = 17 * result + (strategyDetailsWithDefensiveGenome?.hashCode() ?: 0)
        return result
    }

    fun isSame(offensiveGenome: OffensiveGenome): Boolean {
        if (choiceParameter != offensiveGenome.choiceParameter) {
            return false
        }
        if (periodMonths != offensiveGenome.periodMonths) {
            return false
        }
        for (parameter in parameters) {
            if (parameter.value != offensiveGenome.parameters[parameter.key]) {
                return false
            }
        }
        return true
    }

    companion object {
        val CHOICE_PARAMETER_RESTRICTIONS = Pair(15.0, 40.0)
    }
}
