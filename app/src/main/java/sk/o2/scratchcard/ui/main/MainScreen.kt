package sk.o2.scratchcard.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun MainScreen(
    onNavigateToScratch: () -> Unit,
    onNavigateToActivation: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val card by viewModel.scratchCard.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when (card.state) {
                        ScratchCardState.UNSCRATCHED -> MaterialTheme.colorScheme.surfaceVariant
                        ScratchCardState.SCRATCHED -> MaterialTheme.colorScheme.primaryContainer
                        ScratchCardState.ACTIVATED -> MaterialTheme.colorScheme.tertiaryContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scratch Card",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (card.state) {
                            ScratchCardState.UNSCRATCHED -> "Unscratched"
                            ScratchCardState.SCRATCHED -> "Scratched"
                            ScratchCardState.ACTIVATED -> "Activated"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = when (card.state) {
                            ScratchCardState.UNSCRATCHED -> MaterialTheme.colorScheme.onSurfaceVariant
                            ScratchCardState.SCRATCHED -> MaterialTheme.colorScheme.onPrimaryContainer
                            ScratchCardState.ACTIVATED -> MaterialTheme.colorScheme.onTertiaryContainer
                        }
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

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigateToScratch,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Scratch Screen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToActivation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Activation Screen")
            }
        }
    }
}
