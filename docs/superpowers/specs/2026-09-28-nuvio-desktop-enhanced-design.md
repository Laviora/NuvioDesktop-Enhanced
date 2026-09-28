# Nuvio Desktop Enhanced Design

## Objective

Create **Nuvio Desktop Enhanced**, an unofficial GPL-3.0 desktop fork that keeps
the official Nuvio Desktop application as its implementation base and ports
selected enhancements from Luqman's Nuvio Mobile Enhanced. The maintained and
verified targets are macOS and Windows only.

The project must remain recognizably Nuvio Desktop: preserve its architecture,
upstream directory layout, native desktop player integrations, and release
packaging. Port features individually instead of merging or replacing the
desktop tree with mobile code.

## Pinned Discovery State

Discovery was performed on 2026-09-28 against:

- Nuvio Desktop `Dev` at `fe92d414024b775351d17912a19cbcf51b880241`.
- Nuvio Mobile Enhanced `enhanced` at
  `40faf88abca3e556f4e7258bcca1658cd29c1120`.
- Nuvio Mobile upstream `cmp-rewrite`, used only to distinguish Luqman's work
  from ordinary upstream mobile changes.

At discovery time, the desktop and Enhanced repositories had 446 byte-identical
shared `commonMain` files and 261 shared paths with different content. Luqman's
branch was 293 commits ahead of and 16 commits behind current mobile upstream.
This divergence rules out wholesale cherry-picking or subtree replacement.

## Scope

### In scope

- A GitHub fork of official Nuvio Desktop as the implementation repository.
- A separate GitHub fork of Luqman's Enhanced repository as a preserved
  reference.
- Shared Kotlin/Compose changes that can be adapted without weakening desktop
  behavior.
- macOS arm64 and x86_64 application builds and DMG packaging.
- Windows x64 application builds and MSI packaging.
- Desktop-specific adapters required by a selected shared feature.
- Tests, documentation, attribution, and macOS/Windows CI needed to maintain
  the fork.

### Out of scope

- Android or iOS application work.
- Linux implementation, packaging, or verification work.
- iOS-only appearance, PiP, download, font, tab-bar, and navigation changes.
- Android-only download and storage behavior.
- Rebranding unrelated to distinguishing the unofficial fork.
- Broad redesigns, dependency upgrades, or refactors not required by a port.

Android, iOS, and Linux source already present upstream remains in the tree to
reduce merge conflicts. Shared changes must not knowingly break those source
sets, but they are not release targets and do not receive platform-specific
feature work.

## Repository and Branch Model

Create these GitHub repositories under the authenticated `Laviora` account:

1. `Laviora/NuvioDesktop-Enhanced`, forked from
   `NuvioMedia/NuvioDesktop`. This is the product repository.
2. `Laviora/NuvioMobile-Enhanced`, forked from
   `luqmanfadlli/NuvioMobile-Enhanced`. This is a reference snapshot, not an
   application base.

The product checkout uses these remotes:

| Remote | Repository | Purpose |
|---|---|---|
| `origin` | `Laviora/NuvioDesktop-Enhanced` | User-owned product fork |
| `upstream` | `NuvioMedia/NuvioDesktop` | Authoritative desktop updates |
| `enhanced` | `luqmanfadlli/NuvioMobile-Enhanced` | Read-only source and history for ports |

Branch policy:

- `Dev` stays aligned with `upstream/Dev` and contains no Enhanced-only work.
- `enhanced` starts from `Dev`, is the product integration branch, and becomes
  the fork's default branch.
- Larger ports use temporary `port/<feature>` branches from `enhanced`.
- Each user-visible feature lands as its own commit or short commit series.
- Upstream desktop changes are merged into `Dev`, then merged into `enhanced`.
  Do not merge the mobile Enhanced branch into either desktop branch.

## Porting Rules

For every feature:

1. Identify the original Enhanced commit or the smallest relevant commit
   series and record it in the porting ledger.
2. Re-read the current desktop implementation; mobile code is evidence of
   intended behavior, not an authoritative patch.
3. Write a failing test for the desktop behavior before production changes.
4. Adapt the minimum shared code and add only the desktop adapters needed.
5. Run focused tests, the full desktop test task, desktop compilation, and the
   applicable package build.
6. Commit only that feature, tests, strings, and its ledger update.

Commit messages include a `Ported-from:` trailer with a GitHub commit URL.
When code is substantially derived from an identifiable contributor's patch,
retain Git authorship where practical or add an appropriate `Co-authored-by:`
trailer.

## Enhancement Inventory and Desktop Mapping

Priority describes the intended order, not a promise that every feature will
be ported. A port proceeds only after its dependencies and desktop behavior are
understood.

| Enhanced capability | Desktop mapping | Disposition |
|---|---|---|
| Budget and revenue | `commonMain` currency formatter, TMDB detail model/service, details information UI | **Phase 1: first port**; platform-independent and small |
| Hardware keyboard shortcuts | Shared player shortcut model and player runtime; desktop key handling/controller | **Phase 1: second port**; omit all iOS bridge code |
| Stream-list search | Shared stream filtering/UI/settings plus desktop settings persistence | Phase 2; extract pure filtering for tests |
| Hero card style | Shared home settings and hero/skeleton composables | Phase 2; adapt to desktop window sizes |
| Dynamic hero background | Shared palette/background UI and desktop image pipeline | Phase 2; verify rendering cost and fallback behavior |
| Hero trailer autoplay, delay, and sound | Shared home/details settings and desktop trailer player surface | Phase 2; preserve desktop focus and audio behavior |
| Catalog accent underline and accent gradients | Shared theme/settings/components | Phase 2; avoid iOS tab-bar code |
| Tap-to-seek and gesture readouts | Shared player timeline/gesture UI and desktop input behavior | Phase 3; first audit current mouse semantics |
| Swipe-to-seek toggle | Shared player settings and gesture runtime | Defer unless useful for trackpads; touch-first behavior is not automatically copied |
| Playback info and quality chooser | Shared panels plus desktop mpv controller/track APIs | Phase 3; native-player dependency |
| Volume boost | Desktop mpv audio controls | Phase 3; requires clipping and gain tests |
| Picture in Picture | Native player/window integration | Defer; mobile implementation is not portable |
| Subtitle background transparency | Shared subtitle style plus desktop mpv subtitle rendering | Phase 3 |
| Profile Insights | Shared profile statistics, storage, navigation, and responsive UI | Phase 3; large feature |
| Custom profile background URL | Shared profile model/editor plus desktop image loading/storage | Phase 2 |
| More Like This: View All | Shared details route and paged grid | Phase 2 |
| Episode ratings | Shared TMDB/IMDb enrichment, settings, and episode UI | Phase 2; secrets and API fallback must remain optional |
| Icon action row | Shared details layout | Phase 2; verify keyboard focus order and resizing |
| Details download button | Details/streams/download integration | Defer until desktop downloads are audited |
| Random episode and include-watched option | Shared details/player/watch-state logic | Phase 2; deterministic selection tests |
| Pinned stream sources | Shared per-profile repository, stream sorting, UI, and desktop persistence | Phase 2 |
| Library calendar | Shared release schedule, library UI, navigation, and desktop persistence | Phase 3 |
| Trakt and SIMKL device-code sign-in | Shared auth repositories/sheet plus desktop browser/clipboard behavior | Phase 2; secrets remain external configuration |
| Live TV: M3U, Xtream, Stalker, navigation | New shared subsystem plus desktop persistence/player integration | Defer to a separate design and plan |
| Wi-Fi-only downloads | Mobile network semantics | Out of scope |
| Custom download location | Desktop downloads and file chooser would require a separate design | Defer |
| iOS background downloads and progress activity | iOS-only | Out of scope |
| iOS Liquid Glass, Skia image engine, PiP, bundled fonts | iOS-only | Out of scope |

Enhancements discovered in history but not described in Luqman's public feature
inventory are not silently included. They require explicit classification in
the ledger before porting.

## Initial Port: Budget and Revenue

The first port proves the workflow with the smallest platform-independent
feature. It adds:

- A common formatter for positive whole-dollar TMDB amounts.
- Nullable `budget` and `revenue` fields in the details and enrichment models.
- TMDB decoding and enrichment propagation for movie details.
- Budget and revenue rows in the existing additional-information section.
- Localized labels in the existing string resources.

Unknown values (`null`, zero, or negative) are not displayed. Values are shown
as whole US-dollar amounts with comma grouping, matching the source feature.
Series remain unaffected because TMDB supplies these fields for movies.

Tests cover missing and non-positive amounts, grouping boundaries, large
amounts, TMDB propagation, and the absence of display rows when values are
unknown.

## Second Port: Desktop Keyboard Shortcuts

The second port adds the Enhanced shortcuts that naturally fit desktop:

- `Space`: toggle play/pause.
- `Left Arrow`: seek backward ten seconds.
- `Right Arrow`: seek forward ten seconds.
- `Escape`: leave the player.

The handler is inactive while a modal, text field, source/episode panel, or
locked-controls state owns keyboard input. Repeated key-down events must not
double-trigger actions. The implementation uses Compose desktop key events and
the existing desktop player controller; no iOS `UIPress` or Swift bridge code
is copied.

Tests cover key mapping, disabled states, focus ownership, seek bounds, and one
action per accepted event.

## Build and CI Design

The official desktop paths remain authoritative:

- Development run: `:composeApp:run`.
- macOS package: `:composeApp:packageReleaseDmg`; the existing
  `scripts/build-macos-release-dmgs.sh --package-only` builds arm64 and x86_64.
- Windows package: `:composeApp:packageReleaseMsi`.

The Enhanced CI workflow runs only macOS and Windows jobs. It reuses the
official setup for Git LFS native runtimes, JDK 17, Gradle, WebView2, libmpv,
and packaging. Existing Linux source and scripts stay untouched, but Linux jobs
are not part of Enhanced verification or release criteria.

Required checks for an Enhanced commit are:

1. Focused feature tests.
2. `:composeApp:desktopTest`.
3. `:composeApp:compileKotlinDesktop`.
4. macOS arm64 DMG packaging on macOS.
5. macOS x86_64 DMG packaging in CI.
6. Windows x64 MSI packaging in CI.

Unsigned packages are sufficient for build verification. Signing,
notarization, release publishing, credentials, and secret provisioning are a
separate release-operations concern; no secret is committed to the repository.

## Failure Handling

- A failed port test blocks the feature commit.
- A macOS or Windows packaging failure is reported with the exact failing task;
  success on one OS never implies success on the other.
- A missing native runtime or Git LFS object is treated as an environment/setup
  failure and fixed in CI setup, not bypassed in application code.
- An Enhanced feature that requires invasive desktop restructuring is moved to
  a separate design rather than expanded opportunistically.
- If an upstream merge conflicts with a port, preserve current desktop
  behavior first, then reapply only the still-relevant enhancement behavior.

## Licensing and Attribution

- Preserve the upstream `LICENSE` unchanged (GNU GPL v3.0).
- Preserve existing copyright and third-party notices.
- Add an unofficial-fork notice and credits for NuvioMedia and Luqman to the
  README without implying endorsement.
- Maintain `docs/ports/ENHANCED_PORTS.md` with feature name, status, source
  commits, adapted desktop files, tests, and platform verification.
- Copy third-party assets or dependencies only when required by a selected
  desktop feature, together with their applicable license notice.

## Acceptance Criteria

The initial project setup is complete when:

- Both requested GitHub forks exist under `Laviora`.
- The product fork is named `NuvioDesktop-Enhanced` and uses the documented
  remotes and branches.
- `Dev` matches the pinned official desktop base and `enhanced` contains only
  documented project setup and feature ports.
- GPL licensing, upstream attribution, Luqman attribution, and the porting
  ledger are present.
- Linux, Android, and iOS implementation work has not been introduced.
- Budget/revenue and desktop keyboard shortcuts are committed separately with
  tests and source attribution.
- Fresh verification results identify the exact macOS and Windows checks that
  passed, failed, or could not run.
- The final handoff lists completed ports and the ordered remaining inventory.
