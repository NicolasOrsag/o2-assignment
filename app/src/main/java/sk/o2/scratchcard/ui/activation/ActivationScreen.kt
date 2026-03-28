package sk.o2.scratchcard.ui.activation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sk.o2.scratchcard.domain.model.ScratchCardState

@Composable
fun ActivationScreen(
    onBack: () -> Unit,
    viewModel: ActivationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val card by viewModel.scratchCard.collectAsStateWithLifecycle()

    if (uiState is ActivationUiState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("Activation Error") },
            text = { Text((uiState as ActivationUiState.Error).message) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text("OK")
                }
            }
        )
    }

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
                text = "Activation Screen",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (card.state) {
                    ScratchCardState.UNSCRATCHED -> "Card has not been scratched yet."
                    ScratchCardState.SCRATCHED -> "Code: ${card.code}"
                    ScratchCardState.ACTIVATED -> "Card is activated!"
                },
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (uiState) {
                is ActivationUiState.Idle, is ActivationUiState.Error -> {
                    Button(
                        onClick = { viewModel.activate() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = card.state == ScratchCardState.SCRATCHED
                    ) {
                        Text("Activate Card")
                    }
                }

                is ActivationUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Activating...")
                }

                is ActivationUiState.Success -> {
                    Text(
                        text = "Card activated successfully!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
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
