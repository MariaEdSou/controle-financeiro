package br.com.maeds.financeirocontrole.model

import java.math.BigDecimal

data class Financa(
    var id: String = "",
    var tipo: String = "",
    var valor: Double = .0,
    var data: String = "",
    var categoria: String = ""
)
