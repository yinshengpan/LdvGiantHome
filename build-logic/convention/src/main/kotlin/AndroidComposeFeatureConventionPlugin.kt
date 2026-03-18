import com.ledvance.build.logic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 11:09
 * Describe : AndroidComposeFeatureConventionPlugin
 */
class AndroidComposeFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "ledvance.android.library")
            apply(plugin = "ledvance.android.library.compose")
            apply(plugin = "ledvance.android.hilt")
            apply(plugin = "ledvance.kotlinx.serialization")

            dependencies {
                "implementation"(project(":core:utils"))
                "implementation"(project(":core:ui"))

                "implementation"(libs.findLibrary("timber").get())
                "implementation"(libs.findLibrary("navigation3.runtime").get())
                "implementation"(libs.findLibrary("hilt.navigation.compose").get())
            }
        }
    }
}