package sk.o2.scratchcard.domain.model

enum class ScratchCardState {
    UNSCRATCHED,
    SCRATCHED,
    ACTIVATED
}

data class ScratchCard(
    val state: ScratchCardState = ScratchCardState.UNSCRATCHED,
    val code: String? = null
)
