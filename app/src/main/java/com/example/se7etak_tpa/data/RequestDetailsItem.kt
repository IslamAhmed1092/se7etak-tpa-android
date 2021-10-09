package com.example.se7etak_tpa.data

data class RequestDetailsItem(
    val comment: String,
    val date: String,
    val listOfDocuments: List<String>,
    val providerName: String,
    val providerType: String,
    val state: String,
    val type: String
)