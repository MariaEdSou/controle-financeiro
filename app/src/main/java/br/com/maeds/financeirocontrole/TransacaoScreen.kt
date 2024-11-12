package br.com.maeds.financeirocontrole

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.maeds.financeirocontrole.repository.FinancasRepository
import com.example.financas.ui.theme.GreenPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddTransactionDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Adicionar Transação", style = MaterialTheme.typography.h6)

                Spacer(modifier = Modifier.height(16.dp))

                var transactionType by remember { mutableStateOf("Despesa") }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    RadioButtonWithText(
                        text = "Despesa",
                        selected = transactionType == "Despesa",
                        onClick = { transactionType = "Despesa" }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButtonWithText(
                        text = "Receita",
                        selected = transactionType == "Receita",
                        onClick = { transactionType = "Receita" }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                var amount by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Valor") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                var category by remember { mutableStateOf("") }
                val categories = if (transactionType == "Receita") {
                    listOf("Cílios", "Sobrancelha", "Micropigmentação", "Rena")
                } else {
                    listOf("Materiais", "Energia", "Água","Material / Cilios",
                        "Mat / Cola para Cilios","Mat / Pinsa",
                        "Mat / Agulha", "Mat / Algodao", "Mat / Pigmento", "Mat / Rena",
                        "Manutenção do Salão")
                }

                DropdownMenuWithLabelCategoria(
                    label = "Categoria",
                    options = categories,
                    selectedOption = category,
                    onOptionSelected = { category = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                val context = LocalContext.current
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                var selectedDate by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("Data") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            DatePickerDialog(
                                context,
                                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                                    calendar.set(year, month, dayOfMonth)
                                    selectedDate = dateFormat.format(calendar.time)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val financeRepository = FinancasRepository()
                            financeRepository.salvar(
                                transactionType,
                                amount.toDouble(),
                                selectedDate,
                                category
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = GreenPrimary)
                    ) {
                        Text("Salvar", color = Color.White)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropdownMenuWithLabelCategoria(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            },
            readOnly = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                ) {
                    Text(option)
                }
            }
        }
    }
}