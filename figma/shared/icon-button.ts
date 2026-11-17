import figma from 'figma'

// `Icon Button` and the internal `.Icon Button Circular` share every property;
// the circular set is the same composable with a circular shape.
const read = (instance, cased) => {
  const variant = instance.getEnum('◇ Variant', {
    Primary: cased('Primary'),
    Secondary: cased('Secondary'),
    Neutral: cased('Neutral'),
    Critical: cased('Critical'),
    'On Brand': cased('OnBrand'),
    'On Color': cased('OnColor'),
  })
  const type = instance.getEnum('◇ Type', {
    Solid: cased('Solid'),
    Subtle: cased('Subtle'),
    Ghost: cased('Ghost'),
  })
  // LemonadeButtonSize also has XSmall, unused by these sets.
  const size = instance.getEnum('↕ Size', {
    Large: cased('Large'),
    Medium: cased('Medium'),
    Small: cased('Small'),
  })
  const loading = instance.getEnum('◉ Is Loading', { True: true, False: false })
  const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })
  const icon = instance.getInstanceSwap('🧩 Icon')
  const iconCode = icon && icon.type === 'INSTANCE' ? icon.executeTemplate().example : undefined
  return { variant, type, size, loading, disabled, iconCode }
}

const lowerFirst = (value) => value.charAt(0).toLowerCase() + value.slice(1)

export const kotlinIconButton = (instance, circular) => {
  const { variant, type, size, loading, disabled, iconCode } = read(instance, (value) => value)
  return {
    example: figma.kotlin`LemonadeUi.IconButton(
    icon = ${iconCode ?? 'LemonadeIcons.Heart'},
    contentDescription = null, // TODO: this button has no visible label — describe the action
    onClick = { },
    variant = LemonadeButtonVariant.${variant},
    type = LemonadeButtonType.${type},
    size = LemonadeButtonSize.${size},${disabled ? `
    enabled = false,` : ''}${loading ? `
    loading = true,` : ''}${circular ? `
    shape = LemonadeIconButtonShape.Circular,` : ''}
)${iconCode ? '' : `
// NOTE: the design's icon did not resolve; set the entry it uses`}`,
    imports: [
      'import com.teya.lemonade.core.LemonadeIcons',
      'import com.teya.lemonade.IconButton',
      'import com.teya.lemonade.LemonadeUi',
      'import com.teya.lemonade.core.LemonadeButtonSize',
      'import com.teya.lemonade.core.LemonadeButtonType',
      'import com.teya.lemonade.core.LemonadeButtonVariant',
      ...(circular ? ['import com.teya.lemonade.core.LemonadeIconButtonShape'] : []),
    ],
  }
}

export const swiftIconButton = (instance, circular) => {
  const { variant, type, size, loading, disabled, iconCode } = read(instance, lowerFirst)
  return {
    example: figma.swift`LemonadeUi.IconButton(
    icon: ${iconCode ?? 'LemonadeIcon.heart'},
    contentDescription: nil, // TODO: this button has no visible label — describe the action
    onClick: { },${disabled ? `
    enabled: false,` : ''}
    variant: .${variant},
    type: .${type},
    size: .${size}${loading ? `,
    loading: true` : ''}${circular ? `,
    shape: .circular` : ''}
)${iconCode ? '' : `
// NOTE: the design's icon did not resolve; set the case it uses`}`,
  }
}
