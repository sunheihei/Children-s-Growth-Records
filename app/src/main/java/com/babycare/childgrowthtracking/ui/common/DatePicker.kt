package com.babycare.childgrowthtracking.ui.common

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.babycare.childgrowthtracking.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonDatePickerDialog(
    selectedDate: Long,
    confirmButton: (millis: Long) -> Unit,
    cancelButtonClick: () -> Unit,
    onDismissRequest: () -> Unit
) {

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )
    DatePickerDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = {
            datePickerState.selectedDateMillis?.let { millis ->
                confirmButton.invoke(millis)
            }
        }) {
            Text(stringResource(R.string.ok))
        }
    }, dismissButton = {
        TextButton(onClick = cancelButtonClick) {
            Text(stringResource(R.string.cancel))
        }
    }) {
        DatePicker(
            state = datePickerState, modifier = Modifier.padding(16.dp)
        )
    }
}
