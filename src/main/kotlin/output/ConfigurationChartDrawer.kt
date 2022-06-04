package output

import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import simulation.SimulationOutcome
import simulation.hvParetoFitnessFunctionForSet
import simulation.invertedGenerationalDistanceForSet
import simulation.spacingForSet


class ConfigurationChartDrawer(private val purityValues: Array<Double>, private val timeValues: Array<Int>) :
    ChartDrawer("Badanie konfiguracji systemu") {
    private val labels = arrayOf(
        "EA-HV",
        "NSGA-II",
        "SPEA2",
        "NTGA2",
        "cEA-HV",
        "cNSGA-II",
        "cSPEA2",
        "cNTGA2",
        "MOEA/D",
    )
    private var algorithmOutcomes: List<List<SimulationOutcome>>? = null

    override fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset {
        this.algorithmOutcomes = algorithmOutcomes
        if (algorithmOutcomes.size != 9) {
            throw Exception(
                "This chart drawer supports 9 algorithm outcomes"
            )
        }
        val dataset = XYSeriesCollection()
        val noCoevolutionHvParetoSeries = XYSeries(labels[0])
        for (outcome in algorithmOutcomes[0]) {
            noCoevolutionHvParetoSeries.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(noCoevolutionHvParetoSeries)
        val noCoevolutionNsga2Series = XYSeries(labels[1])
        for (outcome in algorithmOutcomes[1]) {
            noCoevolutionNsga2Series.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(noCoevolutionNsga2Series)
        val noCoevolutionSpea2Series = XYSeries(labels[2])
        for (outcome in algorithmOutcomes[2]) {
            noCoevolutionSpea2Series.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(noCoevolutionSpea2Series)
        val noCoevolutionNtga2Series = XYSeries(labels[3])
        for (outcome in algorithmOutcomes[3]) {
            noCoevolutionNtga2Series.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(noCoevolutionNtga2Series)
        val coevolutionHvParetoSeries = XYSeries(labels[4])
        for (outcome in algorithmOutcomes[4]) {
            coevolutionHvParetoSeries.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(coevolutionHvParetoSeries)
        val coevolutionNsgaIISeries = XYSeries(labels[5])
        for (outcome in algorithmOutcomes[5]) {
            coevolutionNsgaIISeries.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(coevolutionNsgaIISeries)
        val coevolutionSpea2Series = XYSeries(labels[6])
        for (outcome in algorithmOutcomes[6]) {
            coevolutionSpea2Series.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(coevolutionSpea2Series)
        val coevolutionNtga2Series = XYSeries(labels[7])
        for (outcome in algorithmOutcomes[7]) {
            coevolutionNtga2Series.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(coevolutionNtga2Series)
        val moeaDSeries = XYSeries(labels[8])
        for (outcome in algorithmOutcomes[8]) {
            moeaDSeries.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(moeaDSeries)
        return dataset
    }

    override fun setSeriesRendering(chart: JFreeChart, plot: XYPlot) {
        for (i in 0 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i, seriesColors[i % 4])
        }
        for (i in 0 until plot.getDataset(0).seriesCount / 2) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(i, shapes[2])
        }
        for (i in plot.getDataset(0).seriesCount / 2 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(i, shapes[3])
        }
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(8, shapes[0])
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(8, classicalStrategiesColor)
        val chartinfo = buildString {
            for (i in 0 until 9) {
                append(
                    "${labels[i]} - Purity: ${"%.3f".format(purityValues[i])}, Pareto Front size: ${algorithmOutcomes!![i].size}, " +
                            "Inverted Generational Distance: ${
                                "%.3f".format(
                                    invertedGenerationalDistanceForSet(algorithmOutcomes!![i].map {
                                        Pair(
                                            it.profits,
                                            it.risk
                                        )
                                    })
                                )
                            }, Hypervolume: ${
                                "%.3f".format(
                                    hvParetoFitnessFunctionForSet(algorithmOutcomes!![i].map {
                                        Pair(
                                            it.profits,
                                            it.risk
                                        )
                                    })
                                )
                            }, Spacing: ${
                                "%.3f".format(
                                    spacingForSet(algorithmOutcomes!![i].map {
                                        Pair(
                                            it.profits,
                                            it.risk
                                        )
                                    })
                                )
                            } Time: ${if(timeValues.size > i) timeValues[i] else "N/A"}s" +
                            System.lineSeparator()
                )
            }
        }
        println(chartinfo)
    }
}
