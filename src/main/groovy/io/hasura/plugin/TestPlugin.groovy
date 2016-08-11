package io.hasura.plugin

import io.hasura.db.util.Configuration
import org.gradle.api.Plugin
import org.gradle.api.Project
import io.hasura.db.util.GenerationUtil
// import org.gradle.api.tasks.GradleBuild

/**
 * Created by tanmaigopal on 11/08/16.
 */
class TestPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        Properties properties = new Properties()
        properties.load(target.rootProject.file('hasura.properties').newDataInputStream())
        def adminToken = properties.getProperty('admin.token')

        def dbCodegenTask = target.tasks.create('dbcodegen') << {
            println 'Running dbCodegen. So exciting!...'

            // Build Generation Configuration
            // ------------------------------
            def cfg = new Configuration();
            cfg.setDir('chumma')
            cfg.setPackageName('io.hasura.' + target.projectName + '.data')
            cfg.setDBUrl('https://data.' + target.projectName + '.hasura-app.io')
            cfg.setDBPrefix('/v1')
            cfg.setAdminAPIKey(adminToken)

            // Run the code generator
            // ----------------------
            GenerationUtil.generate(cfg)
        }
        dbCodegenTask.group = "codegen"
        dbCodegenTask.description = "Just generates the tables code"

        def codegenTask = target.tasks.create("codegen").dependsOn(dbCodegenTask) {
            println 'DBCodegen will execute before me. Now that dbCodegen is done, I shall copy/compile them into a JAR'
            println target.projectName
        }

        codegenTask.group = "codegen"
        codegenTask.description = "Overall task that does all the codegen and builds a JAR in the right place"
    }
}
