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
| Stream-list search and filtering | [`8cfcba5`](https://github.com/luqmanfadlli/NuvioMobile-Enhanced/commit/8cfcba5e5311020ec08f2fbe943bc5af5bc6209c) | Optional desktop search setting, profile-scoped persistence, wide/compact UI, and provider-aware multi-term filtering | `StreamSearchTest` covers disabled/blank input, metadata matching, and selected-provider empty results | macOS arm64/x86_64 and Windows x64 CI pass |
| More Like This: View All | [`913fe7b`](https://github.com/luqmanfadlli/NuvioMobile-Enhanced/commit/913fe7b50baa1ac2aa273c647d9e5b9a8f36b9dc) | Existing desktop catalog grid with TMDB/Trakt pagination and source-aware navigation; non-paginated SIMKL recommendations remain rail-only | Catalog page/visibility policy, TMDB and Trakt pagination, and desktop Compose action tests | macOS arm64/x86_64 and Windows x64 CI pass |
| Detail-page icon action row | [`fc09cb1`](https://github.com/luqmanfadlli/NuvioMobile-Enhanced/commit/fc09cb18e6af8c63aae1d4b7122727d24dce1df9), [`40faf88`](https://github.com/luqmanfadlli/NuvioMobile-Enhanced/commit/40faf88abca3e556f4e7258bcca1658cd29c1120) | Default-on full-width Play/Resume button with a responsive icon row for Start from beginning, Watched, and Library; disabling the setting restores the upstream overflow layout. Unsupported or separately unported actions remain hidden. | Layout fitting, backward-compatible settings codec, and desktop Compose interaction tests | macOS arm64/x86_64 and Windows x64 CI pass |
| Random episode selection | [`295c45c`](https://github.com/luqmanfadlli/NuvioMobile-Enhanced/commit/295c45c5e45d467df59270c29c7a8ad7f1c45aa7) | Series-only Shuffle action in both icon-row and overflow layouts; selects from released playable episodes and reuses desktop episode playback | Eligibility/release filtering, deterministic selection, and desktop Compose interaction tests | macOS arm64/x86_64 and Windows x64 CI pass |

## Planned inventory

### Phase 2 — shared or low-risk desktop adaptations

- Hero card styles, dynamic backgrounds, accent treatments, and trailer preferences
- Custom profile backgrounds
- Episode ratings
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

Stream-list search verification passed in [Desktop Enhanced CI run 36443754516](https://github.com/Laviora/NuvioDesktop-Enhanced/actions/runs/36443754516): the Enhanced regression suite, macOS arm64 and x86_64 DMGs, and the Windows x64 MSI all succeeded.

More Like This: View All verification passed in [Desktop Enhanced CI run 36450106960](https://github.com/Laviora/NuvioDesktop-Enhanced/actions/runs/36450106960): the Enhanced regression suite, macOS arm64 and x86_64 DMGs, and the Windows x64 MSI all succeeded.

Detail-page icon action row verification passed in [Desktop Enhanced CI run 36557269898](https://github.com/Laviora/NuvioDesktop-Enhanced/actions/runs/36557269898): the Enhanced regression suite, macOS arm64 and x86_64 DMGs, and the Windows x64 MSI all succeeded.

Random episode selection verification passed in [Desktop Enhanced CI run 36562760351](https://github.com/Laviora/NuvioDesktop-Enhanced/actions/runs/36562760351): the Enhanced regression suite, macOS arm64 and x86_64 DMGs, and the Windows x64 MSI all succeeded.

The CI test job compiles the complete desktop source and runs Enhanced-specific regression tests.
The unfiltered upstream desktop suite is not used as a required check because 27 unrelated tests
(remote-image behavior, hero layout, and native-player teardown) fail on a clean GitHub macOS arm64
runner at the pinned upstream revision.
