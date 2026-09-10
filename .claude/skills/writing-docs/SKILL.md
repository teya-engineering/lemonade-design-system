---
name: writing-docs
description: Use when creating or editing any standalone prose in this repo - the root and module READMEs, CLAUDE.md, and any Markdown under .claude/ - or when a code change removes something that docs mention. Enforces that a doc describes the present state and carries no trace of its own edit history, and that removed things are deleted from the docs in the same change.
allowed-tools: Read, Edit, Write, Grep, Glob, Bash(git diff:*)
---

# Writing docs

A doc describes the system as it is right now. It is not a log of how the system got here. Every time
you touch a doc, rewrite the part you touch as if you were writing it for the first time today, with
no memory of what it said before.

For in-code comments and KDoc, use the `comment-review` skill instead. The two overlap; that one
governs comments attached to code, this one governs standalone documents.

## Default: write the present state, not the change

When you open a doc to update it, the question is never "how do I fit my change into what's already
here". It's "if I were documenting this from scratch today, knowing what I now know, what would this
section say". Write that. The reader should not be able to tell the doc was ever any different.

A doc carries no trace of its own edit history:

- No "previously", "used to", "in the past", "historically", "originally".
- No "now", "going forward", "as of this change", "recently", "we've migrated to".
- No "the old X / the new X" contrast when the only thing separating them is time.
- No correction markers: strikethrough, "(updated)", "(was: ...)", "note: this changed",
  "deprecated but kept here for reference".
- No paragraph that exists only to explain why the current state differs from a past one.

If a future reader needs to know why something changed, that belongs in the commit message, the PR
description, or your reply in chat. It never belongs in the document body.

## Delete what no longer exists

A doc that describes a rule, file, flag, API, or convention that has been removed is worse than a doc
that says nothing about it. A silent doc sends the reader to go look. A wrong doc gets the reader to
trust it and build on something that is already gone.

So when you remove or replace something in code, the same change hunts down every mention of it in
the READMEs, `CLAUDE.md`, and `.claude/skills/` and deletes or rewrites it. A renamed type, a dropped
feature flag, a retired script, a convention you just reversed: grep for it, fix every doc that names
it. Leaving the doc for "later" ships a lie.

When the thing you removed was the whole point of a section, delete the section. Don't leave a stub,
a "no longer used" note, or an empty heading. Cut it so the document reads as if that section was
never there.

## Edit by rewriting, not annotating

This is the failure mode the skill exists to kill: bolting the new fact onto the old text and leaving
both. You end up with a paragraph describing the old behavior, immediately followed by a sentence
walking it back. Both now sit in the doc, the reader has to diff them in their head, and the next
editor inherits the confusion.

Rewrite the passage so only the current truth survives. If two sentences contradict each other
because one went stale, the stale one goes. If a list has an item that no longer applies, remove the
item; don't annotate it.

**Before** (the change is narrated, the dead context lingers):

> ## Dependency Injection
> The project originally used a hand-written `DIContainer` with manual module wiring. We have since
> migrated to Metro, a compile-time DI framework. Some older modules may still reference the manual
> container, but new code should use Metro going forward.

**After** (present state, no history):

> ## Dependency Injection
> All DI uses Metro, a compile-time framework with no runtime reflection. Repositories bind with
> `@ContributesBinding(AppScope::class)`; services add `@SingleIn(AppScope::class)`. There is no
> manual container or module wiring.

The "After" still tells the reader the manual container isn't a thing, but it states it as a fact
about the present, not as the resolution of a story.

## The exception: history-scoped documents

Some documents are about change over time on purpose, and their history is the content. Don't strip
these:

- Changelogs and release notes.
- Migration guides ("upgrading from vN to vN+1").
- Architecture decision records (ADRs) and other dated decision logs.

The test: would the document be wrong or pointless without its past entries? A changelog with only
the latest version is broken. A reference doc with a "we previously used Koin" paragraph is just
cluttered. The first is history-scoped and keeps its history; the second describes present state and
drops it.

When you're unsure which kind you're editing, look at the filename and the surrounding entries. A
file named `CHANGELOG.md`, or one living in an `adr/` directory, is history-scoped. A rule, an
architecture overview, a how-to, or a README describes present state.

## Prose quality

Write the way a tired teammate would: plain and direct. No inflated stakes, no hedging, no
rule-of-three padding, no filler.

## Checklist

Before you finish a doc edit:

- The section reads as a clean first draft of the current truth, not a patched version of an older one.
- No "previously / now / used to / going forward / as of" framing (unless the doc is history-scoped).
- Every name it mentions (type, file, flag, script, command) still exists. You grepped.
- Nothing it describes has been removed without the description being removed too.
- The "why it changed" lives in the commit or PR, not in the doc.
