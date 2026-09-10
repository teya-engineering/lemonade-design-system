import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import java.io.File

/**
 * Each fixture is a real unified diff produced by running BCV's `apiDump` task
 * twice — once with a "before" source file, once with an "after" — and capturing
 * `diff -u -U999999` between the two baseline snapshots. See
 * `kmp/scripts/capture-all-fixtures.sh` for the source pairs used.
 *
 * Tests are parameterized over the three bucket directories under
 * `src/test/resources/fixtures/`:
 *  - `additions-only/` → must classify as [Verdict.AdditionsOnly]
 *  - `breaking/`       → must classify as [Verdict.Breaking]
 *  - `no-changes/`     → must classify as [Verdict.NoChanges]
 */
class ApiStabilityClassifierTest {

    @TestFactory
    fun `additions-only fixtures classify as AdditionsOnly`(): List<DynamicTest> =
        loadBucket("additions-only")
            .map { fixture ->
                DynamicTest.dynamicTest(fixture.name) {
                    val verdict = ApiStabilityClassifier.classify(fixture.diff)
                    assertEquals(
                        expected = Verdict.AdditionsOnly,
                        actual = verdict,
                        message = "Expected ${fixture.name} to classify as AdditionsOnly but got $verdict",
                    )
                }
            }

    @TestFactory
    fun `breaking fixtures classify as Breaking`(): List<DynamicTest> =
        loadBucket("breaking")
            .map { fixture ->
                DynamicTest.dynamicTest(fixture.name) {
                    val verdict = ApiStabilityClassifier.classify(fixture.diff)
                    assertIs<Verdict.Breaking>(
                        value = verdict,
                        message = "Expected ${fixture.name} to classify as Breaking but got $verdict",
                    )
                    assertTrue(
                        actual = verdict.reasons.isNotEmpty(),
                        message = "Breaking verdict for ${fixture.name} produced no reasons",
                    )
                }
            }

    @TestFactory
    fun `no-changes fixtures classify as NoChanges`(): List<DynamicTest> =
        loadBucket("no-changes")
            .map { fixture ->
                DynamicTest.dynamicTest(fixture.name) {
                    val verdict = ApiStabilityClassifier.classify(fixture.diff)
                    assertEquals(
                        expected = Verdict.NoChanges,
                        actual = verdict,
                        message = "Expected ${fixture.name} to classify as NoChanges but got $verdict",
                    )
                }
            }

    @Test
    fun `abstract member added to existing interface mentions abstract in reasons`() {
        val fixture = loadFixture("breaking/11-add-abstract-to-existing-iface.diff")
        val verdict = ApiStabilityClassifier.classify(fixture)
        assertIs<Verdict.Breaking>(verdict)
        assertTrue(
            actual = verdict.reasons.any { reason ->
                reason.contains(
                    other = "abstract",
                    ignoreCase = true,
                )
            },
            message = "Expected an 'abstract member' reason. Reasons: ${verdict.reasons}",
        )
    }

    @Test
    fun `abstract member added to existing abstract class mentions abstract in reasons`() {
        val fixture = loadFixture("breaking/12-add-abstract-to-existing-class.diff")
        val verdict = ApiStabilityClassifier.classify(fixture)
        assertIs<Verdict.Breaking>(verdict)
        assertTrue(
            actual = verdict.reasons.any { reason ->
                reason.contains(
                    other = "abstract",
                    ignoreCase = true,
                )
            },
            message = "Expected an 'abstract member' reason. Reasons: ${verdict.reasons}",
        )
    }

    @Test
    fun `removed function mentions removed in reasons`() {
        val fixture = loadFixture("breaking/07-remove-fun.diff")
        val verdict = ApiStabilityClassifier.classify(fixture)
        assertIs<Verdict.Breaking>(verdict)
        assertTrue(
            actual = verdict.reasons.any { reason ->
                reason.contains(
                    other = "removed",
                    ignoreCase = true,
                )
            },
            message = "Expected a 'removed' reason. Reasons: ${verdict.reasons}",
        )
    }

    @Test
    fun `default-parameter addition is classified breaking because JVM signature changes`() {
        val fixture = loadFixture("breaking/08-add-default-param.diff")
        val verdict = ApiStabilityClassifier.classify(fixture)
        assertIs<Verdict.Breaking>(verdict)
    }

    @Test
    fun `brand-new interface with abstract members is additions-only despite abstract keyword`() {
        val fixture = loadFixture("additions-only/05-brand-new-iface-with-abstracts.diff")
        val verdict = ApiStabilityClassifier.classify(fixture)
        assertEquals(
            expected = Verdict.AdditionsOnly,
            actual = verdict,
        )
    }

    @Test
    fun `default-body method on existing interface is additions-only (open or non-abstract)`() {
        val fixture = loadFixture("additions-only/06-add-default-method-existing-iface.diff")
        val verdict = ApiStabilityClassifier.classify(fixture)
        assertEquals(
            expected = Verdict.AdditionsOnly,
            actual = verdict,
        )
    }

    @Test
    fun `default-param add that keeps the old signature as synthetic is additions-only`() {
        val diff = """
            --- a/kmp/core/api/android/core.api
            +++ b/kmp/core/api/android/core.api
            @@ -1,3 +1,5 @@
             public final class com/teya/lemonade/core/BcvKt {
            -${"\t"}public static final fun bcvHiddenOverload (Ljava/lang/String;)Ljava/lang/String;
            +${"\t"}public static final synthetic fun bcvHiddenOverload (Ljava/lang/String;)Ljava/lang/String;
            +${"\t"}public static final fun bcvHiddenOverload (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
            +${"\t"}public static synthetic fun bcvHiddenOverload${'$'}default (Ljava/lang/String;Ljava/lang/String;ILjava/lang/Object;)Ljava/lang/String;
             }
        """.trimIndent()
        assertEquals(
            expected = Verdict.AdditionsOnly,
            actual = ApiStabilityClassifier.classify(diff),
        )
    }

    @Test
    fun `real removal is not masked by a synthetic addition in a different file`() {
        val diff = """
            --- a/kmp/core/api/android/core.api
            +++ b/kmp/core/api/android/core.api
            @@ -1,2 +1,1 @@
             public final class com/teya/lemonade/core/BcvKt {
            -${"\t"}public static final fun bcvGone (Ljava/lang/String;)Ljava/lang/String;
             }
            --- a/kmp/core/api/desktop/core.api
            +++ b/kmp/core/api/desktop/core.api
            @@ -1,2 +1,2 @@
             public final class com/teya/lemonade/core/BcvKt {
            -${"\t"}public static final fun bcvGone (Ljava/lang/String;)Ljava/lang/String;
            +${"\t"}public static final synthetic fun bcvGone (Ljava/lang/String;)Ljava/lang/String;
             }
        """.trimIndent()
        val verdict = ApiStabilityClassifier.classify(diff)
        assertIs<Verdict.Breaking>(verdict)
        assertTrue(
            actual = verdict.reasons.any { reason -> reason.contains("bcvGone") },
            message = "Expected the android-only removal to be flagged. Reasons: ${verdict.reasons}",
        )
    }

    @Test
    fun `data class grown with HIDDEN constructor and copy shims is additions-only`() {
        val diff = """
            --- a/kmp/core/api/android/core.api
            +++ b/kmp/core/api/android/core.api
            @@ -1,6 +1,12 @@
             public final class com/teya/lemonade/core/BcvDataModel {
            -${"\t"}public fun <init> (Ljava/lang/String;)V
            +${"\t"}public synthetic fun <init> (Ljava/lang/String;)V
            +${"\t"}public fun <init> (Ljava/lang/String;I)V
            +${"\t"}public synthetic fun <init> (Ljava/lang/String;IILkotlin/jvm/internal/DefaultConstructorMarker;)V
             ${"\t"}public final fun component1 ()Ljava/lang/String;
            +${"\t"}public final fun component2 ()I
            -${"\t"}public final fun copy (Ljava/lang/String;)Lcom/teya/lemonade/core/BcvDataModel;
            +${"\t"}public final synthetic fun copy (Ljava/lang/String;)Lcom/teya/lemonade/core/BcvDataModel;
            +${"\t"}public final fun copy (Ljava/lang/String;I)Lcom/teya/lemonade/core/BcvDataModel;
            +${"\t"}public static synthetic fun copy${'$'}default (Lcom/teya/lemonade/core/BcvDataModel;Ljava/lang/String;IILjava/lang/Object;)Lcom/teya/lemonade/core/BcvDataModel;
             ${"\t"}public static synthetic fun copy${'$'}default (Lcom/teya/lemonade/core/BcvDataModel;Ljava/lang/String;ILjava/lang/Object;)Lcom/teya/lemonade/core/BcvDataModel;
            +${"\t"}public final fun getB ()I
             }
        """.trimIndent()
        assertEquals(
            expected = Verdict.AdditionsOnly,
            actual = ApiStabilityClassifier.classify(diff),
        )
    }

    @Test
    fun `data class grown WITHOUT the copy shim is still breaking`() {
        val diff = """
            --- a/kmp/core/api/android/core.api
            +++ b/kmp/core/api/android/core.api
            @@ -1,4 +1,5 @@
             public final class com/teya/lemonade/core/BcvDataModel {
            -${"\t"}public final fun copy (Ljava/lang/String;)Lcom/teya/lemonade/core/BcvDataModel;
            +${"\t"}public final fun copy (Ljava/lang/String;I)Lcom/teya/lemonade/core/BcvDataModel;
             }
        """.trimIndent()
        val verdict = ApiStabilityClassifier.classify(diff)
        assertIs<Verdict.Breaking>(verdict)
        assertTrue(
            actual = verdict.reasons.any { reason -> reason.contains("copy") },
            message = "Expected the unshimmed copy change to be flagged. Reasons: ${verdict.reasons}",
        )
    }

    @Test
    fun `re-hashed Compose getLambda singleton on desktop is additions-only`() {
        // Adding a parameter to the enclosing @Composable re-hashes the ComposableSingletons lambda key.
        val diff = """
            --- a/kmp/expressive/api/desktop/expressive.api
            +++ b/kmp/expressive/api/desktop/expressive.api
            @@ -1,4 +1,4 @@
             public final class com/teya/lemonade/ComposableSingletons${'$'}BottomSheetKt {
             ${"\t"}public static final field INSTANCE Lcom/teya/lemonade/ComposableSingletons${'$'}BottomSheetKt;
            -${"\t"}public final fun getLambda${'$'}-968066332${'$'}expressive ()Lkotlin/jvm/functions/Function2;
            +${"\t"}public final fun getLambda${'$'}1980115960${'$'}expressive ()Lkotlin/jvm/functions/Function2;
             }
        """.trimIndent()
        assertEquals(
            expected = Verdict.AdditionsOnly,
            actual = ApiStabilityClassifier.classify(diff),
        )
    }

    @Test
    fun `re-hashed Compose getLambda singleton on android (variant-suffixed module) is additions-only`() {
        val diff = """
            --- a/kmp/expressive/api/android/expressive.api
            +++ b/kmp/expressive/api/android/expressive.api
            @@ -1,3 +1,3 @@
             public final class com/teya/lemonade/ComposableSingletons${'$'}BottomSheetKt {
            -${"\t"}public final fun getLambda${'$'}-968066332${'$'}expressive_release ()Lkotlin/jvm/functions/Function2;
            +${"\t"}public final fun getLambda${'$'}1980115960${'$'}expressive_release ()Lkotlin/jvm/functions/Function2;
             }
        """.trimIndent()
        assertEquals(
            expected = Verdict.AdditionsOnly,
            actual = ApiStabilityClassifier.classify(diff),
        )
    }

    @Test
    fun `removing a mangled internal symbol outright is additions-only`() {
        val diff = """
            --- a/kmp/core/api/desktop/core.api
            +++ b/kmp/core/api/desktop/core.api
            @@ -1,3 +1,2 @@
             public final class com/teya/lemonade/core/ComposableSingletons${'$'}WidgetKt {
            -${"\t"}public final fun getLambda${'$'}123456${'$'}core ()Lkotlin/jvm/functions/Function2;
             }
        """.trimIndent()
        assertEquals(
            expected = Verdict.AdditionsOnly,
            actual = ApiStabilityClassifier.classify(diff),
        )
    }

    @Test
    fun `removing a default-argument synthetic is still breaking (not mistaken for mangling)`() {
        // `foo$default` ends in `default`, not the module name, so it falls outside the
        // mangling carve-out — removing one breaks defaulted call sites.
        val diff = """
            --- a/kmp/core/api/android/core.api
            +++ b/kmp/core/api/android/core.api
            @@ -1,3 +1,2 @@
             public final class com/teya/lemonade/core/BcvKt {
            -${"\t"}public static synthetic fun foo${'$'}default (Ljava/lang/String;ILjava/lang/Object;)Ljava/lang/String;
             }
        """.trimIndent()
        val verdict = ApiStabilityClassifier.classify(diff)
        assertIs<Verdict.Breaking>(verdict)
        assertTrue(
            actual = verdict.reasons.any { reason -> reason.contains("foo\$default") },
            message = "Expected the removed default-arg synthetic to stay flagged. Reasons: ${verdict.reasons}",
        )
    }

    @Test
    fun `empty input is NoChanges`() {
        assertEquals(
            expected = Verdict.NoChanges,
            actual = ApiStabilityClassifier.classify(""),
        )
        assertEquals(
            expected = Verdict.NoChanges,
            actual = ApiStabilityClassifier.classify("   \n  \n"),
        )
    }

    private data class Fixture(
        val name: String,
        val diff: String,
    )

    private fun loadBucket(bucket: String): List<Fixture> {
        val dir = fixtureDir()
            .resolve(bucket)
        check(dir.isDirectory) { "Fixture directory missing: $dir" }
        return dir.listFiles { _, name -> name.endsWith(".diff") }
            .orEmpty()
            .sortedBy { file -> file.name }
            .map { file ->
                Fixture(
                    name = file.name,
                    diff = file.readText(),
                )
            }
            .also { fixtures ->
                check(fixtures.isNotEmpty()) { "No .diff fixtures in $dir" }
            }
    }

    private fun loadFixture(relative: String): String =
        fixtureDir()
            .resolve(relative)
            .readText()

    private fun fixtureDir(): File {
        val resource = checkNotNull(this::class.java.classLoader.getResource("fixtures")) {
            "fixtures resource directory not found on test classpath"
        }
        return File(resource.toURI())
    }
}
