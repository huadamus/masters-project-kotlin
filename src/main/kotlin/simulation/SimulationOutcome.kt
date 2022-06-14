package simulation

import model.DefensiveGenome
import model.Genome
import output.round
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class SimulationOutcome(
    var profits: Double,
    var risk: Double
) : Serializable {

    lateinit var genome: Genome
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
        return if (this::genome.isInitialized) {
            if (other::genome.isInitialized) {
                genome == other.genome && profits == other.profits && risk == other.risk
            } else {
                false
            }
        } else {
            if (!other::genome.isInitialized) {
                profits == other.profits && risk == other.risk
            } else {
                false
            }
        }
    }

    override fun hashCode(): Int {
        var result = profits.hashCode()
        if(this::genome.isInitialized) {
            result = 31 * result + genome.hashCode()
        }
        result = 31 * result + profits.hashCode()
        result = 31 * result + risk.hashCode()
        return result
    }

    private fun writeObject(oos: ObjectOutputStream) {
        oos.writeObject(genome)
        oos.writeDouble(profits)
        oos.writeDouble(risk)
    }

    private fun readObject(ois: ObjectInputStream) {
        genome = ois.readObject() as DefensiveGenome
        profits = ois.readDouble()
        risk = ois.readDouble()
    }

    fun clone(): SimulationOutcome {
        val output = SimulationOutcome(profits, risk)
        if(this::genome.isInitialized) {
            output.genome = genome.clone()
        }
        return output
    }
}
