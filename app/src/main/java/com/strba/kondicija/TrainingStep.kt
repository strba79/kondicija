package com.strba.kondicija

data class TrainingStep(val type: StepType, val duration: Long)

enum class StepType {
    PREPARE, WORK, REST, END
}