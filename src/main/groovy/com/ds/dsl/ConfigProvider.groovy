package com.ds.dsl

import java.nio.file.Paths

/**
 * Created by db on 01.12.2016.
 */
class ConfigProvider {

    /**
     *
     * @param variableMap LinkedHashMap
     * @return
     */
    def static getConfig(Map variableMap) {
        def callingScript = variableMap.__FILE__ as String
        def callingScriptPropertiesFileName = callingScript.replaceAll('(\\.groovy)$', '.properties')

        variableMap.out.println "callingScriptPropertiesFileName: $callingScriptPropertiesFileName"

        def scriptPropertiesFilePath = Paths.get(callingScript).parent.resolve(callingScriptPropertiesFileName)
        def applicationPropertiesFilePath = Paths.get(callingScript).parent.resolve("app.properties")
        def environmentPropertiesFilePath = Paths.get(callingScript).parent.parent.resolve('env.properties')

        variableMap << readPropertiesFile(environmentPropertiesFilePath.toString())
        variableMap << readPropertiesFile(applicationPropertiesFilePath.toString())
        variableMap << readPropertiesFile(scriptPropertiesFilePath.toString())

        variableMap<< getCallingScriptContextProperties(callingScript)

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
