package com.pranayam.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.pranayam.app.ui.theme.*

@Composable
fun PranayamTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = PranayamTypography.LabelMedium,
            color = if (isError) PranayamColors.Error else PranayamColors.TextSecondaryLight,
            modifier = Modifier.padding(bottom = Spacing.XXS)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = PranayamTypography.BodyMedium,
                    color = PranayamColors.TextTertiaryLight
                )
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = PranayamColors.TextSecondaryLight
                    )
                }
            },
            trailingIcon = trailingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = PranayamColors.TextSecondaryLight
                    )
                }
            },
            isError = isError,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            textStyle = PranayamTypography.BodyMedium,
            shape = RoundedCornerShape(CornerRadius.M),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PranayamColors.RosePrimary,
                unfocusedBorderColor = PranayamColors.DividerLight,
                errorBorderColor = PranayamColors.Error,
                focusedLabelColor = PranayamColors.RosePrimary,
                unfocusedLabelColor = PranayamColors.TextSecondaryLight
            )
        )
        
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = PranayamTypography.BodySmall,
                color = PranayamColors.Error,
                modifier = Modifier.padding(top = Spacing.XXS)
            )
        }
    }
}
