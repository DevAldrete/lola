# Clean Git Workflow

A practical discipline for turning any coding session into a clean, reviewable history — one where every commit tells a single true story, and code quality is tended continuously rather than fixed in a panic later.

Three pillars, always in play together:
1. **Atomic, contextual commits** — every commit is one logical change, fully explained.
2. **Periodic optimization scanning** — don't just do the task; notice nearby opportunities to improve.
3. **Ongoing complexity control** — actively work to keep code readable and maintainable, not just correct.

---

## Pillar 1: Atomic, Contextual Commits

### The atomic commit rule
One commit = one logical change. If you'd need the word "and" to describe it honestly, split it.

- ✅ "Fix null pointer in `parseInvoice` when currency field is missing"
- ❌ "Fix invoice bug and update dependencies and tidy up formatting"

A change is atomic when:
- Reverting it alone doesn't break something unrelated
- It builds and passes tests in isolation
- A reviewer can understand *why* without reading other commits first

### Before committing — the staging discipline
Never blindly `git add .`. Instead:

```bash
git status                 # see what actually changed
git diff                   # read every hunk before staging
git add -p                 # stage hunk-by-hunk when a file mixes concerns
```

If a single file has two unrelated changes (e.g., a bug fix and an unrelated formatting pass), split them into separate commits with `git add -p`, even if it's more effort. This is the single highest-leverage habit in this skill.

### Commit message structure
Use a consistent, information-dense format:

```
<type>(<scope>): <imperative summary, ≤50 chars>

<why this change was needed — the problem, not just the solution>
<what approach was taken and any tradeoffs, if non-obvious>
<side effects or follow-ups, if any>

Refs: #123
```

**Types** (Conventional Commits, adapt as needed):
- `feat` — new capability
- `fix` — bug fix
- `refactor` — behavior-preserving restructuring
- `perf` — performance improvement
- `docs` — documentation only
- `test` — tests only
- `chore` — tooling, deps, config
- `style` — formatting, no logic change

**Rules of thumb:**
- Summary line: imperative mood ("Add", not "Added" or "Adds"), no trailing period, ≤50 chars.
- Body wraps at ~72 chars, explains *why* over *what* (the diff already shows what).
- If a commit needs a bullet list to explain multiple things it did — that's a signal it should have been multiple commits.
- Reference issue/ticket numbers when they exist; don't invent them.

### Sequencing a piece of work
Before writing code, sketch the commit sequence, not just the end state. Example for "add rate limiting to the API":

1. `test: add failing tests for rate limiter behavior`
2. `feat(api): add token-bucket rate limiter`
3. `feat(api): wire rate limiter into request middleware`
4. `docs: document rate limit headers in API reference`

Each step should leave the repo in a working state. This makes `git bisect` useful later and lets a reviewer approve incrementally.

### Cleaning history before sharing
Local, not-yet-pushed (or not-yet-reviewed) history is a draft — feel free to fix it:

```bash
git commit --amend                    # fix the most recent commit's message or content
git rebase -i HEAD~5                  # reorder, squash, split, or reword the last 5 commits
```

In an interactive rebase:
- `squash`/`fixup` — fold a "fix typo" or "address review comment" commit into the commit it fixes, so history doesn't preserve the mess of getting there.
- `reword` — fix a message after the fact.
- `edit` — stop mid-rebase to split a commit that turned out to mix concerns (`git reset HEAD^` then re-stage in pieces).

**Never rewrite history that others have already pulled from** — that's the line between "cleaning up a draft" and "breaking your teammates' repos." Once pushed and reviewed/shared, only add new commits (use `git revert` to undo, not rebase).

### Fixes specifically
A "fix" commit should be the smallest possible change that resolves the issue, isolated from any refactoring you were tempted to do at the same time. If you spot a refactor opportunity while fixing a bug:
1. Commit the fix alone first.
2. Then do the refactor as its own, separate `refactor:` commit.

This keeps `git bisect` and `git blame` trustworthy — a regression hunt should never land on a commit that's secretly three things at once.

---

## Pillar 2: Periodic Optimization Scanning

Don't wait for a dedicated "refactor sprint." Build small, low-risk scans into the normal rhythm of work.

### When to scan
- **Before starting work in a file/module**: quick pass for anything glaringly stale nearby.
- **After finishing a feature/fix, before moving on**: look at what you just touched with fresh eyes.
- **On a regular cadence** (e.g., weekly, or every N commits): a deliberate, timeboxed pass over a module, not the whole codebase at once.

### What to look for
| Signal | Likely opportunity |
|---|---|
| Same 3+ lines repeated in multiple places | Extract function/constant |
| Function doing setup + logic + cleanup + logging | Split into smaller functions |
| Deeply nested conditionals (3+ levels) | Early returns / guard clauses |
| Comment explaining *what* the code does | Code should say what; comment should say why (or code needs renaming) |
| TODO/FIXME older than a few weeks | Either do it now, or convert to a tracked issue and delete the comment |
| Dependency with a major version behind | Flag for a dedicated `chore` commit — don't silently bundle the upgrade into feature work |
| A test that's hard to write | Usually means the code under test is doing too much — a design smell, not a test problem |
| Manual repeated steps (build, deploy, data seed) | Candidate for a script or Makefile target |

### How to act on what you find
Don't fix everything you notice in the moment — that derails the current task and produces exactly the kind of mixed commit Pillar 1 warns against. Instead:

1. **Trivial and truly isolated** (rename a misleading local variable, delete dead code you're already looking at) → fix immediately, in its own tiny commit.
2. **Real but not urgent** → leave a `TODO(yourname): <what and why>` and log it as an issue/ticket, or add to a running "tech debt" note.
3. **Large or risky** → write it up (what, why, rough cost) and propose it separately; don't surprise-refactor something load-bearing mid-feature.

### A lightweight periodic review checklist
Run this over a module/file about to see heavy use, on a cadence:

- [ ] Any function longer than ~40–50 lines? Could it be split by responsibility?
- [ ] Any duplicated logic that's drifted (same idea, implemented slightly differently in two places)?
- [ ] Are names still accurate after recent changes, or did the code's purpose shift under them?
- [ ] Is error handling consistent with the rest of the codebase?
- [ ] Are there dependencies or patterns here that are now deprecated elsewhere in the repo?
- [ ] Would a newcomer understand this file's purpose from its name + first 10 lines?

---

## Pillar 3: Readability, Maintainability, Reduced Complexity

Treat this as a standing constraint on every change, not a separate task.

### Guiding heuristics
- **Optimize for the reader, not the writer.** Code is read far more often than it's written. If a clever one-liner takes 30 seconds to parse, an obvious 4-liner is usually better.
- **Name things by intent, not implementation.** `activeUsersSince(date)` beats `filterUsers(u => u.lastSeen > date)` repeated inline.
- **Push complexity to the edges.** Keep core logic simple and pure; put messiness (I/O, formatting, retries, edge-case handling) at the boundaries.
- **Prefer deletion to addition.** The fastest way to reduce complexity is removing code that's no longer needed — dead branches, unused flags, obsolete abstractions. Deleting code is a valid, valuable commit on its own (`chore: remove unused legacy export path`).
- **Match the existing idiom.** Consistency across a codebase reduces cognitive load more than any single "better" pattern would, unless you're deliberately migrating the whole codebase off an old idiom.

### Concrete complexity controls
- **Cyclomatic complexity**: if a function has many branching paths, look for early returns, polymorphism, or lookup tables/maps instead of long if/else or switch chains.
- **Parameter count**: 4+ positional parameters is a smell — group into a config object/struct, or split the function.
- **Depth of nesting**: flatten with guard clauses (`if (!valid) return;`) rather than wrapping the "happy path" in nested ifs.
- **Abstraction layers**: don't introduce an interface/factory/strategy pattern for a single implementation "just in case." Add abstraction when the second real use case appears, not before (rule of three).
- **File/module size**: if a file keeps growing and covers multiple unrelated responsibilities, that's a signal to split it — but do the split as its own dedicated commit (`refactor: split UserService into UserService + UserAuth`), never bundled with a feature change.

### Documentation as maintainability
- Comments explain *why*, not *what* — if a comment restates the code, delete the comment or clarify the code instead.
- Keep a short "why" trail for non-obvious decisions in commit bodies (Pillar 1) rather than scattering rationale in code comments that go stale.
- Update docs/README in the *same* commit as the behavior change they describe — not "later." Stale docs are worse than no docs.

### A pre-commit self-review pass
Before committing, briefly ask:
1. Could someone unfamiliar with this change understand it from the diff + message alone?
2. Did I leave anything more complex than it needed to be to solve *this* problem?
3. Is there dead code, debug logging, or commented-out code I should remove before committing?
4. Does this commit do exactly one thing?

If any answer is "no," fix it before committing rather than after — it's cheaper now than as a follow-up cleanup commit later.

---

## Putting it together: a session workflow

1. **Before coding**: sketch the intended commit sequence (Pillar 1) for the task.
2. **While coding**: work file by file; when something extra catches your eye, triage it per the Pillar 2 table instead of context-switching into it.
3. **Before each commit**: run the self-review pass (Pillar 3), then stage with `git add -p` to keep it atomic (Pillar 1).
4. **After finishing the task**: do a short Pillar 2 scan of the files you just touched while context is fresh — this is the cheapest time to catch drift.
5. **Before sharing/pushing**: `git log --oneline` the branch and sanity-check that the story reads cleanly top to bottom; `rebase -i` to tidy if it doesn't (only if unpushed/unshared).

The goal isn't perfection on every commit — it's a repo where `git log`, `git blame`, and `git bisect` are all genuinely useful tools instead of archaeology, and where complexity gets paid down continuously in small amounts instead of accumulating into a rewrite.
