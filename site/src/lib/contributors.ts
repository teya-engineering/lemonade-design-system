/**
 * Who wrote each page, and who last touched it, read from git at build time.
 *
 * Starlight can surface a last-updated date but knows nothing about authors,
 * so this does one `git log` pass over the docs directory and derives both
 * ends of each file's history from it.
 *
 * Astro components run on the server during the build, so shelling out here is
 * the same thing Starlight's own date lookup does.
 */
import { spawnSync } from 'node:child_process';
import { relative, resolve } from 'node:path';

export interface Contribution {
	name: string;
	date: Date;
}

export interface PageHistory {
	/** The commit that first added the page, following renames. */
	created: Contribution;
	/** The most recent commit to touch it. */
	lastEdited: Contribution;
}

interface RawEntry {
	created: { date: number; name: string };
	lastEdited: { date: number; name: string };
}

/**
 * Pure parse step, split out from `pageHistory()` so it can be exercised in
 * tests without a repository in a particular shape.
 *
 * `git log` lists newest first, so the first time a path appears is its most
 * recent change and the last time is its creation. Renames are followed: a
 * rename line names both paths, and because the newer name has already been
 * seen by the time the rename is reached, the older one can be pointed at it.
 */
export function parseGitLog(stdout: string): Map<string, RawEntry> {
	const renamedTo = new Map<string, string>();
	const entries = new Map<string, RawEntry>();

	const currentName = (path: string): string => {
		const seen = new Set<string>();
		let name = path;
		while (renamedTo.has(name) && !seen.has(name)) {
			seen.add(name);
			name = renamedTo.get(name)!;
		}
		return name;
	};

	let date = 0;
	let author = '';

	for (const line of stdout.split('\n')) {
		if (line.startsWith('c:')) {
			const split = line.indexOf('|');
			date = Number.parseInt(line.slice(2, split), 10) * 1000;
			author = line.slice(split + 1);
			continue;
		}

		// A<TAB>path | M<TAB>path | D<TAB>path | R<n><TAB>old<TAB>new
		const parts = line.split('\t');
		if (parts.length < 2) continue;

		const path = currentName(parts[parts.length - 1]!);
		if (parts[0]!.startsWith('R') && parts.length === 3) {
			renamedTo.set(parts[1]!, path);
		}

		const existing = entries.get(path);
		if (existing) {
			// Still walking backwards, so this is always the older commit.
			existing.created = { date, name: author };
		} else {
			entries.set(path, { created: { date, name: author }, lastEdited: { date, name: author } });
		}
	}

	return entries;
}

const DOCS_SUBPATH = 'src/content/docs';

let cache: Map<string, PageHistory> | undefined;

/** Keyed by path relative to `src/content/docs`, e.g. "layout/rhythm.md". */
export function pageHistory(): Map<string, PageHistory> {
	if (cache) return cache;

	const projectRoot = process.cwd();
	const docsPath = resolve(projectRoot, DOCS_SUBPATH);

	const root = spawnSync('git', ['rev-parse', '--show-toplevel'], {
		cwd: projectRoot,
		encoding: 'utf-8',
	});
	const repoRoot = root.stdout?.trim();

	const log = spawnSync(
		'git',
		['log', '--format=c:%ct|%an', '--name-status', '--', docsPath],
		// The default 1 MB buffer is not enough for a long history.
		{ cwd: repoRoot || projectRoot, encoding: 'utf-8', maxBuffer: 10 * 1024 * 1024 },
	);

	const parsed = repoRoot && !log.error ? parseGitLog(log.stdout ?? '') : new Map();

	if (parsed.size === 0) {
		throw new Error(
			'No git history found for the documentation pages, so no page could show who ' +
				'wrote it. This usually means a shallow clone: Starlight and this module both ' +
				'read git at build time, so the CI checkout sets `fetch-depth: 0`. If you are ' +
				'building outside a git working tree, that is the cause.',
		);
	}

	cache = new Map(
		[...parsed].map(([repoRelativePath, entry]) => [
			relative(docsPath, resolve(repoRoot!, repoRelativePath)).replace(/\\/g, '/'),
			{
				created: { name: entry.created.name, date: new Date(entry.created.date) },
				lastEdited: { name: entry.lastEdited.name, date: new Date(entry.lastEdited.date) },
			},
		]),
	);

	return cache;
}

/**
 * History for one page, given the entry's `filePath`. Returns undefined for a
 * page that is not committed yet — that is normal while writing one, and it
 * corrects itself on commit, so it must not fail the build.
 */
export function historyForPage(filePath: string): PageHistory | undefined {
	const normalised = filePath.replace(/\\/g, '/');
	const marker = `${DOCS_SUBPATH}/`;
	const index = normalised.indexOf(marker);
	const key = index === -1 ? normalised : normalised.slice(index + marker.length);
	return pageHistory().get(key);
}
