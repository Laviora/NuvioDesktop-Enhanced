# Enhanced Porting Ledger

Nuvio Desktop Enhanced is an unofficial GPL-3.0 fork of
[Nuvio Desktop](https://github.com/NuvioMedia/NuvioDesktop). Selected behavior is adapted from
[Nuvio Mobile Enhanced](https://github.com/luqmanfadlli/NuvioMobile-Enhanced), with source commit
attribution retained in this ledger and in the relevant Git commits.

## Repository policy

- `Dev` tracks `NuvioMedia/NuvioDesktop:Dev` and contains no Enhanced-only changes.
- `enhanced` is the product branch and tracks `Laviora/NuvioDesktop-Enhanced:enhanced`.
- The `enhanced` remote points to Luqman's original repository and has pushing disabled.
- Maintained build targets are macOS and Windows. Platform-only Android, iOS, and Linux work is out of scope.
- Ports should preserve upstream structure, remain feature-scoped, and include `Ported-from` and
  `Co-authored-by` commit trailers when source is adapted.

## Completed

| Feature | Source | Desktop adaptation | Tests | Status |
| --- | --- | --- | --- | --- |
| Movie budget and revenue | [`373d34b`](https://github.com/luqmanfadlli/NuvioMobile-Enhanced/commit/373d34b339b9877a6de5a5b7f57fe95a2185e1b9) | Common currency formatter, `MetaDetails`, TMDB enrichment, and additional-info rows | Formatter, TMDB mapping/settings, and desktop Compose UI | macOS arm64/x86_64 and Windows x64 CI pass |
| Hardware keyboard shortcuts | Existing desktop implementation; no mobile code copied | `PlayerControlsAction`, `player-ui/controls.js`, and `NativePlayerController.kt` already cover desktop keyboard input | `DesktopPlayerKeyboardShortcutsTest` protects Space, arrows, Escape, modal, and text-entry behavior | Characterized; no port required |

## Planned inventory

### Phase 2 — shared or low-risk desktop adaptations

- Stream-list search and filtering
- Hero card styles, dynamic backgrounds, accent treatments, and trailer preferences
- Custom profile backgrounds
- More Like This: View All
- Episode ratings and details icon action row
- Random episode selection
- Pinned stream sources
- Trakt and SIMKL device-code sign-in

### Phase 3 — native player or larger features

- Tap-to-seek and gesture readouts
- Playback information and quality selection
- Volume boost
- Subtitle background transparency
- Profile Insights
- Library calendar

### Deferred pending architecture/security review

- Live TV integrations and externally hosted provider catalogs
- Debug-log export and other diagnostics that may expose private data

### Out of scope

- Android- or iOS-only downloads, background execution, PiP, fonts, and visual-system work
- Linux-specific implementation, packaging, or CI

## Platform verification

The `Desktop Enhanced CI` workflow is the source of truth for macOS and Windows verification.
Individual feature rows must not claim a platform passes until the corresponding CI job succeeds.

Initial verification passed in [Desktop Enhanced CI run 36415752928](https://github.com/Laviora/NuvioDesktop-Enhanced/actions/runs/36415752928): desktop compilation and Enhanced tests, macOS arm64 and x86_64 DMGs, and the Windows x64 MSI all succeeded.

The CI test job compiles the complete desktop source and runs Enhanced-specific regression tests.
The unfiltered upstream desktop suite is not used as a required check because 27 unrelated tests
(remote-image behavior, hero layout, and native-player teardown) fail on a clean GitHub macOS arm64
runner at the pinned upstream revision.
