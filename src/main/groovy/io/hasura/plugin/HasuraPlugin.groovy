package io.hasura.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.github.plecong.hogan.Hogan

import io.hasura.db.util.Configuration
import io.hasura.db.util.GenerationUtil

class HasuraPlugin implements Plugin<Project> {

    private final ClassLoader loader = getClass().classLoader

    @Override
    void apply(Project target) {
        Properties properties = new Properties()
        properties.load(target.rootProject.file('hasura.properties').newDataInputStream())

        def adminToken = properties.getProperty('admin.token')
        def projectName = properties.getProperty('project.name')
        def mainOutputDir = projectName + '/src/main/java/io/hasura/' + projectName
        // pass this to dbCodegen
        def dbDir = mainOutputDir + '/data'
        // pass this to swagger codegen
        def apiDir = mainOutputDir + '/api'

        def dbCodegenTask = target.tasks.create('dbcodegen') << {
            println 'Hasura Magic: Generating models for your tables!...'
            // Build Generation Configuration
            def cfg = new Configuration();
            cfg.setDir(dbDir)
            cfg.setPackageName('io.hasura.' + projectName + '.data')
            cfg.setDBUrl('https://data.' + projectName + '.hasura-app.io')
            cfg.setDBPrefix('/v1')
            cfg.setAdminAPIKey(adminToken)
            // Run the code generator
            GenerationUtil.generate(cfg)
        }
        dbCodegenTask.group = "codegen"
        dbCodegenTask.description = "Just generates the tables code"

        def codegenTask = target.tasks.create('codegen').dependsOn(dbCodegenTask) << {
            // copy the build.gradle file
            def gradleContents = loader.getResource('dev.build.gradle.mustache').getText()
            def destGradleFile = new File(target.projectDir.absolutePath, projectName + '/build.gradle')
            destGradleFile.parentFile.mkdirs()
            destGradleFile.write(gradleContents)

            // template the copy the AndroidManifest.xml file
            def manifestContents = loader.getResource('AndroidManifest.xml.mustache').getText()
            def data = [ projectName: projectName ]
            def template = Hogan.compile(manifestContents)
            def output = template.render(data)
            def destManifestFile = new File(target.projectDir.absolutePath, projectName + '/src/main/AndroidManifest.xml')
            destManifestFile.parentFile.mkdirs()
            destManifestFile.write(output)
        }
        /*
        def codegenTask = target.tasks.create("codegen", Copy.class, new Action<Copy>() {
            @Override
            public void execute(Copy copy) {
                // copy the build.gradle file
                def gradleContents = loader.getResource('build.gradle.mustache').getText()
                def destGradleFile = new File(target.projectDir.absolutePath, projectName + '/build.gradle')
                destGradleFile.parentFile.mkdirs()
                destGradleFile.write(gradleContents)

                // copy the AndroidManifest.xml file
                def manifestContents = loader.getResource('AndroidManifest.xml.mustache').getText()
                def data = [ projectName: projectName]
                def template = Hogan.compile(manifestContents)
                def output = template.render(data)
                def destManifestFile = new File(target.projectDir.absolutePath, projectName + '/src/main/AndroidManifest.xml')
                destManifestFile.parentFile.mkdirs()
                destManifestFile.write(output)
            }
        }).dependsOn(dbCodegenTask);
        */
        codegenTask.group = "codegen"
        codegenTask.description = "Overall task that does all the codegen and builds a JAR in the right place"
    }
}
