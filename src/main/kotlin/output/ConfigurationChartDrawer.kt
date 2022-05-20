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


class ConfigurationChartDrawer(private val purityValues: Array<Double>) :
    ChartDrawer("Badanie konfiguracji systemu") {
    private val labels = arrayOf(
        "Gener. AG, sel. hypervolume",
        "Gener. AG, sel. NSGA-II",
        "Gener. AG, sel. SPEA2",
        "Gener. AG, sel. NTGA2",
        "Koew. AG, sel. hypervolume",
        "Koew. AG, sel. NSGA-II",
        "Koew. AG, sel. SPEA2",
        "Koew. AG, sel. NTGA2",
    )
    private var algorithmOutcomes: List<List<SimulationOutcome>>? = null

    override fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset {
        this.algorithmOutcomes = algorithmOutcomes
        if (algorithmOutcomes.size != 8) {
            throw Exception(
                "This chart drawer supports 8 algorithm outcomes"
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
        return dataset
    }

    override fun setSeriesRendering(chart: JFreeChart, plot: XYPlot) {
        for (i in 0 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i, seriesColors[i % 4])
        }
        for (i in 0 until plot.getDataset(0).seriesCount / 2 - 1) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(i, shapes[1])
        }
        for (i in plot.getDataset(0).seriesCount / 2 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(i, shapes[0])
        }
        val chartinfo = TextTitle(buildString {
            for (i in 0 until 8) {
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
