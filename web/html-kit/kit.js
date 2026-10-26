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

  // The same as the overlay's own close link: an empty fragment, which :target follows.
  const close = () => {
    if (openOverlay()) location.hash = ''
  }

  document.addEventListener('click', (event) => {
    const link = event.target.closest('a[href^="#"]')
    if (link && link.getAttribute('href').length > 1) opener = link
    const overlay = openOverlay()
    // A tap on the scrim itself, outside the sheet or dialog, closes it.
    if (overlay && event.target === overlay) close()
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
      else if (!history.state?.lmndKitScreen) history.replaceState({ lmndKitScreen: current.id }, '')
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

  // ---------- A segmented control's sliding pill ----------

  // One pill under the selected option, moved to whichever is picked. It follows the control's size, so it
  // lands right in a panel or screen that was hidden when the page loaded.
  const slidePills = () => {
    document.querySelectorAll('.lmnd-kit-segmented').forEach((control) => {
      // Only a pick slides the pill; every other placement jumps it into place.
      const place = (slide) => {
        const item = control.querySelector('.lmnd-kit-segmented__item:has(input:checked), .lmnd-kit-segmented__item--selected')
        if (!item || !item.offsetWidth) return
        if (!slide) control.setAttribute('data-lmnd-kit-still', '')
        control.style.setProperty('--lmnd-kit-pill-x', `${item.offsetLeft}px`)
        control.style.setProperty('--lmnd-kit-pill-top', `${item.offsetTop}px`)
        control.style.setProperty('--lmnd-kit-pill-width', `${item.offsetWidth}px`)
        control.style.setProperty('--lmnd-kit-pill-height', `${item.offsetHeight}px`)
        if (!slide) {
          getComputedStyle(control, '::before').transform
          requestAnimationFrame(() => control.removeAttribute('data-lmnd-kit-still'))
        }
      }
      control.setAttribute('data-lmnd-kit-still', '')
      control.setAttribute('data-lmnd-kit-sliding', '')
      place(false)
      control.addEventListener('change', () => place(true))
      new ResizeObserver(() => place(false)).observe(control)
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

  document.addEventListener('input', (event) => {
    if (event.target.matches('[data-lmnd-kit-max-length]')) count(event.target)
  })

  // ---------- A large title that folds into the bar on scroll ----------

  // A navigation top bar with a large title below its row folds it into the centred small title once the
  // title has scrolled under the bar, as the app's collapsing top bar does.
  const foldTitles = () => {
    document.querySelectorAll('.lmnd-kit-top-bar:has(> .lmnd-kit-top-bar__bar .lmnd-kit-top-bar__center):has(> .lmnd-kit-top-bar__large)').forEach((bar) => {
      const screen = bar.closest('.lmnd-kit-screen')
      const large = bar.querySelector(':scope > .lmnd-kit-top-bar__large')
      if (!screen || !large) return
      const fold = large.offsetHeight
      const update = () => bar.classList.toggle('lmnd-kit-top-bar--collapsed', screen.scrollTop > fold)
      ;(screen.scrollHeight > screen.clientHeight ? screen : window).addEventListener('scroll', update, { passive: true })
      update()
    })
  }

  // ---------- A top bar that fills once its screen scrolls ----------

  // Details and Home bars start clear over their colour and fill once the screen scrolls. CSS does it from
  // the scroll position where the browser can; this marks the bar everywhere else.
  const fillBars = () => {
    document.querySelectorAll('.lmnd-kit-screen--details > .lmnd-kit-top-bar, .lmnd-kit-screen--home > .lmnd-kit-top-bar').forEach((bar) => {
      const screen = bar.parentElement
      const scroller = screen.scrollHeight > screen.clientHeight ? screen : window
      const update = () => bar.classList.toggle('lmnd-kit-top-bar--scrolled', (scroller === window ? scrollY : screen.scrollTop) > 8)
      scroller.addEventListener('scroll', update, { passive: true })
      update()
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
    slidePills()
    centreDays()
    document.querySelectorAll('[data-lmnd-kit-max-length]').forEach(count)
    shown = openOverlay()
    if (shown) window.dispatchEvent(new HashChangeEvent('hashchange'))
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', start)
  else start()
})()
