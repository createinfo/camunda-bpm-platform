/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.container.impl.jboss.config;

import java.util.Map;

import org.camunda.bpm.engine.ProcessEngineException;

/**
 * @author Daniel Meyer
 *
 */
public class ManagedProcessEngineMetadata {
  
  /** indicates whether the process engine should automatically create / 
   * update the database schema upon startup */ 
  public static String PROP_IS_AUTO_SCHEMA_UPDATE = "isAutoSchemaUpdate";

  /** indicates whether the identity module is used and if this tables are
   *  required */ 
  public static String PROP_IS_IDENTITY_USED = "isIdentityUsed";

  /** indicates whether the job executor should be automatically activated */
  public static String PROP_IS_ACTIVATE_JOB_EXECUTOR = "isActivateJobExecutor";
  
  /** the prefix to be used for all process engine database tables */
  public static String PROP_DB_TABLE_PREFIX = "dbTablePrefix";
  
  /** the name of the platform job executor acquisition to use */
  public static String PROP_JOB_EXECUTOR_ACQUISITION_NAME = "jobExecutorAcquisitionName";

  private boolean isDefault;
  private String engineName;
  private String datasourceJndiName;
  private String historyLevel;
  protected String configuration;
  private Map<String, Object> properties;

  /**
   * @param isDefault
   * @param engineName
   * @param datasourceJndiName
   * @param historyLevel
   * @param configuration 
   * @param properties
   */
  public ManagedProcessEngineMetadata(boolean isDefault, String engineName, String datasourceJndiName, String historyLevel, String configuration, Map<String, Object> properties) {
    this.isDefault = isDefault;
    this.engineName = engineName;
    this.datasourceJndiName = datasourceJndiName;
    this.historyLevel = historyLevel;
    this.configuration = configuration;
    this.properties = properties;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  public String getEngineName() {
    return engineName;
  }

  public void setEngineName(String engineName) {
    this.engineName = engineName;
  }

  public String getDatasourceJndiName() {
    return datasourceJndiName;
  }

  public void setDatasourceJndiName(String datasourceJndiName) {
    this.datasourceJndiName = datasourceJndiName;
  }

  public String getHistoryLevel() {
    return historyLevel;
  }

  public void setHistoryLevel(String historyLevel) {
    this.historyLevel = historyLevel;
  }
  
  public String getConfiguration() {
    return configuration;
  }
  
  public void setConfiguration(String configuration) {
    this.configuration = configuration;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
  
  public boolean isIdentityUsed() {
    Object object = getProperties().get(PROP_IS_IDENTITY_USED);
    if(object == null) {
      return true;
    } else {
      return Boolean.parseBoolean((String) object);
    }
  }
  
  public boolean isAutoSchemaUpdate() {
    Object object = getProperties().get(PROP_IS_AUTO_SCHEMA_UPDATE);
    if(object == null) {
      return true;
    } else {
      return Boolean.parseBoolean((String) object);
    }
  }
  
  public boolean isActivateJobExecutor() {
    Object object = getProperties().get(PROP_IS_ACTIVATE_JOB_EXECUTOR);
    if(object == null) {
      return true;
    } else {
      return Boolean.parseBoolean((String) object);
    }
  }
  
  public String getDbTablePrefix() {
    Object object = getProperties().get(PROP_DB_TABLE_PREFIX);
    if(object == null) {
      return null;
    } else {
      return (String) object;
    }
  }
  
  public String getJobExecutorAcquisitionName() {
    Object object = getProperties().get(PROP_JOB_EXECUTOR_ACQUISITION_NAME);
    if(object == null) {
      return "default";
    } else {
      return (String) object;
    }
  }
  
  /**
   * validates the configuration and throws {@link ProcessEngineException} 
   * if the configuration is invalid.
   */
  public void validate() {
    StringBuilder validationErrorBuilder = new StringBuilder("Process engine configuration is invalid: \n");
    boolean isValid = true;    
    
    if(datasourceJndiName == null || datasourceJndiName.length() == 0) {
      isValid = false;
      validationErrorBuilder.append(" property 'datasource' cannot be null \n");      
    }
    if(engineName == null || engineName.isEmpty()) {
      isValid = false;
      validationErrorBuilder.append(" property 'engineName' cannot be null \n");
    }
    
    if(!isValid) {
      throw new ProcessEngineException(validationErrorBuilder.toString());
    }
  }
}
