// The kit's optional behaviour. A page links it after lemonade-kit.css and adds no script of its own; every
// page still works without it (LHK-FLOWS). It reads kit markup only, so nothing here is page-specific.
;(() => {
  const OVERLAYS = '.lmnd-kit-scrim[id], .lmnd-kit-dropdown-layer[id], .lmnd-kit-drawer-layer[id]'
  const FOCUSABLE = 'a[href], button:not(:disabled), input:not(:disabled), select, textarea, [tabindex]:not([tabindex="-1"])'

  // ---------- Overlays: sheets, dialogs, dropdowns and the side menu ----------

  // Opened by a link to their id, as without script; this adds Esc, a tap on the scrim, and focus that
  // moves into the overlay and back to what opened it.
  let opener = null

  const openOverlay = () => {
    const id = decodeURIComponent(location.hash.slice(1))
    const overlay = id && document.getElementById(id)
    return overlay && overlay.matches(OVERLAYS) ? overlay : null
  }

  // An overlay a link opened has its own history entry, so closing goes back through it: a later Back then
  // returns to the screen, not to the overlay. One the page opened on has none to go back through, so it
  // takes an empty fragment, which :target follows, as its own close link does.
  const close = () => {
    if (!openOverlay()) return
    if (history.state?.lmndKitOverlay) history.back()
    else location.hash = ''
  }

  document.addEventListener('click', (event) => {
    const link = event.target.closest('a[href^="#"]')
    if (link && link.getAttribute('href').length > 1) opener = link
    const overlay = openOverlay()
    // A tap on the scrim itself, outside the sheet or dialog, closes it.
    if (overlay && event.target === overlay) close()
    // A label or button with data-lmnd-kit-close does its own job first — a label picks its option — and
    // then closes the overlay it sits in. Without script the option is still picked and the overlay stays.
    else if (overlay && event.target.closest('[data-lmnd-kit-close]')?.closest(OVERLAYS) === overlay) setTimeout(close)
  })

  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') close()
  })

  // Opening animates in CSS. Closing, whichever way it happens — Esc, the scrim, a link, the browser's back
  // button — keeps the overlay on screen while it animates out.
  let shown = null
  const CLOSE = 360

  const animateClose = (overlay) => {
    if (matchMedia('(prefers-reduced-motion: reduce)').matches) return
    overlay.classList.add('lmnd-kit-overlay--closing')
    setTimeout(() => overlay.classList.remove('lmnd-kit-overlay--closing'), CLOSE)
  }

  window.addEventListener('hashchange', () => {
    const overlay = openOverlay()
    if (overlay && opener) history.replaceState({ ...history.state, lmndKitOverlay: true }, '')
    if (shown && shown !== overlay) animateClose(shown)
    shown = overlay
    if (overlay) {
      const first = [...overlay.querySelectorAll(FOCUSABLE)].find((el) => !el.matches('[class$="__dismiss"]'))
      first?.focus({ preventScroll: true })
    } else if (opener) {
      opener.focus({ preventScroll: true })
      opener = null
    }
  })

  // ---------- Several screens in one file ----------

  // A device holding several screens with ids is a little app: a link to a screen's id pushes it in, the
  // browser's back button and a link with data-lmnd-kit-back slide back, and a link in the tab bar switches
  // without sliding. Closing a sheet keeps the screen it opened on.
  let device = null
  let screens = []
  const stack = []
  let current = null
  let lastLink = null
  const reduceMotion = matchMedia('(prefers-reduced-motion: reduce)')

  const screenFor = (id) => screens.find((screen) => screen.id === id)

  // An animation that never reports its end (a background tab, a headless render) still ends.
  const SLIDE = 400
  const animate = (screen, name, done) => {
    const end = () => {
      screen.classList.remove(name)
      done?.()
    }
    if (reduceMotion.matches) return end()
    screen.classList.add(name)
    const timer = setTimeout(end, SLIDE)
    screen.addEventListener('animationend', () => {
      clearTimeout(timer)
      end()
    }, { once: true })
  }

  const show = (next, how) => {
    if (!next || next === current) return
    const previous = current
    current = next
    if (previous) {
      previous.classList.remove('lmnd-kit-screen--current')
      if (how !== 'switch' && !reduceMotion.matches) {
        previous.classList.add('lmnd-kit-screen--leaving')
        animate(previous, how === 'back' ? 'lmnd-kit-screen--pop-out' : 'lmnd-kit-screen--push-out', () => {
          previous.classList.remove('lmnd-kit-screen--leaving')
        })
      }
    }
    next.classList.add('lmnd-kit-screen--current')
    if (how === 'push') {
      next.scrollTop = 0
      animate(next, 'lmnd-kit-screen--push-in')
      // A browser only autofocuses as the page loads, so a field marked autofocus takes focus as its screen
      // is pushed in, as the app's keyboard opens on a form step.
      next.querySelector('[autofocus]')?.focus({ preventScroll: true })
    } else if (how === 'back') {
      animate(next, 'lmnd-kit-screen--pop-in')
    }
  }

  const go = (next) => {
    const index = stack.indexOf(next.id)
    let how = 'push'
    if (lastLink?.closest('.lmnd-kit-tab-bar')) how = 'switch'
    else if (index >= 0) how = 'back'
    if (how === 'back') stack.length = index + 1
    else if (how === 'switch') stack.splice(0, stack.length, next.id)
    else stack.push(next.id)
    show(next, how)
    history.replaceState({ lmndKitScreen: next.id }, '')
    lastLink = null
  }

  // Set up once the page is parsed: a standalone file inlines this script in its head.
  const route = () => {
    device = document.querySelector('.lmnd-kit-device')
    screens = device ? [...device.querySelectorAll(':scope > .lmnd-kit-screen[id]')] : []
    if (screens.length < 2) return
    device.setAttribute('data-lmnd-kit-screens', '')
    const first = screenFor(decodeURIComponent(location.hash.slice(1))) || screens[0]
    stack.push(first.id)
    show(first, 'switch')
    history.replaceState({ lmndKitScreen: first.id }, '')

    document.addEventListener('click', (event) => {
      const link = event.target.closest('a[href]')
      if (!link) return
      lastLink = link
      // Back goes back through history when there is somewhere to go; its href is the fallback.
      if (link.hasAttribute('data-lmnd-kit-back') && stack.length > 1) {
        event.preventDefault()
        history.back()
      }
    }, true)

    window.addEventListener('popstate', (event) => {
      const next = screenFor(event.state?.lmndKitScreen)
      if (next) go(next)
    })

    // Every entry remembers the screen it showed, so going back through a sheet's opening and closing
    // lands on the right screen.
    window.addEventListener('hashchange', () => {
      const next = screenFor(decodeURIComponent(location.hash.slice(1)))
      if (next && next !== current) go(next)
      else if (!history.state?.lmndKitScreen) history.replaceState({ ...history.state, lmndKitScreen: current.id }, '')
    })
  }

  // ---------- Tabs and segmented controls that switch panels ----------

  // An option names the panel it shows with aria-controls; the panels it doesn't show carry `hidden`
  // in the markup, so without script the first stays put.
  const showPanel = (input) => {
    const group = input.closest('.lmnd-kit-tabs, .lmnd-kit-segmented')
    group?.querySelectorAll('input[aria-controls]').forEach((option) => {
      const panel = document.getElementById(option.getAttribute('aria-controls'))
      if (panel) panel.hidden = !option.checked
    })
  }

  document.addEventListener('change', (event) => {
    if (event.target.matches('.lmnd-kit-tabs input[aria-controls], .lmnd-kit-segmented input[aria-controls]')) {
      showPanel(event.target)
    }
  })

  // ---------- The selection that slides: a segmented control's pill, a tab bar's underline ----------

  // One mark under the selected option, moved to whichever is picked. Only a pick slides it; on load, a
  // resize or a font load it jumps into place, so it lands right in a panel that was hidden at first.
  const SLIDERS = [
    { control: '.lmnd-kit-segmented', selected: '.lmnd-kit-segmented__item:has(input:checked), .lmnd-kit-segmented__item--selected', mark: (item) => item },
    { control: '.lmnd-kit-tabs', selected: '.lmnd-kit-tabs__item:has(input:checked), .lmnd-kit-tabs__item--selected', mark: (item) => item.querySelector('.lmnd-kit-tabs__pill') || item },
  ]

  const slideMarks = () => {
    SLIDERS.forEach(({ control: selector, selected, mark }) => {
      document.querySelectorAll(selector).forEach((control) => {
        const place = (slide) => {
          const item = control.querySelector(selected)
          const target = item && mark(item)
          if (!target || !target.offsetWidth) return
          const box = control.getBoundingClientRect()
          const rect = target.getBoundingClientRect()
          if (!slide) control.setAttribute('data-lmnd-kit-still', '')
          control.style.setProperty('--lmnd-kit-pill-x', `${rect.left - box.left + control.scrollLeft}px`)
          control.style.setProperty('--lmnd-kit-pill-top', `${rect.top - box.top}px`)
          control.style.setProperty('--lmnd-kit-pill-width', `${rect.width}px`)
          control.style.setProperty('--lmnd-kit-pill-height', `${rect.height}px`)
          if (!slide) {
            getComputedStyle(control).width
            requestAnimationFrame(() => control.removeAttribute('data-lmnd-kit-still'))
          }
        }
        control.setAttribute('data-lmnd-kit-still', '')
        control.setAttribute('data-lmnd-kit-sliding', '')
        place(false)
        control.addEventListener('change', () => place(true))
        new ResizeObserver(() => place(false)).observe(control)
      })
    })
  }

  // ---------- A text area's counter ----------

  // A field with a data-lmnd-kit-max-length input counts what's typed against it, in UTF-16 code units
  // as the app does, and turns critical past it. It never blocks typing.
  const count = (input) => {
    const field = input.closest('.lmnd-kit-field')
    const counter = field?.querySelector('.lmnd-kit-field__counter')
    const limit = Number(input.dataset.lmndKitMaxLength)
    if (!counter || !limit) return
    counter.textContent = `${input.value.length}/${limit}`
    counter.classList.toggle('lmnd-kit-field__counter--over', input.value.length > limit)
  }

  // ---------- Text that follows a field ----------

  // An element with data-lmnd-kit-mirror="field-id" shows what's typed in that field — a new role's name in
  // the footer under it, on the screens after it and on its review — and its data-lmnd-kit-mirror-empty text
  // while the field is empty. Without script it keeps the text in its markup. A field that mirrors another
  // takes its value, so the same entry on two screens stays one entry.
  const mirror = (input, seen = new Set()) => {
    if (!input.id || seen.has(input)) return
    seen.add(input)
    const text = input.value.trim()
    document.querySelectorAll(`[data-lmnd-kit-mirror="${CSS.escape(input.id)}"]`).forEach((target) => {
      if (target.matches('input, textarea')) {
        target.value = input.value
        mirror(target, seen)
      } else {
        target.textContent = text || target.dataset.lmndKitMirrorEmpty || ''
      }
    })
  }

  document.addEventListener('input', (event) => {
    if (event.target.matches('[data-lmnd-kit-max-length]')) count(event.target)
    if (event.target.matches('input, textarea')) mirror(event.target)
  })

  // ---------- Starting over from what the markup says ----------

  // A control with data-lmnd-kit-reset="element-id" puts every field inside that element back as its markup
  // wrote it before the control acts — picking a role to start from shows that role's own permissions, not
  // the ticks left from last time. An option already picked resets nothing, so tapping it again keeps the
  // edits. Without script the fields keep what was entered.
  const resetFields = (scope) => {
    scope.querySelectorAll('input, textarea, select').forEach((field) => {
      if (field.matches('[type="checkbox"], [type="radio"]')) field.checked = field.defaultChecked
      else if (field.matches('select')) [...field.options].forEach((option) => { option.selected = option.defaultSelected })
      else field.value = field.defaultValue
    })
    scope.querySelectorAll('input, textarea, select').forEach((field) => {
      field.dispatchEvent(new Event('input', { bubbles: true }))
      field.dispatchEvent(new Event('change', { bubbles: true }))
    })
  }

  document.addEventListener('click', (event) => {
    const trigger = event.target.closest('[data-lmnd-kit-reset]')
    if (!trigger) return
    const control = trigger.matches('input') ? trigger : trigger.control
    if (control?.matches('[type="radio"]') && control.checked) return
    const scope = document.getElementById(trigger.dataset.lmndKitReset)
    if (scope) resetFields(scope)
  }, true)

  // A screen scrolls itself in the phone frame and the page scrolls on a phone, so both are watched. Its size
  // changing — not least from hidden to shown, or back to the top as it's pushed — measures it again.
  const watchScroll = (screen, update) => {
    screen.addEventListener('scroll', update, { passive: true })
    window.addEventListener('scroll', update, { passive: true })
    new ResizeObserver(update).observe(screen)
    update()
  }

  // ---------- A large title that scrolls under the bar ----------

  // The large title is the first thing in the content and scrolls with the page. Once it has gone under the
  // navigation bar, the bar collapses: its small title and divider fade in. Nothing changes size, so the
  // title follows the finger.
  const foldTitles = () => {
    document.querySelectorAll('.lmnd-kit-screen').forEach((screen) => {
      const bar = screen.querySelector(':scope > .lmnd-kit-top-bar')
      const large = screen.querySelector(':scope > .lmnd-kit-content > .lmnd-kit-large-title')
      if (!bar || !large || !bar.querySelector('.lmnd-kit-top-bar__center')) return
      // Half under the bar is enough: the small title takes over as the large one disappears. A screen that
      // isn't showing yet (one of several in a file) has nothing to measure, so it waits until it shows.
      const update = () => {
        if (!screen.offsetWidth) return
        // The title, not the block: a description under it would hold the bar open for two more lines.
        const title = large.firstElementChild.getBoundingClientRect()
        bar.classList.toggle('lmnd-kit-top-bar--collapsed', (title.top + title.bottom) / 2 <= bar.getBoundingClientRect().bottom)
      }
      watchScroll(screen, update)
    })
  }

  // ---------- A top bar that fills once its screen scrolls ----------

  // Details and Home bars start clear over their colour and fill once the screen scrolls. CSS does it from
  // the scroll position where the browser can; this marks the bar everywhere else.
  const fillBars = () => {
    document.querySelectorAll('.lmnd-kit-screen--details > .lmnd-kit-top-bar, .lmnd-kit-screen--home > .lmnd-kit-top-bar').forEach((bar) => {
      const screen = bar.parentElement
      const update = () => {
        if (!screen.offsetWidth) return
        bar.classList.toggle('lmnd-kit-top-bar--scrolled', Math.max(screen.scrollTop, scrollY) > 8)
      }
      watchScroll(screen, update)
    })
  }

  // ---------- A strip of days that opens on the selected one ----------

  const centreDays = () => {
    document.querySelectorAll('.lmnd-kit-day-strip').forEach((strip) => {
      const day = strip.querySelector('[aria-current]')
      if (!day) return
      const style = getComputedStyle(strip)
      const step = day.offsetWidth + (parseFloat(style.columnGap) || 0)
      const left = day.getBoundingClientRect().left - strip.getBoundingClientRect().left + strip.scrollLeft
      strip.scrollLeft = Math.max(0, left - (parseFloat(style.paddingLeft) || 0) - 3 * step)
    })
  }

  const start = () => {
    route()
    document.querySelectorAll('.lmnd-kit-tabs input[aria-controls]:checked, .lmnd-kit-segmented input[aria-controls]:checked').forEach(showPanel)
    foldTitles()
    fillBars()
    slideMarks()
    centreDays()
    document.querySelectorAll('[data-lmnd-kit-max-length]').forEach(count)
    new Set([...document.querySelectorAll('[data-lmnd-kit-mirror]')].map((target) => document.getElementById(target.dataset.lmndKitMirror)))
      .forEach((input) => input?.matches('input, textarea') && mirror(input))
    shown = openOverlay()
    if (shown) window.dispatchEvent(new HashChangeEvent('hashchange'))
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', start)
  else start()
})()
