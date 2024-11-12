import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import br.com.maeds.financeirocontrole.model.Financa
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.Color as ComposeColor

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChartScreen(financas: List<Financa>, tipo: String) {
    if (financas.isEmpty()) return

    val financasFiltradas = financas.filter { f -> f.tipo == tipo }

    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var selectedYear by remember { mutableStateOf(LocalDate.now().year) }

    val months = (1..12).toList()
    val years = (2020..LocalDate.now().year).toList()

    val filteredFinancas = financasFiltradas.filter { financa ->
        val fIn = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = financa.data
        val formatedDate = LocalDate.parse(date, fIn)
        formatedDate.monthValue == selectedMonth && formatedDate.year == selectedYear
    }

    val groupedData = filteredFinancas.groupBy { it.categoria }
        .mapValues { (_, transactions) -> transactions.sumOf { it.valor } }
        .filter { it.value != 0.0 }

    val totalValue = groupedData.values.sum()

    val entries = groupedData.map { (category, total) ->
        PieEntry(total.toFloat(), category)
    }

    val dataSet = remember(entries) { PieDataSet(entries, "") }
    dataSet.colors = getChartColors()
    dataSet.sliceSpace = 2f
    dataSet.valueTextSize = 12f
    dataSet.valueTextColor = Color.BLACK

    val pieData = remember(entries) { PieData(dataSet) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropdownMenuField(
                label = "Mês",
                options = months,
                selectedOption = selectedMonth,
                onOptionSelected = { selectedMonth = it }
            )

            DropdownMenuField(
                label = "Ano",
                options = years,
                selectedOption = selectedYear,
                onOptionSelected = { selectedYear = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${tipo}s por categorias",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            factory = { ctx ->
                PieChart(ctx).apply {
                    data = pieData
                    description.isEnabled = false
                    isDrawHoleEnabled = true
                    setUsePercentValues(true)
                    setEntryLabelTextSize(12f)
                    setEntryLabelColor(Color.BLACK)
                    animateY(1000)
                    invalidate()
                }
            },
            update = { pieChart ->
                pieChart.data = pieData
                pieChart.invalidate()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            groupedData.forEach { (category, total) ->
                item {
                    val percentage = if (totalValue != 0.0) (total / totalValue) * 100 else 0.0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = category, fontSize = 16.sp)
                        Text(
                            text = String.format("%.2f%%", percentage),
                            fontSize = 16.sp,
                            color = ComposeColor.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuField(
    label: String,
    options: List<Int>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(
            text = "$label: $selectedOption",
            modifier = Modifier
                .padding(8.dp)
                .clickable { expanded = true },
            fontSize = 16.sp
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(text = option.toString())
                }
            }
        }
    }
}

private fun getChartColors(): List<Int> {
    return listOf(
        Color.rgb(244, 67, 54),    // Vermelho
        Color.rgb(33, 150, 243),   // Azul
        Color.rgb(76, 175, 80),    // Verde
        Color.rgb(255, 235, 59),   // Amarelo
        Color.rgb(156, 39, 176),   // Roxo
        Color.rgb(255, 152, 0),    // Laranja
        Color.rgb(0, 188, 212),    // Azul Claro
        Color.rgb(139, 195, 74),   // Verde Claro
        Color.rgb(233, 30, 99),    // Rosa
        Color.rgb(121, 85, 72),    // Marrom
        Color.rgb(63, 81, 181)     // Índigo
    )
}
