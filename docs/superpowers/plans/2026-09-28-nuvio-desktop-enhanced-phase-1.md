# Nuvio Desktop Enhanced Phase 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Establish the user-owned Nuvio Desktop Enhanced repositories, document the porting relationship, add macOS/Windows-only verification, and port the first safe shared feature: TMDB movie budget and revenue.

**Architecture:** Keep official Nuvio Desktop `Dev` untouched as the synchronization branch and place Enhanced work on an `enhanced` branch. Adapt the small budget/revenue feature into existing `commonMain` models, TMDB enrichment, and details UI; retain the desktop-native keyboard implementation already present instead of adding Luqman's mobile/iOS shortcut bridge.

**Tech Stack:** Git/GitHub CLI, Kotlin Multiplatform, Compose Multiplatform, Kotlin serialization, Compose Desktop UI tests, Gradle/JDK 17, GitHub Actions, Git LFS, macOS DMG packaging, Windows MSI packaging.

**Spec:** `docs/superpowers/specs/2026-09-28-nuvio-desktop-enhanced-design.md`

## Global Constraints

- The implementation base is `NuvioMedia/NuvioDesktop` branch `Dev`, pinned for Phase 1 at `fe92d414024b775351d17912a19cbcf51b880241`.
- Luqman's `luqmanfadlli/NuvioMobile-Enhanced` branch `enhanced` is a read-only behavioral and attribution reference, never an application base.
- Maintained and verified build targets are macOS arm64, macOS x86_64, and Windows x64 only.
- Do not add Android, iOS, or Linux implementation work; leave existing upstream source intact unless shared compilation requires a narrowly scoped change.
- Preserve the upstream GNU GPL v3.0 `LICENSE`, existing notices, architecture, and directory structure.
- No secrets, signing identities, API keys, or release credentials may be committed.
- Each feature commit includes `Ported-from: https://github.com/luqmanfadlli/NuvioMobile-Enhanced/commit/373d34b339b9877a6de5a5b7f57fe95a2185e1b9` and, for substantially derived code, `Co-authored-by: Luqman Fadlli <luqman.fadlli@gmail.com>`.
- Documentation, repository wiring, and CI YAML are configuration work: their approved exception to red-green TDD is structural validation with Git/GitHub checks and YAML parsing. All Kotlin production behavior follows red-green-refactor.

## Review Focus

- Null, zero, and negative TMDB money values must remain unknown and produce no Budget or Revenue row; Task 4 tests the formatter and Task 6 tests the UI.
- Grouping boundaries and very large positive `Long` values must format without locale or overflow surprises; Task 4 covers `1`, `999`, `1_000`, `1_000_000`, and `Long.MAX_VALUE`.
- Disabled TMDB details enrichment must not overwrite addon-provided budget/revenue values; Task 5 covers `useDetails = false`.
- A budget-only or revenue-only enrichment must count as content and survive standalone/enrichment mapping; Task 5 covers both shapes.
- Existing desktop keyboard input must not be shadowed by a parallel Compose handler; Task 7 characterizes Space, arrows, Escape, modal/text-input guards, and confirms that no `PlayerKeyboardShortcuts.kt` is added.

---

### Task 1: Create the forks and wire the repository

**Files:**
- No tracked file changes.
- Git configuration: `.git/config`
- GitHub repositories: `Laviora/NuvioDesktop-Enhanced`, `Laviora/NuvioMobile-Enhanced`

**Interfaces:**
- Consumes: the local `enhanced` branch containing the approved spec and this plan.
- Produces: `origin`, `upstream`, and `enhanced` remotes; local `Dev` tracking `upstream/Dev`; local `enhanced` tracking `origin/enhanced`; both requested GitHub forks.

- [ ] **Step 1: Reconfirm that the target repositories do not already exist**

Run:

```bash
gh repo view Laviora/NuvioDesktop-Enhanced --json nameWithOwner 2>/dev/null || true
gh repo view Laviora/NuvioMobile-Enhanced --json nameWithOwner 2>/dev/null || true
```

Expected: neither repository resolves. If either exists, inspect its fork parent and stop rather than overwrite unrelated work.

- [ ] **Step 2: Fork official desktop and rename the fork**

Run:

```bash
gh repo fork NuvioMedia/NuvioDesktop --clone=false --remote=false
gh repo rename NuvioDesktop-Enhanced --repo Laviora/NuvioDesktop --yes
```

Expected: `Laviora/NuvioDesktop-Enhanced` reports `NuvioMedia/NuvioDesktop` as its fork parent.

- [ ] **Step 3: Fork Luqman's reference repository**

Run:

```bash
gh repo fork luqmanfadlli/NuvioMobile-Enhanced --clone=false --remote=false
```

Expected: `Laviora/NuvioMobile-Enhanced` reports the Nuvio Mobile fork network and exposes branch `enhanced`.

- [ ] **Step 4: Configure product remotes and tracking branches**

Run:

```bash
git remote rename origin upstream
git remote add origin https://github.com/Laviora/NuvioDesktop-Enhanced.git
git remote add enhanced https://github.com/luqmanfadlli/NuvioMobile-Enhanced.git
git fetch upstream Dev --depth=1 --filter=blob:none
git fetch enhanced enhanced --depth=1 --filter=tree:0
git branch --set-upstream-to=upstream/Dev Dev
git push -u origin enhanced
gh repo edit Laviora/NuvioDesktop-Enhanced --default-branch enhanced
```

Expected: `git remote -v` shows the three documented remotes, `Dev` tracks `upstream/Dev`, `enhanced` tracks `origin/enhanced`, and the GitHub default branch is `enhanced`.

- [ ] **Step 5: Materialize the full source checkout without automatically downloading every LFS binary**

Run:

```bash
GIT_LFS_SKIP_SMUDGE=1 git sparse-checkout disable
git status --short --branch
```

Expected: all tracked source is available, the worktree is clean, and HEAD remains on `enhanced`.

- [ ] **Step 6: Verify fork lineage and branch topology**

Run:

```bash
gh api repos/Laviora/NuvioDesktop-Enhanced --jq '{name:.full_name,parent:.parent.full_name,default:.default_branch}'
gh api repos/Laviora/NuvioMobile-Enhanced --jq '{name:.full_name,parent:.parent.full_name,default:.default_branch}'
git branch -vv
git remote -v
```

Expected: desktop parent `NuvioMedia/NuvioDesktop`, product default `enhanced`, reference fork present, and no Enhanced commit on local `Dev`.

### Task 2: Add unofficial-fork attribution and the porting ledger

**Files:**
- Modify: `README.md`
- Create: `docs/ports/ENHANCED_PORTS.md`

**Interfaces:**
- Consumes: repository identity and branch/remotes from Task 1.
- Produces: user-facing project scope and an auditable record for every adapted Enhanced feature.

- [ ] **Step 1: Update the README identity and scope**

Change the title and introductory copy to “Nuvio Desktop Enhanced,” add an “Unofficial fork” notice, identify official Nuvio Desktop as the base and Luqman's Nuvio Mobile Enhanced as the porting reference, and state that maintained builds are macOS and Windows only. Retain the existing GPL, legal, upstream installation/build, and technology information unless a link now points at the wrong repository.

- [ ] **Step 2: Create the porting ledger**

Create `docs/ports/ENHANCED_PORTS.md` with:

- Repository/branch policy and attribution rules.
- A Phase 1 row for Budget and Revenue with source commit `373d34b339b9877a6de5a5b7f57fe95a2185e1b9`, planned files, tests, and macOS/Windows status.
- A row marking Hardware Keyboard Shortcuts as “already in desktop; no mobile code copied,” listing `PlayerControlsAction`, `controls.js`, and `NativePlayerController.kt` as evidence.
- The remaining inventory grouped as Phase 2, Phase 3, deferred, or out of scope, matching the spec.

- [ ] **Step 3: Validate documentation structure**

Run:

```bash
git diff --check
rg -n 'Unofficial|NuvioMedia/NuvioDesktop|luqmanfadlli/NuvioMobile-Enhanced|macOS|Windows|GPL' README.md docs/ports/ENHANCED_PORTS.md
```

Expected: no whitespace errors and every required attribution/scope term is present.

- [ ] **Step 4: Commit documentation**

Run:

```bash
git add README.md docs/ports/ENHANCED_PORTS.md
git commit -m "docs: establish Desktop Enhanced attribution"
```

### Task 3: Add macOS and Windows Enhanced CI

**Files:**
- Create: `.github/workflows/desktop-enhanced-ci.yml`
- Modify: `docs/ports/ENHANCED_PORTS.md`

**Interfaces:**
- Consumes: official runtime-fetch and packaging steps from `.github/workflows/desktop-release.yml`.
- Produces: a workflow named `Desktop Enhanced CI` triggered by pull requests, pushes to `enhanced`, and manual dispatch; jobs `desktop-tests`, `macos-package` (arm64/x86_64), and `windows-package` (x64).

- [ ] **Step 1: Create the workflow using official build paths**

Use JDK 17 and current pinned official action versions. Run `:composeApp:desktopTest` and `:composeApp:compileKotlinDesktop`; package unsigned macOS DMGs with `:composeApp:packageReleaseDmg` on `macos-15` and `macos-15-intel`; package Windows with `:composeApp:packageReleaseMsi` on `windows-2022`, installing the same WebView2 SDK and fetching the same Windows libmpv/TorrServer LFS paths as the official release workflow. Do not invoke Sentry upload, signing, notarization, publishing, Linux jobs, or secrets.

- [ ] **Step 2: Add artifact assertions**

The macOS jobs must fail unless exactly one DMG for the requested architecture exists. The Windows job must fail unless the MSI exists and its extracted application JAR contains `player_bridge.dll`, `libmpv-2.dll`, `WebView2Loader.dll`, the VC runtime, `runtime-files.txt`, and `TorrServer.exe`, reusing the official verification logic.

- [ ] **Step 3: Validate YAML and platform scope**

Run:

```bash
ruby -e 'require "yaml"; YAML.load_file(".github/workflows/desktop-enhanced-ci.yml"); puts "yaml-ok"'
rg -n 'macos-15|macos-15-intel|windows-2022|packageReleaseDmg|packageReleaseMsi' .github/workflows/desktop-enhanced-ci.yml
if rg -ni 'ubuntu|linux|packageReleaseDeb|packageReleaseRpm|AppImage|Flatpak' .github/workflows/desktop-enhanced-ci.yml; then exit 1; fi
git diff --check
```

Expected: `yaml-ok`, all macOS/Windows markers present, no Linux marker, and no whitespace errors.

- [ ] **Step 4: Record the CI path and commit**

Update the ledger's platform-verification section to name `Desktop Enhanced CI`, then run:

```bash
git add .github/workflows/desktop-enhanced-ci.yml docs/ports/ENHANCED_PORTS.md
git commit -m "ci: verify macOS and Windows desktop builds"
```

### Task 4: Port and test USD amount formatting

**Files:**
- Create: `composeApp/src/commonTest/kotlin/com/nuvio/app/core/format/CurrencyDisplayTest.kt`
- Create: `composeApp/src/commonMain/kotlin/com/nuvio/app/core/format/CurrencyDisplay.kt`

**Interfaces:**
- Consumes: nullable whole-dollar amounts from TMDB.
- Produces: `fun formatUsdAmountForDisplay(amount: Long?): String?`.

- [ ] **Step 1: Write the failing formatter tests**

```kotlin
class CurrencyDisplayTest {
    @Test
    fun nonPositiveOrMissingAmountsAreUnknown() {
        assertNull(formatUsdAmountForDisplay(null))
        assertNull(formatUsdAmountForDisplay(0L))
        assertNull(formatUsdAmountForDisplay(-1L))
    }

    @Test
    fun positiveAmountsUseDollarPrefixAndCommaGrouping() {
        assertEquals("\$1", formatUsdAmountForDisplay(1L))
        assertEquals("\$999", formatUsdAmountForDisplay(999L))
        assertEquals("\$1,000", formatUsdAmountForDisplay(1_000L))
        assertEquals("\$1,000,000", formatUsdAmountForDisplay(1_000_000L))
        assertEquals("\$9,223,372,036,854,775,807", formatUsdAmountForDisplay(Long.MAX_VALUE))
    }
}
```

- [ ] **Step 2: Run the focused test and confirm RED**

Run:

```bash
./gradlew :composeApp:desktopTest --tests 'com.nuvio.app.core.format.CurrencyDisplayTest'
```

Expected: compilation fails because `formatUsdAmountForDisplay` does not exist.

- [ ] **Step 3: Implement `formatUsdAmountForDisplay(amount: Long?): String?`**

Return `null` for null or non-positive values. For positive values, prefix `$` and insert commas every three digits using common Kotlin only; do not introduce locale APIs or a dependency.

- [ ] **Step 4: Run the focused test and confirm GREEN**

Run the Step 2 command again.

Expected: all `CurrencyDisplayTest` cases pass.

- [ ] **Step 5: Commit the formatter**

Run:

```bash
git add composeApp/src/commonMain/kotlin/com/nuvio/app/core/format/CurrencyDisplay.kt composeApp/src/commonTest/kotlin/com/nuvio/app/core/format/CurrencyDisplayTest.kt
git commit -m "feat(details): format TMDB money amounts" -m "Ported-from: https://github.com/luqmanfadlli/NuvioMobile-Enhanced/commit/373d34b339b9877a6de5a5b7f57fe95a2185e1b9" -m "Co-authored-by: Luqman Fadlli <luqman.fadlli@gmail.com>"
```

### Task 5: Propagate budget and revenue through TMDB enrichment

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/nuvio/app/features/details/MetaDetailsModels.kt:5-40`
- Modify: `composeApp/src/commonMain/kotlin/com/nuvio/app/features/tmdb/TmdbMetadataService.kt:757-830,1036-1067,1400-1448,1827-1845`
- Modify: `composeApp/src/commonTest/kotlin/com/nuvio/app/features/tmdb/TmdbMetadataServiceTest.kt:1-210`

**Interfaces:**
- Consumes: TMDB `budget` and `revenue` JSON fields as nullable `Long` values.
- Produces: `MetaDetails.budget: Long?`, `MetaDetails.revenue: Long?`, `TmdbEnrichment.budget: Long?`, and `TmdbEnrichment.revenue: Long?`.

- [ ] **Step 1: Add failing standalone/enrichment tests**

Extend `TmdbMetadataServiceTest` with these assertions:

```kotlin
@Test
fun `buildStandaloneMeta maps budget and revenue`() {
    val enrichment = testEnrichment(budget = 5_000_000L, revenue = 12_500_000L)
    val result = TmdbMetadataService.buildStandaloneMeta("movie", "tmdb:1", 1, enrichment)
    assertEquals(5_000_000L, result.budget)
    assertEquals(12_500_000L, result.revenue)
}

@Test
fun `applyEnrichment preserves addon money when details enrichment is disabled`() {
    val base = MetaDetails("tt1", "movie", "Movie", budget = 100L, revenue = 200L)
    val result = TmdbMetadataService.applyEnrichment(
        meta = base,
        enrichment = testEnrichment(budget = 300L, revenue = 400L),
        episodeMap = emptyMap(),
        settings = TmdbSettings(enabled = true, useDetails = false),
    )
    assertEquals(100L, result.budget)
    assertEquals(200L, result.revenue)
}

@Test
fun `money-only enrichment has content`() {
    assertTrue(testEnrichment(budget = 1L, revenue = null).hasContent())
    assertTrue(testEnrichment(budget = null, revenue = 1L).hasContent())
}
```

Add a private `testEnrichment(budget: Long?, revenue: Long?): TmdbEnrichment` fixture using neutral values for existing required constructor parameters.

- [ ] **Step 2: Run the focused test and confirm RED**

Run:

```bash
./gradlew :composeApp:desktopTest --tests 'com.nuvio.app.features.tmdb.TmdbMetadataServiceTest'
```

Expected: compilation fails because the money properties do not exist.

- [ ] **Step 3: Add model fields and service propagation**

Add nullable money fields to `MetaDetails`, `TmdbEnrichment`, and private `TmdbDetailsResponse`; map them in `buildStandaloneMeta`; merge them only inside the existing `settings.useDetails` branch; include them in `TmdbEnrichment.hasContent()`; normalize API values with `takeIf { it > 0L }` when creating enrichment.

- [ ] **Step 4: Run the focused test and confirm GREEN**

Run the Step 2 command again.

Expected: all `TmdbMetadataServiceTest` cases pass.

- [ ] **Step 5: Run the formatter and TMDB tests together**

Run:

```bash
./gradlew :composeApp:desktopTest --tests 'com.nuvio.app.core.format.CurrencyDisplayTest' --tests 'com.nuvio.app.features.tmdb.TmdbMetadataServiceTest'
```

Expected: both test classes pass.

- [ ] **Step 6: Commit the data-path port**

Run:

```bash
git add composeApp/src/commonMain/kotlin/com/nuvio/app/features/details/MetaDetailsModels.kt composeApp/src/commonMain/kotlin/com/nuvio/app/features/tmdb/TmdbMetadataService.kt composeApp/src/commonTest/kotlin/com/nuvio/app/features/tmdb/TmdbMetadataServiceTest.kt
git commit -m "feat(details): propagate TMDB budget and revenue" -m "Ported-from: https://github.com/luqmanfadlli/NuvioMobile-Enhanced/commit/373d34b339b9877a6de5a5b7f57fe95a2185e1b9" -m "Co-authored-by: Luqman Fadlli <luqman.fadlli@gmail.com>"
```

### Task 6: Render budget and revenue in Movie Details

**Files:**
- Modify: `composeApp/src/commonMain/composeResources/values/strings.xml:1920-1932`
- Modify: `composeApp/src/commonMain/kotlin/com/nuvio/app/features/details/components/DetailAdditionalInfoSection.kt:1-65`
- Create: `composeApp/src/desktopTest/kotlin/com/nuvio/app/features/details/components/DetailAdditionalInfoSectionTest.kt`
- Modify: `docs/ports/ENHANCED_PORTS.md`

**Interfaces:**
- Consumes: `MetaDetails.budget`, `MetaDetails.revenue`, and `formatUsdAmountForDisplay`.
- Produces: optional `Budget` and `Revenue` rows in the existing details section.

- [ ] **Step 1: Write failing desktop UI tests**

Use `createComposeRule()` and `MaterialTheme` to render `DetailAdditionalInfoSection(showHeader = false)`.

```kotlin
@Test
fun positiveBudgetAndRevenueAreDisplayed() {
    show(MetaDetails("tt1", "movie", "Movie", budget = 1_000L, revenue = 2_500_000L))
    compose.onNodeWithText("Budget").assertIsDisplayed()
    compose.onNodeWithText("\$1,000").assertIsDisplayed()
    compose.onNodeWithText("Revenue").assertIsDisplayed()
    compose.onNodeWithText("\$2,500,000").assertIsDisplayed()
}

@Test
fun unknownBudgetAndRevenueAreOmitted() {
    show(MetaDetails("tt1", "movie", "Movie", status = "Released", budget = 0L, revenue = null))
    compose.onNodeWithText("Budget").assertDoesNotExist()
    compose.onNodeWithText("Revenue").assertDoesNotExist()
}
```

- [ ] **Step 2: Run the focused UI test and confirm RED**

Run:

```bash
./gradlew :composeApp:desktopTest --tests 'com.nuvio.app.features.details.components.DetailAdditionalInfoSectionTest'
```

Expected: the positive-value test fails because the rows do not exist.

- [ ] **Step 3: Add strings and optional rows**

Add `details_budget` = `Budget` and `details_revenue` = `Revenue`. In `DetailAdditionalInfoSection`, call `formatUsdAmountForDisplay` and append each row only when it returns a non-null string.

- [ ] **Step 4: Run the focused UI test and confirm GREEN**

Run the Step 2 command again.

Expected: both UI tests pass.

- [ ] **Step 5: Mark Budget and Revenue ported in the ledger**

Record the source commit, adapted files, focused tests, and current verification state. Do not claim platform packages pass before Task 8.

- [ ] **Step 6: Commit the UI port**

Run:

```bash
git add composeApp/src/commonMain/composeResources/values/strings.xml composeApp/src/commonMain/kotlin/com/nuvio/app/features/details/components/DetailAdditionalInfoSection.kt composeApp/src/desktopTest/kotlin/com/nuvio/app/features/details/components/DetailAdditionalInfoSectionTest.kt docs/ports/ENHANCED_PORTS.md
git commit -m "feat(details): show movie budget and revenue" -m "Ported-from: https://github.com/luqmanfadlli/NuvioMobile-Enhanced/commit/373d34b339b9877a6de5a5b7f57fe95a2185e1b9" -m "Co-authored-by: Luqman Fadlli <luqman.fadlli@gmail.com>"
```

### Task 7: Characterize the existing desktop keyboard shortcuts

**Files:**
- Create: `composeApp/src/desktopTest/kotlin/com/nuvio/app/features/player/DesktopPlayerKeyboardShortcutsTest.kt`
- Modify: `docs/ports/ENHANCED_PORTS.md`

**Interfaces:**
- Consumes: classpath resource `player-ui/controls.js` and existing `PlayerControlsAction` dispatch.
- Produces: a regression guard proving the four Enhanced shortcut behaviors already exist in the desktop-native controls path.

- [ ] **Step 1: Write the characterization test**

```kotlin
class DesktopPlayerKeyboardShortcutsTest {
    private val controlsSource by lazy {
        checkNotNull(javaClass.classLoader.getResource("player-ui/controls.js")).readText()
    }

    @Test
    fun enhancedShortcutKeysAreHandledByDesktopControls() {
        assertContains(controlsSource, "event.code === \"Space\"")
        assertContains(controlsSource, "case \"ArrowLeft\"")
        assertContains(controlsSource, "case \"ArrowRight\"")
        assertContains(controlsSource, "event.key === \"Escape\"")
    }

    @Test
    fun shortcutsYieldToModalsAndTextEntry() {
        assertContains(controlsSource, "if (activeModal || isTextEntryTarget(event.target)) return;")
        assertContains(controlsSource, "if (event.key === \"Escape\" && activeModal)")
    }
}
```

This is a characterization test for existing behavior, so it is expected to pass immediately and does not authorize production changes.

- [ ] **Step 2: Run the characterization test**

Run:

```bash
./gradlew :composeApp:desktopTest --tests 'com.nuvio.app.features.player.DesktopPlayerKeyboardShortcutsTest'
```

Expected: both tests pass against the current desktop controls resource.

- [ ] **Step 3: Confirm no parallel mobile shortcut handler was added**

Run:

```bash
test ! -e composeApp/src/commonMain/kotlin/com/nuvio/app/features/player/PlayerKeyboardShortcuts.kt
git diff upstream/Dev...HEAD -- composeApp/src/commonMain/kotlin/com/nuvio/app/features/player/PlayerEngine.kt composeApp/src/commonMain/kotlin/com/nuvio/app/features/player/PlayerScreenRuntimeUi.kt
```

Expected: the mobile handler file is absent and there is no shortcut-related shared-player duplication.

- [ ] **Step 4: Update the ledger and commit the regression guard**

Run:

```bash
git add composeApp/src/desktopTest/kotlin/com/nuvio/app/features/player/DesktopPlayerKeyboardShortcutsTest.kt docs/ports/ENHANCED_PORTS.md
git commit -m "test(player): protect existing desktop shortcuts"
```

### Task 8: Verify locally, push, and verify macOS/Windows CI

**Files:**
- No required production changes; repair only failures caused by Phase 1 changes.

**Interfaces:**
- Consumes: Tasks 1-7 and workflow `Desktop Enhanced CI`.
- Produces: fresh local test/build evidence, a pushed `enhanced` branch, and completed macOS arm64/x86_64 plus Windows x64 jobs.

- [ ] **Step 1: Run all desktop tests**

Run:

```bash
./gradlew :composeApp:desktopTest --no-daemon --stacktrace
```

Expected: BUILD SUCCESSFUL with zero failed tests. Report every pre-existing failure by name if the suite is not green.

- [ ] **Step 2: Compile the desktop target**

Run:

```bash
./gradlew :composeApp:compileKotlinDesktop --no-daemon --stacktrace
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Materialize macOS arm64 runtimes and package locally**

Run:

```bash
git lfs pull --include='composeApp/src/desktopMain/native/macos/runtime/arm64/**' --exclude=''
./gradlew :composeApp:packageReleaseDmg -Pnuvio.macos.arch=arm64 -Pcompose.desktop.packaging.checkJdkVendor=false --no-configuration-cache --no-daemon --stacktrace
```

Expected on an arm64 Mac: BUILD SUCCESSFUL and one DMG under `composeApp/build/compose/binaries/main-release/dmg/`. If the host is not arm64, record the host mismatch and rely on the arm64 CI job.

- [ ] **Step 4: Inspect the final branch before publication**

Run:

```bash
git status --short --branch
git diff --check upstream/Dev...HEAD
git log --oneline --decorate upstream/Dev..HEAD
git diff --stat upstream/Dev...HEAD
```

Expected: clean worktree; small, feature-scoped commits; no unrelated source-set changes.

- [ ] **Step 5: Enable fork Actions and push the completed branch**

Run:

```bash
gh api --method PUT repos/Laviora/NuvioDesktop-Enhanced/actions/permissions -F enabled=true -f allowed_actions=all
git push origin enhanced
```

Expected: push succeeds and `Desktop Enhanced CI` starts for the pushed commit.

- [ ] **Step 6: Wait for the workflow and inspect every required job**

Run:

```bash
run_id="$(gh run list --repo Laviora/NuvioDesktop-Enhanced --workflow desktop-enhanced-ci.yml --branch enhanced --limit 1 --json databaseId --jq '.[0].databaseId')"
gh run watch "$run_id" --repo Laviora/NuvioDesktop-Enhanced --exit-status
gh run view "$run_id" --repo Laviora/NuvioDesktop-Enhanced --json headSha,conclusion,url,jobs
```

Expected: `desktop-tests`, macOS arm64, macOS x86_64, and Windows x64 all conclude `success` for the current HEAD. Fix and re-run any Phase 1-caused failure before completion.

- [ ] **Step 7: Perform final requirement and attribution audit**

Run:

```bash
git remote -v
git branch -vv
git log --format=full -6
rg -n 'GPL|NuvioMedia|Luqman|macOS|Windows' README.md docs/ports/ENHANCED_PORTS.md
if git diff --name-only upstream/Dev...HEAD | rg -i 'composeApp/src/(android|ios)|native/linux|scripts/.*linux'; then exit 1; fi
```

Expected: correct remotes/branches, port trailers and credits present, and no Android/iOS/Linux implementation files changed.
