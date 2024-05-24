package com.example.calllog

data class CallLogEntry(
    val number: String,
    val type: Int,
    val date: Long,
    val duration: Long
)