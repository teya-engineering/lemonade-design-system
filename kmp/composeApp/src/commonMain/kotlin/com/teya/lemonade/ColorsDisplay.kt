@file:OptIn(InternalLemonadeApi::class)

package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.teya.lemonade.LemonadePrimitiveColors.Alpha
import com.teya.lemonade.LemonadePrimitiveColors.Solid

@Composable
internal fun ColorsDisplay() {
    val shadowColor = LemonadeTheme.colors.shadow.shadowDefault
    val onLightColor = LemonadeTheme.colors.content.contentPrimary
    val onDarkColor = LemonadeTheme.colors.content.contentPrimaryInverse

    val allColors = remember(shadowColor) {
        allColorsLight + ColorData(
            title = "Shadow",
            items = mapOf("shadowDefault" to shadowColor),
        )
    }

    SampleScreenDisplayLazyColumn(title = "Colors") {
        items(
            items = allColors,
            key = { colorData -> colorData.title },
        ) { colorData ->
            ColorRampRow(
                colorData = colorData,
                onLightColor = onLightColor,
                onDarkColor = onDarkColor,
            )
        }
    }
}

@Composable
private fun ColorRampRow(
    colorData: ColorData,
    onLightColor: Color,
    onDarkColor: Color,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing100),
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing400),
    ) {
        LemonadeUi.Text(
            text = colorData.title,
            textStyle = LemonadeTheme.typography.bodyXSmallOverline,
            color = LemonadeTheme.colors.content.contentTertiary,
        )
        Row(
            modifier = Modifier.clip(shape = RoundedCornerShape(size = LemonadeTheme.radius.radius300)),
        ) {
            colorData.items.forEach { item ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(weight = 1f)
                        .height(height = LemonadeTheme.sizes.size1200)
                        .background(color = item.value),
                ) {
                    LemonadeUi.Text(
                        text = item.key,
                        textStyle = LemonadeTheme.typography.bodyXSmallSemiBold,
                        color = textColorFor(
                            backgroundColor = item.value,
                            onLightColor = onLightColor,
                            onDarkColor = onDarkColor,
                        ),
                    )
                }
            }
        }
    }
}

private val allColorsLight = listOf(
    ColorData(
        title = "Yellow",
        items = mapOf(
            "50" to Solid.Yellow.yellow50,
            "100" to Solid.Yellow.yellow100,
            "200" to Solid.Yellow.yellow200,
            "300" to Solid.Yellow.yellow300,
            "400" to Solid.Yellow.yellow400,
            "500" to Solid.Yellow.yellow500,
            "600" to Solid.Yellow.yellow600,
            "700" to Solid.Yellow.yellow700,
            "800" to Solid.Yellow.yellow800,
            "900" to Solid.Yellow.yellow900,
            "950" to Solid.Yellow.yellow950,
        ),
    ),
    ColorData(
        title = "Yellow Alpha",
        items = mapOf(
            "50" to Alpha.Yellow.alpha50,
            "100" to Alpha.Yellow.alpha100,
            "200" to Alpha.Yellow.alpha200,
            "300" to Alpha.Yellow.alpha300,
            "400" to Alpha.Yellow.alpha400,
            "500" to Alpha.Yellow.alpha500,
            "600" to Alpha.Yellow.alpha600,
            "700" to Alpha.Yellow.alpha700,
            "800" to Alpha.Yellow.alpha800,
            "900" to Alpha.Yellow.alpha900,
            "950" to Alpha.Yellow.alpha950,
        ),
    ),
    ColorData(
        title = "Amber",
        items = mapOf(
            "50" to Solid.Amber.amber50,
            "100" to Solid.Amber.amber100,
            "200" to Solid.Amber.amber200,
            "300" to Solid.Amber.amber300,
            "400" to Solid.Amber.amber400,
            "500" to Solid.Amber.amber500,
            "600" to Solid.Amber.amber600,
            "700" to Solid.Amber.amber700,
            "800" to Solid.Amber.amber800,
            "900" to Solid.Amber.amber900,
            "950" to Solid.Amber.amber950,
        ),
    ),
    ColorData(
        title = "Amber Alpha",
        items = mapOf(
            "50" to Alpha.Amber.alpha50,
            "100" to Alpha.Amber.alpha100,
            "200" to Alpha.Amber.alpha200,
            "300" to Alpha.Amber.alpha300,
            "400" to Alpha.Amber.alpha400,
            "500" to Alpha.Amber.alpha500,
            "600" to Alpha.Amber.alpha600,
            "700" to Alpha.Amber.alpha700,
            "800" to Alpha.Amber.alpha800,
            "900" to Alpha.Amber.alpha900,
            "950" to Alpha.Amber.alpha950,
        ),
    ),
    ColorData(
        title = "Orange",
        items = mapOf(
            "50" to Solid.Orange.orange50,
            "100" to Solid.Orange.orange100,
            "200" to Solid.Orange.orange200,
            "300" to Solid.Orange.orange300,
            "400" to Solid.Orange.orange400,
            "500" to Solid.Orange.orange500,
            "600" to Solid.Orange.orange600,
            "700" to Solid.Orange.orange700,
            "800" to Solid.Orange.orange800,
            "900" to Solid.Orange.orange900,
            "950" to Solid.Orange.orange950,
        ),
    ),
    ColorData(
        title = "Orange Alpha",
        items = mapOf(
            "50" to Alpha.Orange.alpha50,
            "100" to Alpha.Orange.alpha100,
            "200" to Alpha.Orange.alpha200,
            "300" to Alpha.Orange.alpha300,
            "400" to Alpha.Orange.alpha400,
            "500" to Alpha.Orange.alpha500,
            "600" to Alpha.Orange.alpha600,
            "700" to Alpha.Orange.alpha700,
            "800" to Alpha.Orange.alpha800,
            "900" to Alpha.Orange.alpha900,
            "950" to Alpha.Orange.alpha950,
        ),
    ),
    ColorData(
        title = "Red",
        items = mapOf(
            "50" to Solid.Red.red50,
            "100" to Solid.Red.red100,
            "200" to Solid.Red.red200,
            "300" to Solid.Red.red300,
            "400" to Solid.Red.red400,
            "500" to Solid.Red.red500,
            "600" to Solid.Red.red600,
            "700" to Solid.Red.red700,
            "800" to Solid.Red.red800,
            "900" to Solid.Red.red900,
            "950" to Solid.Red.red950,
        ),
    ),
    ColorData(
        title = "Red Alpha",
        items = mapOf(
            "50" to Alpha.Red.alpha50,
            "100" to Alpha.Red.alpha100,
            "200" to Alpha.Red.alpha200,
            "300" to Alpha.Red.alpha300,
            "400" to Alpha.Red.alpha400,
            "500" to Alpha.Red.alpha500,
            "600" to Alpha.Red.alpha600,
            "700" to Alpha.Red.alpha700,
            "800" to Alpha.Red.alpha800,
            "900" to Alpha.Red.alpha900,
            "950" to Alpha.Red.alpha950,
        ),
    ),
    ColorData(
        title = "Rose",
        items = mapOf(
            "50" to Solid.Rose.rose50,
            "100" to Solid.Rose.rose100,
            "200" to Solid.Rose.rose200,
            "300" to Solid.Rose.rose300,
            "400" to Solid.Rose.rose400,
            "500" to Solid.Rose.rose500,
            "600" to Solid.Rose.rose600,
            "700" to Solid.Rose.rose700,
            "800" to Solid.Rose.rose800,
            "900" to Solid.Rose.rose900,
            "950" to Solid.Rose.rose950,
        ),
    ),
    ColorData(
        title = "Rose Alpha",
        items = mapOf(
            "50" to Alpha.Rose.alpha50,
            "100" to Alpha.Rose.alpha100,
            "200" to Alpha.Rose.alpha200,
            "300" to Alpha.Rose.alpha300,
            "400" to Alpha.Rose.alpha400,
            "500" to Alpha.Rose.alpha500,
            "600" to Alpha.Rose.alpha600,
            "700" to Alpha.Rose.alpha700,
            "800" to Alpha.Rose.alpha800,
            "900" to Alpha.Rose.alpha900,
            "950" to Alpha.Rose.alpha950,
        ),
    ),
    ColorData(
        title = "Pink",
        items = mapOf(
            "50" to Solid.Pink.pink50,
            "100" to Solid.Pink.pink100,
            "200" to Solid.Pink.pink200,
            "300" to Solid.Pink.pink300,
            "400" to Solid.Pink.pink400,
            "500" to Solid.Pink.pink500,
            "600" to Solid.Pink.pink600,
            "700" to Solid.Pink.pink700,
            "800" to Solid.Pink.pink800,
            "900" to Solid.Pink.pink900,
            "950" to Solid.Pink.pink950,
        ),
    ),
    ColorData(
        title = "Pink Alpha",
        items = mapOf(
            "50" to Alpha.Pink.alpha50,
            "100" to Alpha.Pink.alpha100,
            "200" to Alpha.Pink.alpha200,
            "300" to Alpha.Pink.alpha300,
            "400" to Alpha.Pink.alpha400,
            "500" to Alpha.Pink.alpha500,
            "600" to Alpha.Pink.alpha600,
            "700" to Alpha.Pink.alpha700,
            "800" to Alpha.Pink.alpha800,
            "900" to Alpha.Pink.alpha900,
            "950" to Alpha.Pink.alpha950,
        ),
    ),
    ColorData(
        title = "Purple",
        items = mapOf(
            "50" to Solid.Purple.purple50,
            "100" to Solid.Purple.purple100,
            "200" to Solid.Purple.purple200,
            "300" to Solid.Purple.purple300,
            "400" to Solid.Purple.purple400,
            "500" to Solid.Purple.purple500,
            "600" to Solid.Purple.purple600,
            "700" to Solid.Purple.purple700,
            "800" to Solid.Purple.purple800,
            "900" to Solid.Purple.purple900,
            "950" to Solid.Purple.purple950,
        ),
    ),
    ColorData(
        title = "Purple Alpha",
        items = mapOf(
            "50" to Alpha.Purple.alpha50,
            "100" to Alpha.Purple.alpha100,
            "200" to Alpha.Purple.alpha200,
            "300" to Alpha.Purple.alpha300,
            "400" to Alpha.Purple.alpha400,
            "500" to Alpha.Purple.alpha500,
            "600" to Alpha.Purple.alpha600,
            "700" to Alpha.Purple.alpha700,
            "800" to Alpha.Purple.alpha800,
            "900" to Alpha.Purple.alpha900,
            "950" to Alpha.Purple.alpha950,
        ),
    ),
    ColorData(
        title = "Violet",
        items = mapOf(
            "50" to Solid.Violet.violet50,
            "100" to Solid.Violet.violet100,
            "200" to Solid.Violet.violet200,
            "300" to Solid.Violet.violet300,
            "400" to Solid.Violet.violet400,
            "500" to Solid.Violet.violet500,
            "600" to Solid.Violet.violet600,
            "700" to Solid.Violet.violet700,
            "800" to Solid.Violet.violet800,
            "900" to Solid.Violet.violet900,
            "950" to Solid.Violet.violet950,
        ),
    ),
    ColorData(
        title = "Violet Alpha",
        items = mapOf(
            "50" to Alpha.Violet.alpha50,
            "100" to Alpha.Violet.alpha100,
            "200" to Alpha.Violet.alpha200,
            "300" to Alpha.Violet.alpha300,
            "400" to Alpha.Violet.alpha400,
            "500" to Alpha.Violet.alpha500,
            "600" to Alpha.Violet.alpha600,
            "700" to Alpha.Violet.alpha700,
            "800" to Alpha.Violet.alpha800,
            "900" to Alpha.Violet.alpha900,
            "950" to Alpha.Violet.alpha950,
        ),
    ),
    ColorData(
        title = "Indigo",
        items = mapOf(
            "50" to Solid.Indigo.indigo50,
            "100" to Solid.Indigo.indigo100,
            "200" to Solid.Indigo.indigo200,
            "300" to Solid.Indigo.indigo300,
            "400" to Solid.Indigo.indigo400,
            "500" to Solid.Indigo.indigo500,
            "600" to Solid.Indigo.indigo600,
            "700" to Solid.Indigo.indigo700,
            "800" to Solid.Indigo.indigo800,
            "900" to Solid.Indigo.indigo900,
            "950" to Solid.Indigo.indigo950,
        ),
    ),
    ColorData(
        title = "Indigo Alpha",
        items = mapOf(
            "50" to Alpha.Indigo.alpha50,
            "100" to Alpha.Indigo.alpha100,
            "200" to Alpha.Indigo.alpha200,
            "300" to Alpha.Indigo.alpha300,
            "400" to Alpha.Indigo.alpha400,
            "500" to Alpha.Indigo.alpha500,
            "600" to Alpha.Indigo.alpha600,
            "700" to Alpha.Indigo.alpha700,
            "800" to Alpha.Indigo.alpha800,
            "900" to Alpha.Indigo.alpha900,
            "950" to Alpha.Indigo.alpha950,
        ),
    ),
    ColorData(
        title = "Blue",
        items = mapOf(
            "50" to Solid.Blue.blue50,
            "100" to Solid.Blue.blue100,
            "200" to Solid.Blue.blue200,
            "300" to Solid.Blue.blue300,
            "400" to Solid.Blue.blue400,
            "500" to Solid.Blue.blue500,
            "600" to Solid.Blue.blue600,
            "700" to Solid.Blue.blue700,
            "800" to Solid.Blue.blue800,
            "900" to Solid.Blue.blue900,
            "950" to Solid.Blue.blue950,
        ),
    ),
    ColorData(
        title = "Blue Alpha",
        items = mapOf(
            "50" to Alpha.Blue.alpha50,
            "100" to Alpha.Blue.alpha100,
            "200" to Alpha.Blue.alpha200,
            "300" to Alpha.Blue.alpha300,
            "400" to Alpha.Blue.alpha400,
            "500" to Alpha.Blue.alpha500,
            "600" to Alpha.Blue.alpha600,
            "700" to Alpha.Blue.alpha700,
            "800" to Alpha.Blue.alpha800,
            "900" to Alpha.Blue.alpha900,
            "950" to Alpha.Blue.alpha950,
        ),
    ),
    ColorData(
        title = "Cyan",
        items = mapOf(
            "50" to Solid.Cyan.cyan50,
            "100" to Solid.Cyan.cyan100,
            "200" to Solid.Cyan.cyan200,
            "300" to Solid.Cyan.cyan300,
            "400" to Solid.Cyan.cyan400,
            "500" to Solid.Cyan.cyan500,
            "600" to Solid.Cyan.cyan600,
            "700" to Solid.Cyan.cyan700,
            "800" to Solid.Cyan.cyan800,
            "900" to Solid.Cyan.cyan900,
            "950" to Solid.Cyan.cyan950,
        ),
    ),
    ColorData(
        title = "Cyan Alpha",
        items = mapOf(
            "50" to Alpha.Cyan.alpha50,
            "100" to Alpha.Cyan.alpha100,
            "200" to Alpha.Cyan.alpha200,
            "300" to Alpha.Cyan.alpha300,
            "400" to Alpha.Cyan.alpha400,
            "500" to Alpha.Cyan.alpha500,
            "600" to Alpha.Cyan.alpha600,
            "700" to Alpha.Cyan.alpha700,
            "800" to Alpha.Cyan.alpha800,
            "900" to Alpha.Cyan.alpha900,
            "950" to Alpha.Cyan.alpha950,
        ),
    ),
    ColorData(
        title = "Teal",
        items = mapOf(
            "50" to Solid.Teal.teal50,
            "100" to Solid.Teal.teal100,
            "200" to Solid.Teal.teal200,
            "300" to Solid.Teal.teal300,
            "400" to Solid.Teal.teal400,
            "500" to Solid.Teal.teal500,
            "600" to Solid.Teal.teal600,
            "700" to Solid.Teal.teal700,
            "800" to Solid.Teal.teal800,
            "900" to Solid.Teal.teal900,
            "950" to Solid.Teal.teal950,
        ),
    ),
    ColorData(
        title = "Teal Alpha",
        items = mapOf(
            "50" to Alpha.Teal.alpha50,
            "100" to Alpha.Teal.alpha100,
            "200" to Alpha.Teal.alpha200,
            "300" to Alpha.Teal.alpha300,
            "400" to Alpha.Teal.alpha400,
            "500" to Alpha.Teal.alpha500,
            "600" to Alpha.Teal.alpha600,
            "700" to Alpha.Teal.alpha700,
            "800" to Alpha.Teal.alpha800,
            "900" to Alpha.Teal.alpha900,
            "950" to Alpha.Teal.alpha950,
        ),
    ),
    ColorData(
        title = "Green",
        items = mapOf(
            "50" to Solid.Green.green50,
            "100" to Solid.Green.green100,
            "200" to Solid.Green.green200,
            "300" to Solid.Green.green300,
            "400" to Solid.Green.green400,
            "500" to Solid.Green.green500,
            "600" to Solid.Green.green600,
            "700" to Solid.Green.green700,
            "800" to Solid.Green.green800,
            "900" to Solid.Green.green900,
            "950" to Solid.Green.green950,
        ),
    ),
    ColorData(
        title = "Green Alpha",
        items = mapOf(
            "50" to Alpha.Green.alpha50,
            "100" to Alpha.Green.alpha100,
            "200" to Alpha.Green.alpha200,
            "300" to Alpha.Green.alpha300,
            "400" to Alpha.Green.alpha400,
            "500" to Alpha.Green.alpha500,
            "600" to Alpha.Green.alpha600,
            "700" to Alpha.Green.alpha700,
            "800" to Alpha.Green.alpha800,
            "900" to Alpha.Green.alpha900,
            "950" to Alpha.Green.alpha950,
        ),
    ),
    ColorData(
        title = "Green-Lime",
        items = mapOf(
            "50" to Solid.GreenLime.greenLime50,
            "100" to Solid.GreenLime.greenLime100,
            "200" to Solid.GreenLime.greenLime200,
            "300" to Solid.GreenLime.greenLime300,
            "400" to Solid.GreenLime.greenLime400,
            "500" to Solid.GreenLime.greenLime500,
            "600" to Solid.GreenLime.greenLime600,
            "700" to Solid.GreenLime.greenLime700,
            "800" to Solid.GreenLime.greenLime800,
            "900" to Solid.GreenLime.greenLime900,
            "950" to Solid.GreenLime.greenLime950,
        ),
    ),
    ColorData(
        title = "Green-Lime Alpha",
        items = mapOf(
            "50" to Alpha.GreenLime.alpha50,
            "100" to Alpha.GreenLime.alpha100,
            "200" to Alpha.GreenLime.alpha200,
            "300" to Alpha.GreenLime.alpha300,
            "400" to Alpha.GreenLime.alpha400,
            "500" to Alpha.GreenLime.alpha500,
            "600" to Alpha.GreenLime.alpha600,
            "700" to Alpha.GreenLime.alpha700,
            "800" to Alpha.GreenLime.alpha800,
            "900" to Alpha.GreenLime.alpha900,
            "950" to Alpha.GreenLime.alpha950,
        ),
    ),
    ColorData(
        title = "Yellow-Lime",
        items = mapOf(
            "50" to Solid.YellowLime.yellowLime50,
            "100" to Solid.YellowLime.yellowLime100,
            "200" to Solid.YellowLime.yellowLime200,
            "300" to Solid.YellowLime.yellowLime300,
            "400" to Solid.YellowLime.yellowLime400,
            "500" to Solid.YellowLime.yellowLime500,
            "600" to Solid.YellowLime.yellowLime600,
            "700" to Solid.YellowLime.yellowLime700,
            "800" to Solid.YellowLime.yellowLime800,
            "900" to Solid.YellowLime.yellowLime900,
            "950" to Solid.YellowLime.yellowLime950,
        ),
    ),
    ColorData(
        title = "Yellow-Lime Alpha",
        items = mapOf(
            "50" to Alpha.YellowLime.alpha50,
            "100" to Alpha.YellowLime.alpha100,
            "200" to Alpha.YellowLime.alpha200,
            "300" to Alpha.YellowLime.alpha300,
            "400" to Alpha.YellowLime.alpha400,
            "500" to Alpha.YellowLime.alpha500,
            "600" to Alpha.YellowLime.alpha600,
            "700" to Alpha.YellowLime.alpha700,
            "800" to Alpha.YellowLime.alpha800,
            "900" to Alpha.YellowLime.alpha900,
            "950" to Alpha.YellowLime.alpha950,
        ),
    ),
    ColorData(
        title = "Neutral",
        items = mapOf(
            "50" to Solid.Neutral.neutral50,
            "100" to Solid.Neutral.neutral100,
            "200" to Solid.Neutral.neutral200,
            "300" to Solid.Neutral.neutral300,
            "400" to Solid.Neutral.neutral400,
            "500" to Solid.Neutral.neutral500,
            "600" to Solid.Neutral.neutral600,
            "700" to Solid.Neutral.neutral700,
            "800" to Solid.Neutral.neutral800,
            "900" to Solid.Neutral.neutral900,
            "950" to Solid.Neutral.neutral950,
        ),
    ),
    ColorData(
        title = "Neutral Alpha",
        items = mapOf(
            "50" to Alpha.Neutral.alpha50,
            "100" to Alpha.Neutral.alpha100,
            "200" to Alpha.Neutral.alpha200,
            "300" to Alpha.Neutral.alpha300,
            "400" to Alpha.Neutral.alpha400,
            "500" to Alpha.Neutral.alpha500,
            "600" to Alpha.Neutral.alpha600,
            "700" to Alpha.Neutral.alpha700,
            "800" to Alpha.Neutral.alpha800,
            "900" to Alpha.Neutral.alpha900,
            "950" to Alpha.Neutral.alpha950,
        ),
    ),
)

private data class ColorData(
    val title: String,
    val items: Map<String, Color>,
)

private fun textColorFor(
    backgroundColor: Color,
    onLightColor: Color,
    onDarkColor: Color,
): Color {
    val bt601Luma = 0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue

    return if (bt601Luma > 0.5) {
        onLightColor
    } else {
        onDarkColor
    }
}
