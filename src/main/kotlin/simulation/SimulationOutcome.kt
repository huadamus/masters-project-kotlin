package simulation

import output.round
import java.io.Serializable

abstract class SimulationOutcome(
    var profits: Double,
    var risk: Double
) : Serializable {

    private var fitness: Double? = null

    fun getHvValue(): Double {
        return if (fitness == null) {
            val output = hvParetoFitnessFunction(profits, risk)
            fitness = output
            output
        } else {
            fitness!!
        }
    }

    override fun toString(): String {
        return "{${profits.round()} ${risk.round()} ${getHvValue().round()}}"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SimulationOutcome) {
            return false
        }
        return profits == other.profits && risk == other.risk
    }

    override fun hashCode(): Int {
        var result = profits.hashCode()
        result = 31 * result + risk.hashCode()
        return result
    }

    abstract fun clone(): SimulationOutcome
}
