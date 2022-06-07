package output

import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import simulation.SimulationOutcome


class ConfigurationTestChartDrawer(private val purityValues: Array<Double>, private val timeValues: Array<Int>) :
    ChartDrawer("Badanie konfiguracji systemu") {
    private val labels = arrayOf(
        "NTGA2",
        "cNTGA2",
    )
    private var algorithmOutcomes: List<List<SimulationOutcome>>? = null

    override fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset {
        this.algorithmOutcomes = algorithmOutcomes
        if (algorithmOutcomes.size != 2) {
            throw Exception(
                "This chart drawer supports 2 algorithm outcomes"
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
        return dataset
    }

    override fun setSeriesRendering(chart: JFreeChart, plot: XYPlot) {
        for (i in 0 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(i, seriesColors[i % 2])
        }
        for (i in 0 until plot.getDataset(0).seriesCount / 2) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(i, shapes[2])
        }
        for (i in plot.getDataset(0).seriesCount / 2 until plot.getDataset(0).seriesCount) {
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(i, shapes[3])
        }
    }
}
