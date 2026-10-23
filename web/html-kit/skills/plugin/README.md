# Lemonade prototype

A Claude Code plugin for prototyping Teya app screens as HTML that looks like the app, built on the
Lemonade design system's tokens, components and icons.

| Skill | What it does |
| --- | --- |
| `/lemonade-prototype:design` | Builds a screen or flow, starting from the kit's example screens, and hands back one self-contained HTML file |
| `/lemonade-prototype:review` | Checks an HTML prototype against the Lemonade and Teya app guidelines and reports each broken rule by ID |

Claude also picks them up on its own when you ask for a Teya prototype or a review of one.

## Install

```sh
/plugin marketplace add <path or git URL of the marketplace folder>
/plugin install lemonade-prototype@lemonade
```

The plugin is generated from `web/html-kit/` in the Lemonade design system repository by
`npm run html-kit`; edit it there, not here.
