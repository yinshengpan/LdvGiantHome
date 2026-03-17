import com.ledvance.build.logic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/20 11:10
 * Describe : AndroidKotlinxSerializationConventionPlugin
 */
class AndroidKotlinxSerializationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
            dependencies {
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
            }
        }
    }
}