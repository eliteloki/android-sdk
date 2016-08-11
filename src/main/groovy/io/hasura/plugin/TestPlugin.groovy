package io.hasura.plugin

import io.hasura.db.util.Configuration
import org.gradle.api.Plugin
import org.gradle.api.Project
import io.hasura.db.util.GenerationUtil

/**
 * Created by tanmaigopal on 11/08/16.
 */
class TestPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        def dbCodegenTask = target.tasks.create("dbCodegen") << {

            println 'Running dbCodegen. So exciting!...'
            // Build Generation Configuration
            // ------------------------------
            def cfg = new Configuration();
            cfg.setDir('chumma')
            cfg.setPackageName('io.hasura-app.project76.data')
            cfg.setDBUrl('https://data.nonslip53.hasura-app.io')
            cfg.setDBPrefix('/v1')
            cfg.setAdminAPIKey('phpez9z174xdsc8gs0b6f1zobi7m0l2p')

            // Run the code generator
            // ----------------------
            GenerationUtil.generate(cfg)
        }
        dbCodegenTask.group = "DB Codegen"
        dbCodegenTask.description = "Generates tables by looking at the schema"
    }
}
