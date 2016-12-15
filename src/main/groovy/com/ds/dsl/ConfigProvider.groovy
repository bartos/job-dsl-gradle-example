package com.ds.dsl

import java.nio.file.Paths

/**
 * Created by db on 01.12.2016.
 */
class ConfigProvider {

    private static final String APP_PROPERTIES_FILE_NAME="app.properties"
    private static final String ENV_PROPERTIES_FILE_NAME="env.properties"
    private static String WORKSPACE_PATH=null


    /**
     *
     * @param variableMap LinkedHashMap
     * @return
     */
    def static getConfig(Map variableMap) {
        WORKSPACE_PATH=variableMap.WORKSPACE as String
        def callingScript = variableMap.__FILE__ as String
        def callingScriptPropertiesFileName = callingScript.replaceAll('(\\.groovy)$', '.properties')

        variableMap.out.println "callingScriptPropertiesFileName: $callingScriptPropertiesFileName"

        def scriptPropertiesFilePath = Paths.get(callingScript).parent.resolve(callingScriptPropertiesFileName)
        def applicationPropertiesFilePath = getApplicationPropertiesFilePath(callingScript)
        def environmentPropertiesFilePath = Paths.get(applicationPropertiesFilePath).parent.resolve(ENV_PROPERTIES_FILE_NAME)

        variableMap << readPropertiesFile(environmentPropertiesFilePath.toString())
        variableMap << readPropertiesFile(applicationPropertiesFilePath)
        variableMap << readPropertiesFile(scriptPropertiesFilePath.toString())

        variableMap<< getCallingScriptContextProperties(callingScript)


    }

    /**
     * Traverse directories up, until app.properties found
     */
    private static def getApplicationPropertiesFilePath(String callingScript){

        def parentPath = Paths.get(callingScript).parent
        def appPropertiesFile = parentPath.resolve(APP_PROPERTIES_FILE_NAME).toFile()

        if (!appPropertiesFile.exists()) {
            //go up while reach workspace main directory
            while (parentPath.toString() != WORKSPACE_PATH) {
                parentPath = parentPath.parent
                appPropertiesFile = parentPath.resolve(APP_PROPERTIES_FILE_NAME).toFile()
                if (appPropertiesFile.exists()) {
                    //got it
                    break
                }

            }
        }

        return appPropertiesFile.absolutePath
    }

    /**
     * serving script path related properties like name, it's parent dir etc
     * @param callingScript
     * @return
     */
    private static  def getCallingScriptContextProperties(String callingScript){
        def contextProps=new HashMap()
        def lFile=new File(callingScript)
        def scriptName=lFile.name.take(lFile.name.lastIndexOf('.'))
        def localPath=Paths.get(callingScript).parent.toString()
        contextProps.put("scriptName",scriptName)
        contextProps.put("localPath",localPath)
        return  contextProps
    }



    static def readPropertiesFile(String filePath) {
        def propertiesFile = new File(filePath)
        def propertiesMap = new HashMap()
        if (propertiesFile.exists()) {
            //TODO: LOG IF NOT EXISTS (ON JENKINS CONSOLE)
            Properties properties = new Properties()
            propertiesFile.withInputStream { properties.load(it) }
            properties.each {
                key, val -> propertiesMap.put(key, val)
            }
        }
        return propertiesMap

    }


}
