# DSL Templates - Resource Templates

DSL templates for creating resource templates for dynamic resources using various cloud providers such as Amazon EC2, OpenStack, and Azure and configuration management tools such as Checf and Puppet.

## Prerequisites ##

  1. The required cloud provider plugin should be installed and promoted on the **ElectricFlow** server.
  * Amazon EC2 - EC-EC2  
  * OpenStack - EC-OpenStack
  * Azure Cloud - EC-Azure
  
  2. The required configuration management plugin should be installed and promoted on the ElectricFlow server.
  * Chef - EC-Chef
  * Puppet - EC-Puppet
  
## Instructions ##

1. Edit the template file dynamicEnvironment.dsl replacing the parameter values with the desired values for your choosen cloud provider and configuration management tool.

2. Run the following command to execute the updated DSL template.
 `ectool evalDsl --dslFile dynamicEnvironment.dsl`
 
 3. You should now have a completely setup Resource Template in **ElectricFlow** under **Environments -> Resource Templates**. 






