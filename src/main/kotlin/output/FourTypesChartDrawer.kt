package output

import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.title.TextTitle
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.ui.HorizontalAlignment.CENTER
import org.jfree.ui.RectangleEdge.TOP
import simulation.SimulationOutcome
import simulation.hvParetoFitnessFunctionForSet
import simulation.invertedGenerationalDistanceForSet
import simulation.spacingForSet


class FourTypesChartDrawer(private val purityValues: Array<Double>) :
    ChartDrawer("Badanie konfiguracji systemu") {
    private val labels = arrayOf(
        "Gener. AG, sel. hypervolume",
        "Gener. AG, sel. NSGA-II",
        "Koew. AG, sel. hypervolume",
        "Koew. AG, sel."
    )
    private var algorithmOutcomes: List<List<SimulationOutcome>>? = null

    override fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset {
        this.algorithmOutcomes = algorithmOutcomes
        if (algorithmOutcomes.size != 4) {
            throw Exception(
                "This chart drawer supports four algorithm outcomes"
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
        val coevolutionHvParetoSeries = XYSeries(labels[2])
        for (outcome in algorithmOutcomes[2]) {
            coevolutionHvParetoSeries.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(coevolutionHvParetoSeries)
        val coevolutionNsgaIISeries = XYSeries(labels[3])
        for (outcome in algorithmOutcomes[3]) {
            coevolutionNsgaIISeries.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(coevolutionNsgaIISeries)
        return dataset
    }

    override fun setSeriesRendering(chart: JFreeChart, plot: XYPlot) {
        for (i in 0 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i, seriesColors[i])
        }
        val chartinfo = TextTitle(buildString {
            for (i in 0 until 4) {
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
                            }" +
                            System.lineSeparator()
                )
            }
        })
        chartinfo.position = TOP
        chartinfo.horizontalAlignment = CENTER
        chart.addSubtitle(chartinfo)
    }
}
