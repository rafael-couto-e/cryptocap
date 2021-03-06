package br.eti.rafaelcouto.cryptocap.data.model

data class CryptoItem(
    val id: Long,
    val name: String,
    val symbol: String,
    val quote: Quote
)
