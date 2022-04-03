package output

import simulation.SimulationOutcome
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.chart.block.BlockBorder
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.chart.title.TextTitle
import org.jfree.data.xy.XYDataset
import java.awt.Color
import java.awt.EventQueue
import java.awt.Font
import java.io.File
import javax.swing.BorderFactory
import javax.swing.JFrame

abstract class ChartDrawer(private val chartTitle: String) : JFrame() {

    fun drawChart(
        algorithmOutcomes: List<List<SimulationOutcome>>
    ) {
        EventQueue.invokeLater {
            val chartPanel = prepareChart(algorithmOutcomes, false)
            add(chartPanel)
            pack()
            setLocationRelativeTo(null)
            defaultCloseOperation = EXIT_ON_CLOSE
            isVisible = true
        }
    }

    fun saveChart(
        name: String,
        algorithmOutcomes: List<List<SimulationOutcome>>
    ) {
        val chartPanel = prepareChart(algorithmOutcomes, false)
        ChartUtilities.saveChartAsPNG(File("chartresults/$name.png"), chartPanel.chart, 1000, 800)
    }

    abstract fun setSeriesRendering(chart: JFreeChart, plot: XYPlot)

    protected abstract fun createDataset(algorithmOutcomes: List<List<SimulationOutcome>>): XYDataset

    private fun prepareChart(
        algorithmOutcomes: List<List<SimulationOutcome>>,
        constantAxes: Boolean
    ): ChartPanel {
        val dataset: XYDataset = createDataset(algorithmOutcomes)
        title = chartTitle
        val chart: JFreeChart = createChart(dataset, chartTitle, constantAxes)

        val chartPanel = ChartPanel(chart)
        chartPanel.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        chartPanel.background = Color.white
        return chartPanel
    }

    private fun createChart(dataset: XYDataset, title: String, constantAxes: Boolean): JFreeChart {
        val chart = ChartFactory.createXYLineChart(
            title,
            "Risk",
            "Returns",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        )
        val plot = chart.xyPlot
        val renderer = XYLineAndShapeRenderer()
        if(constantAxes) {
            plot.domainAxis.setRange(0.0, 50.0)
            plot.rangeAxis.setRange(0.0, 50.0)
        }
        plot.renderer = renderer
        setSeriesRendering(chart, plot)
        plot.backgroundPaint = Color.white
        plot.isRangeGridlinesVisible = true
        plot.rangeGridlinePaint = Color.BLACK
        plot.isDomainGridlinesVisible = true
        plot.domainGridlinePaint = Color.BLACK
        chart.legend.frame = BlockBorder.NONE
        chart.title = TextTitle(title, Font("Serif", Font.BOLD, 18))
        return chart
    }
}
