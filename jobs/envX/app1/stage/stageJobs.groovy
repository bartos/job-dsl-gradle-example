import static com.ds.dsl.ConfigProvider.getConfig
/**
 * Created by db on 02.12.2016.
 */

getConfig(binding.variables)

//TODO: config should reach env & app props
job("$jobXName"){
    description("""This is job description: $description
and this comes from app.properties: $applicationDescription
and this comes from env.poperties: $envName""")

}

