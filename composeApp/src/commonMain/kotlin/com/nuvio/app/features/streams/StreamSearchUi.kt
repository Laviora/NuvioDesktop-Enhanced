package com.nuvio.app.features.streams

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nuvio.app.core.ui.NuvioInputField
import com.nuvio.app.core.ui.nuvio
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.streams_search_clear
import nuvio.composeapp.generated.resources.streams_search_no_results
import nuvio.composeapp.generated.resources.streams_search_placeholder
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun StreamSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NuvioInputField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = stringResource(Res.string.streams_search_placeholder),
        modifier = modifier,
        trailingContent = if (query.isEmpty()) null else {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(Res.string.streams_search_clear),
                        tint = MaterialTheme.nuvio.colors.textMuted,
                    )
                }
            }
        },
    )
}

@Composable
internal fun StreamSearchEmptyBlock(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.streams_search_no_results),
            color = MaterialTheme.nuvio.colors.textMuted,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp),
        )
    }
}
