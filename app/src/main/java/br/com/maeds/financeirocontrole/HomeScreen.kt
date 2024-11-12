package br.com.maeds.financeirocontrole

import ChartScreen
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.maeds.financeirocontrole.model.Financa
import br.com.maeds.financeirocontrole.repository.FinancasRepository
import com.example.financas.ui.theme.BackgroundColor
import com.example.financas.ui.theme.GreenPrimary
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    // Estado das variáveis
    var financas by remember { mutableStateOf<List<Financa>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }
    val financasRepository = FinancasRepository()
    var currentScreen by remember { mutableStateOf("home") }

    val saldoTotal = financas.sumOf {
        if (it.tipo == "Receita") it.valor else -it.valor
    }

    LaunchedEffect(Unit) {
        financasRepository.listarTodos().collect {
            financas = it
        }
    }

    val meses = listOf(
        "Janeiro",
        "Fevereiro",
        "Março",
        "Abril",
        "Maio",
        "Junho",
        "Julho",
        "Agosto",
        "Setembro",
        "Outubro",
        "Novembro",
        "Dezembro"
    )
    val anos = (2020..LocalDate.now().year).toList().map { a -> a.toString() }

    val filteredFinancas = financas.filter { transaction ->
        (selectedMonth.isEmpty() || transaction.data.contains("/${meses.indexOf(selectedMonth) + 1}/")) &&
                (selectedYear.isEmpty() || transaction.data.endsWith("/$selectedYear"))
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(onNavigateToChart = { currentScreen = "chart" },
                onNavigateToRightChart = { currentScreen = "rightChart" }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentScreen == "home") {
                        showDialog = true
                    } else {
                        currentScreen = "home"
                    }
                },
                backgroundColor = GreenPrimary,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = if (currentScreen == "home") Icons.Default.Add else Icons.Default.Home,
                    contentDescription = if (currentScreen == "home") "Add" else "Home",
                    tint = Color.White
                )
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            when (currentScreen) {
                "home" -> {
                    Text(
                        text = "Saldo: R$ ${"%.2f".format(saldoTotal)}",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = if (saldoTotal >= 0) Color(0xFF388E3C) else Color(0xFFD32F2F),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DropdownMenuWithLabel(
                            label = "Mês",
                            options = meses,
                            selectedOption = selectedMonth,
                            onOptionSelected = { selectedMonth = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DropdownMenuWithLabel(
                            label = "Ano",
                            options = anos,
                            selectedOption = selectedYear,
                            onOptionSelected = { selectedYear = it }
                        )
                    }
                    TransactionList(transactions = filteredFinancas)
                }

                "chart" -> ChartScreen(financas, "Receita")
                "rightChart" -> ChartScreen(financas, "Despesa")
            }
        }

        if (showDialog) {
            AddTransactionDialog(onDismiss = { showDialog = false })
        }
    }
}


@Composable
fun TransactionList(transactions: List<Financa>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        items(transactions) { transaction ->
            TransactionItem(transaction)
        }
    }
}

@Composable
fun TransactionItem(transaction: Financa) {
    val backgroundColor =
        if (transaction.tipo == "Receita") Color(0xFF4CAF50) else Color(0xFFF44336)
    val textColor = Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(8.dp, shape = MaterialTheme.shapes.small), // Aumentar a sombra
        shape = MaterialTheme.shapes.small, // Bordas mais arredondadas
        backgroundColor = backgroundColor,
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Money,
                contentDescription = "Transaction Icon",
                tint = textColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = transaction.categoria, color = textColor, fontWeight = FontWeight.Bold)
                Text(text = "R$ ${"%.2f".format(transaction.valor)}", color = textColor)
                Text(text = "Data: ${transaction.data}", color = textColor)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    onNavigateToChart: () -> Unit,
    onNavigateToRightChart: () -> Unit
) {
    BottomAppBar(
        backgroundColor = GreenPrimary,
        cutoutShape = MaterialTheme.shapes.small
    ) {
        IconButton(onClick = onNavigateToChart) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = "Gráficos Receitas",
                tint = Color.White
            )
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(onClick = onNavigateToRightChart) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = "Gráficos Despesas",
                tint = Color.White
            )
        }
    }
}


@Composable
fun RadioButtonWithText(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
        )
        Text(text)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropdownMenuWithLabel(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(
            text = "$label: $selectedOption",
            modifier = Modifier
                .padding(16.dp)
                .clickable { expanded = true },
            fontSize = 16.sp
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(text = option, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
