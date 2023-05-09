package com.example.plantea.dominio

data class JsonPictogramaItem(
    val _id: Int,
    val aac: Boolean,
    val aacColor: Boolean,
    val categories: List<String>,
    val created: String,
    val desc: String,
    val downloads: Int,
    val hair: Boolean,
    val keywords: List<Keyword>,
    val lastUpdated: String,
    val schematic: Boolean,
    val score: Double,
    val sex: Boolean,
    val skin: Boolean,
    val synsets: List<String>,
    val tags: List<String>,
    val violence: Boolean
)