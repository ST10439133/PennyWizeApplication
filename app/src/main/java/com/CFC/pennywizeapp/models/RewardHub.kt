package com.CFC.pennywizeapp.models

data class RewardHub(
    val isSpinAvailable: Boolean,
    val lastSpinTimestamp: Long,
    val rewardHistory: List<Reward>
)

data class Reward(
    val type: String, // e.g., "Badge", "Points"
    val winMessage: String // e.g., "You won a..."
)