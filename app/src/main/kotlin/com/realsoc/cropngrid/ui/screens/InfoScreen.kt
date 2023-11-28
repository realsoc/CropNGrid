package com.realsoc.cropngrid.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.realsoc.cropngrid.R
import com.realsoc.cropngrid.analytics.LocalAnalyticsHelper
import com.realsoc.cropngrid.ui.components.TheZebraSpacer
import com.realsoc.cropngrid.analytics.TrackScreenViewEvent
import com.realsoc.cropngrid.analytics.buttonClick
import com.realsoc.cropngrid.ui.icons.CropNGridIcons
import com.realsoc.cropngrid.ui.icons.FilledGithub
import com.realsoc.cropngrid.ui.icons.FilledMail
import com.realsoc.cropngrid.ui.theme.Blue
import com.realsoc.cropngrid.ui.theme.Lemon
import com.realsoc.cropngrid.viewmodels.InfoUiState
import com.realsoc.cropngrid.viewmodels.InfoViewModel
import org.koin.androidx.compose.koinViewModel

const val GITHUB_ADDRESS = "https://github.com/realsoc/CropNGrid"
const val MAIL_ADDRESS = "threelittledev@gmail.com"
private const val SCREEN_NAME = "info"

@Composable
fun InfoRoute(
    coroutineScope: CoroutineScope,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: InfoViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val analyticsHelper = LocalAnalyticsHelper.current

    val uiState by viewModel.infoUiState.collectAsState()

    val githubIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(GITHUB_ADDRESS)
    }

    val mailIntent = Intent(ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(MAIL_ADDRESS))
    }

    val noMailAppString = stringResource(id = R.string.no_mail_app)

    val onSourceCodeClick = {
        analyticsHelper.buttonClick(SCREEN_NAME, "github")
        context.startActivity(githubIntent)
    }

    val onMailClick: () -> Unit = {
        analyticsHelper.buttonClick(SCREEN_NAME, "mail")
        try {
            context.startActivity(mailIntent)
        } catch (e: ActivityNotFoundException) {
            coroutineScope.launch {
                onShowSnackbar(noMailAppString, null)
            }
        }
    }

    val primaryColor = if (!isSystemInDarkTheme()) Lemon else MaterialTheme.colorScheme.primary
    val backgroundColor = if (!isSystemInDarkTheme()) Blue else MaterialTheme.colorScheme.surface
    InfoScreen(
        uiState,
        viewModel::onLogGranted,
        onSourceCodeClick,
        onMailClick,
        primaryColor = primaryColor,
        backgroundColor = backgroundColor,
        modifier
    )
}

@Composable
fun InfoScreen(
    uiState: InfoUiState,
    onLogGranted: (Boolean) -> Unit,
    onSourceCodeClick: () -> Unit,
    onMailClick: () -> Unit,
    primaryColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    TrackScreenViewEvent(screenName = SCREEN_NAME)

    val cardBackgroundColor = Color.White.copy(alpha = 0.4f)

    val logGranted = (uiState as? InfoUiState.Success)?.logGranted ?: false

    Box(
        modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.logo_crop),
                contentDescription = "Application logo",
                tint = primaryColor,
                modifier = Modifier
                    .width(100.dp)
                    .padding(top = 32.dp)
            )
            Column(
                Modifier
                    .padding(24.dp)
                    .border(
                        width = 1.dp,
                        color = primaryColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.sending_data),
                                color = primaryColor,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Switch(
                                checked = logGranted,
                                onCheckedChange = onLogGranted,
                                colors = SwitchDefaults.colors(
                                    checkedBorderColor = primaryColor,
                                    checkedThumbColor = backgroundColor,
                                    checkedTrackColor = primaryColor,
                                    uncheckedBorderColor = Color.Gray,
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = backgroundColor
                                )
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.data_description),
                            color = primaryColor
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    TheZebraSpacer(
                        color = primaryColor,
                        modifier = Modifier.fillMaxWidth().height(20.dp)
                    )

                    Spacer(Modifier.height(16.dp))
                    Column {
                        Text(
                            stringResource(R.string.about_app),
                            color = primaryColor,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
                        ) {
                            val annotatedAppDescription = buildAnnotatedString {
                                append(stringResource(id = R.string.app_description_first_part) + " ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(stringResource(id = R.string.app_description_bold_second_part))
                                }
                                append(stringResource(id = R.string.app_description_third_part))
                            }
                            Text(
                                annotatedAppDescription,
                                modifier = Modifier.padding(16.dp),
                                color = primaryColor
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(Modifier.align(Alignment.CenterHorizontally)) {
                            IconButton(
                                onClick = onMailClick,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Icon(
                                    imageVector = CropNGridIcons.FilledMail,
                                    contentDescription = "Mail icon button",
                                    modifier = Modifier
                                        .padding(10.dp),
                                    tint = primaryColor
                                )
                            }
                            Spacer(Modifier.width(32.dp))
                            IconButton(
                                onClick = onSourceCodeClick,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Icon(
                                    imageVector = CropNGridIcons.FilledGithub,
                                    contentDescription = "Github icon button",
                                    modifier = Modifier
                                        .padding(10.dp),
                                    tint = primaryColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
