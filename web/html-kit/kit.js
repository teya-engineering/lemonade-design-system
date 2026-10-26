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

  window.addEventListener('hashchange', () => {
    const overlay = openOverlay()
    if (overlay) {
      const first = [...overlay.querySelectorAll(FOCUSABLE)].find((el) => !el.matches('[class$="__dismiss"]'))
      first?.focus({ preventScroll: true })
    } else if (opener) {
      opener.focus({ preventScroll: true })
      opener = null
    }
  })

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
    document.querySelectorAll('.lmnd-kit-tabs input[aria-controls]:checked, .lmnd-kit-segmented input[aria-controls]:checked').forEach(showPanel)
    foldTitles()
    centreDays()
    if (openOverlay()) window.dispatchEvent(new HashChangeEvent('hashchange'))
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', start)
  else start()
})()
