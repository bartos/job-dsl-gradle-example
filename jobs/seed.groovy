// If you want, you can define your seed job in the DSL and create it via the REST API.
// See https://github.com/sheehan/job-dsl-gradle-example#rest-api-runner

job('seed') {
    scm {
        github ('bartos/job-dsl-gradle-example','develop2')

    }
    triggers {
        scm 'H/5 * * * *'
    }
    steps {
        gradle 'clean test'
        dsl {
            external 'jobs/**/*.groovy'
            additionalClasspath 'src/main/groovy'
        }
    }
    publishers {
        //if any test
        archiveJunit 'build/test-results/**/*.xml'
    }
}