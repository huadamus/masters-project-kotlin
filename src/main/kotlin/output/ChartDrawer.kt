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
import org.jfree.util.ShapeUtilities
import java.awt.Color
import java.awt.EventQueue
import java.awt.Font
import java.awt.Rectangle
import java.awt.geom.Ellipse2D
import java.io.File
import javax.swing.BorderFactory
import javax.swing.JFrame

abstract class ChartDrawer(private val chartTitle: String) : JFrame() {
    protected val classicalStrategiesColor = Color(0, 0, 0)
    protected val seriesColors = listOf(
        Color(255, 0, 0),
        Color(0, 170, 0),
        Color(0, 0, 255),
        Color(255, 0, 205),
        Color(100, 20, 150),
        Color(0, 255, 255),
    )
    val shapes = listOf(
        Ellipse2D.Double(-2.0, -2.0, 4.0, 4.0),
        Rectangle(-3, -3, 6, 6),
        ShapeUtilities.createRegularCross(4.0f, 0.5f),
        ShapeUtilities.createUpTriangle(2.0f)
    )

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

        chartPanel.chart.xyPlot.domainAxis.setRange(0.0, 10.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}1a.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(10.0, 20.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}1b.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(20.0, 30.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}1c.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(30.0, 40.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}1d.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(40.0, 50.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}1e.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(50.0, 60.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}1f.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(60.0, 70.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}1g.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(70.0, 80.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}1h.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(80.0, 90.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}1i.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(90.0, 100.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}1j.png"), chartPanel.chart, 1600, 1200)

        chartPanel.chart.xyPlot.domainAxis.setRange(0.0, 20.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}2a.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(20.0, 40.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}2b.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(40.0, 60.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}2c.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(60.0, 80.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}2d.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(80.0, 100.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}2e.png"), chartPanel.chart, 1600, 1200)

        chartPanel.chart.xyPlot.domainAxis.setRange(0.0, 30.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}3a.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(30.0, 60.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}3b.png"), chartPanel.chart, 1600, 1200)
        chartPanel.chart.xyPlot.domainAxis.setRange(60.0, 90.0)
        ChartUtilities.saveChartAsPNG(File("results/${name}3c.png"), chartPanel.chart, 1600, 1200)
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
            "Greatest fall of total value monthly [%]",//"Największy spadek z miesiąca na miesiąc [%]",
            "Yearly average returns",//"Średnioroczny zwrot [%]",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        )
        val plot = chart.xyPlot
        val renderer = XYLineAndShapeRenderer()
        if (constantAxes) {
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
