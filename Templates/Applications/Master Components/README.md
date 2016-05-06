# DSL Templates - Master Components

DSL templates for creating master components for application components that can be deployed and undeployed on application servers such as JBoss, WebLogic, and IIS.

## Prerequisites ##

  1. The required application server plugin should be installed on the **ElectricFlow** server.
    * JBoss           - **EC-JBoss**
    * IBM WebSphere   - **EC-WebSphere**
    * Microsoft IIS   - **EC-IIS7**
    * Oracle Weblogic - **EC-Weblogic**
    * Tomcat          - **EC-Tomcat**
    * IIS             - **EC-IIS7**
  
  2. The **EC-FileSysRepo** is assumed to be pre-installed on the **ElectricFlow** server.
  
  3. Repository for **EC-FileSysRepo** plugin should contain WAR file, that will be deployed to Java application servers (**JBoss**, **WebLogic** or **WebSphere**). For IIS Web server repository should contain zipped archive of Web Application that will be deployed.

## Instructions ##

1. Run the following command to execute the DSL template for your choosen application server.
 `ectool evalDsl --dslFile <file-path>`
 
2. You should now have the Master Component created or updated in **ElectricFlow** under **Applications -> Master Components**. 

3. The master component has parameters defined to allow you to configure the application server specific values such as server home directory, WAR file name, etc. You can specify the actual parameter values to use when creating application components using these master components as references.
