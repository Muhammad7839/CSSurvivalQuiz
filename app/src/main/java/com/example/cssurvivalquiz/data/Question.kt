package com.example.cssurvivalquiz.data

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correct: Set<Int>,   // indexes of correct options
    val multiple: Boolean,   // false = single choice, true = multi-answer
    val fact: String         // one-line “why”
)