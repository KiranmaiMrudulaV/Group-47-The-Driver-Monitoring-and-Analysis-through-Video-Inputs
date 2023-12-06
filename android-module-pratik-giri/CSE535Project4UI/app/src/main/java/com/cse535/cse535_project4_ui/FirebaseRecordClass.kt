package com.cse535.cse535_project4_ui

data class FirebaseRecordClass(
    val UUID: String? = null,
    val distraction: String? = null,
    val emotion: String? = null,
    val workload: String? = null,
    val distractionProbability: Double? = null,
    val drowsinessProbability: Double? = null,
    val hr: Double? = null,
    val rr: Double? = null,
    val workloadProbability: Double? = null
)