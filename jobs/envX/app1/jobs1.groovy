import javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation

import java.nio.file.Paths
import static com.ds.dsl.ConfigProvider.getConfig

/**
 * Created by db on 01.12.2016.
 */
getConfig(binding.variables)


buildFlowJob("${appName}_Flow") {
    description("Simple Flow Example")
    jdk("$jdk")
    properties {
        label("${nodeLabel}")
        buildNeedsWorkspace(false)
    }
    parameters {
        stringParam('Branch', '', '')
    }
    buildFlow(readFileFromWorkspace("$localPath/stuff/flow1script.txt"))
}


def buildJob = job("${appName}_Build") {
    description("a buildJob")
    jdk("$jdk")

    parameters {
        stringParam("Branch")
        label("$nodeLabel")
    }
    scm {
        git {
            remote {
                url("${repository}")
                branch('$Branch')
            }
        }
    }
    steps {
            maven {
                localRepository(LocalRepositoryLocation.LOCAL_TO_EXECUTOR)
            }
    }
}
//postprocessing
buildJob.configure { project ->
    project / 'builders' / 'hudson.tasks.Maven' /
            settings(class:"jenkins.mvn.FilePathSettingsProvider") {
                path "/maven/custom/path/.m2/config.xml"
            }


}





//----------------------------------------------------------------
// methods below should also go to external dependency

def getAppName() {
    getCurrentPathFromBindings().last().toString()
}

def getCurrentPathFromBindings() {
    this.binding.variables.each { k, v -> println "$k: $v" }
    return Paths.get(binding.variables.__FILE__ as String).parent
}

def getLocalPath(){
    "${currentPathFromBindings.toString().minus("${WORKSPACE}${File.SEPARATOR}".toString() as String)  }".toString()
}
