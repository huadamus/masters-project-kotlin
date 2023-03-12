package output

import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import simulation.SimulationOutcome


class ConfigurationChartDrawer(private val purityValues: Array<Double>, private val timeValues: Array<Int>) :
    ChartDrawer("Long investment period experiment") {
    private val labels = arrayOf(
        "EA-HV",
        "NSGA-II",
        "SPEA2",
        "NTGA2",
        "MOEA/D",
    )
    private var algorithmOutcomes: List<List<SimulationOutcome>>? = null

    override fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset {
        this.algorithmOutcomes = algorithmOutcomes
        if (algorithmOutcomes.size != 5) {
            throw Exception(
                "This chart drawer supports 5 algorithm outcomes"
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
        val moeaDSeries = XYSeries(labels[4])
        for (outcome in algorithmOutcomes[4]) {
            moeaDSeries.add(outcome.risk, outcome.profits)
        }
        dataset.addSeries(moeaDSeries)
        return dataset
    }

    override fun setSeriesRendering(chart: JFreeChart, plot: XYPlot) {
        for (i in 0 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i, seriesColors[i % 5])
        }
        for (i in 0 until plot.getDataset(0).seriesCount / 2) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(i, shapes[2])
        }
        for (i in plot.getDataset(0).seriesCount / 2 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(i, shapes[3])
        }
    }
}
