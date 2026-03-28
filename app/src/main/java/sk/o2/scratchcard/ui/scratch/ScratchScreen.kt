package sk.o2.scratchcard.ui.scratch

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sk.o2.scratchcard.domain.model.ScratchCardState

@Composable
fun ScratchScreen(
    onBack: () -> Unit,
    viewModel: ScratchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val card by viewModel.scratchCard.collectAsStateWithLifecycle()

    // Back handler: navigating back will pop the backstack,
    // which destroys this composable's ViewModel, cancelling viewModelScope.
    BackHandler { onBack() }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Scratch Screen",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (val state = uiState) {
                is ScratchUiState.Idle -> {
                    if (card.state == ScratchCardState.UNSCRATCHED) {
                        Button(
                            onClick = { viewModel.scratch() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Scratch Card")
                        }
                    } else {
                        Text(
                            text = "Card has already been scratched.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (card.code != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Code: ${card.code}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                is ScratchUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Scratching card...")
                }

                is ScratchUiState.Success -> {
                    Text(
                        text = "Card scratched!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Code: ${state.code}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is ScratchUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
