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
package org.camunda.bpm.engine.test.api.runtime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.test.PluggableProcessEngineTestCase;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.runtime.VariableInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;

/**
 * @author roman.smirnov
 */
public class VariableInstanceQueryTest extends PluggableProcessEngineTestCase {
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQuery() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("intVar", (int)123);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("stringVar", "test");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery();
    
    // then
    assertNotNull(query);
    
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      if (var.getName().equals("intVar")) {
        assertEquals("intVar", var.getName());
        assertEquals(123, var.getValue());
      } else if (var.getName().equals("stringVar")) {
        assertEquals("stringVar", var.getName());
        assertEquals("test", var.getValue());
      } else {
        fail("An unexpected variable '" + var.getName() + "' was found with value " + var.getValue());
      }

    }
  }

  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByVariableName() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableName("stringVar");
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("stringVar", var.getName());
    assertEquals("test", var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByVariableNameLike() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableNameLike("%ingV%");
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("stringVar", var.getName());
    assertEquals("test", var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByVariableNameLikeWithoutAnyResult() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableNameLike("%ingV_");
    
    // then
    List<VariableInstance> result = query.list();
    assertTrue(result.isEmpty());
    
    assertEquals(0, query.count());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueEquals_String() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueEquals("stringVar", "test");
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("stringVar", var.getName());
    assertEquals("test", var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueNotEquals_String() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("stringVar", "test");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
 
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("stringVar", "test123");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueNotEquals("stringVar", "test123");
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("stringVar", var.getName());
    assertEquals("test", var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueGreaterThan_String() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("stringVar", "a");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("stringVar", "b");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("stringVar", "c");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueGreaterThan("stringVar", "a");
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("stringVar", var.getName());
      if (var.getValue().equals("b")) {
        assertEquals("b", var.getValue());  
      } else if (var.getValue().equals("c")) {
        assertEquals("c", var.getValue());
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueGreaterThanOrEqual_String() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("stringVar", "a");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("stringVar", "b");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("stringVar", "c");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueGreaterThanOrEqual("stringVar", "a");
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("stringVar", var.getName());
      if (var.getValue().equals("a")) {
        assertEquals("a", var.getValue());  
      } else if (var.getValue().equals("b")) {
        assertEquals("b", var.getValue());  
      } else if (var.getValue().equals("c")) {
        assertEquals("c", var.getValue());
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueLessThan_String() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("stringVar", "a");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("stringVar", "b");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("stringVar", "c");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLessThan("stringVar", "c");
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("stringVar", var.getName());
      if (var.getValue().equals("a")) {
        assertEquals("a", var.getValue());  
      } else if (var.getValue().equals("b")) {
        assertEquals("b", var.getValue());
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueLessThanOrEqual_String() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("stringVar", "a");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("stringVar", "b");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("stringVar", "c");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLessThanOrEqual("stringVar", "c");
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("stringVar", var.getName());
      if (var.getValue().equals("a")) {
        assertEquals("a", var.getValue());  
      } else if (var.getValue().equals("b")) {
        assertEquals("b", var.getValue());  
      } else if (var.getValue().equals("c")) {
        assertEquals("c", var.getValue());
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueLike_String() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("stringVar", "test123");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("stringVar", "test456");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("stringVar", "test789");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLike("stringVar", "test%");
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("stringVar", var.getName());
      if (var.getValue().equals("test123")) {
        assertEquals("test123", var.getValue());  
      } else if (var.getValue().equals("test456")) {
        assertEquals("test456", var.getValue());  
      } else if (var.getValue().equals("test789")) {
        assertEquals("test789", var.getValue());
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueEquals_Integer() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("intValue", (int) 1234);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueEquals("intValue", (int) 1234);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("intValue", var.getName());
    assertEquals(1234, var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueNotEquals_Integer() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("intValue", (int) 1234);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("intValue", (int) 5555);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueNotEquals("intValue", (int) 5555);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("intValue", var.getName());
    assertEquals(1234, var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableGreaterThan_Integer() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("intValue", (int) 1234);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("intValue", (int) 5555);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("intValue", (int) 9876);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueGreaterThan("intValue", (int) 1234);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("intValue", var.getName());
      if (var.getValue().equals(5555)) {
        assertEquals(5555, var.getValue());  
      } else if (var.getValue().equals(9876)) {
        assertEquals(9876, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableGreaterThanAndEqual_Integer() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("intValue", (int) 1234);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("intValue", (int) 5555);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("intValue", (int) 9876);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueGreaterThanOrEqual("intValue", (int) 1234);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("intValue", var.getName());
      if (var.getValue().equals(1234)) {
        assertEquals(1234, var.getValue()); 
      } else if (var.getValue().equals(5555)) {
        assertEquals(5555, var.getValue());  
      } else if (var.getValue().equals(9876)) {
        assertEquals(9876, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableLessThan_Integer() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("intValue", (int) 1234);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("intValue", (int) 5555);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("intValue", (int) 9876);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLessThan("intValue", (int) 9876);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("intValue", var.getName());
      if (var.getValue().equals(5555)) {
        assertEquals(5555, var.getValue());  
      } else if (var.getValue().equals(1234)) {
        assertEquals(1234, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableLessThanAndEqual_Integer() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("intValue", (int) 1234);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("intValue", (int) 5555);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("intValue", (int) 9876);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLessThanOrEqual("intValue", (int) 9876);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("intValue", var.getName());
      if (var.getValue().equals(1234)) {
        assertEquals(1234, var.getValue()); 
      } else if (var.getValue().equals(5555)) {
        assertEquals(5555, var.getValue());  
      } else if (var.getValue().equals(9876)) {
        assertEquals(9876, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueEquals_Long() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("longValue", 123456L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueEquals("longValue", 123456L);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("longValue", var.getName());
    assertEquals(123456L, var.getValue());
  }
 
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueNotEquals_Long() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("longValue", 123456L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("longValue", 987654L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueNotEquals("longValue", 987654L);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("longValue", var.getName());
    assertEquals(123456L, var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableGreaterThan_Long() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("longValue", 123456L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("longValue", 987654L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("longValue", 555555L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueGreaterThan("longValue", 123456L);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("longValue", var.getName());
      if (var.getValue().equals(555555L)) {
        assertEquals(555555L, var.getValue());  
      } else if (var.getValue().equals(987654L)) {
        assertEquals(987654L, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableGreaterThanAndEqual_Long() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("longValue", 123456L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("longValue", 987654L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("longValue", 555555L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueGreaterThanOrEqual("longValue", 123456L);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("longValue", var.getName());
      if (var.getValue().equals(123456L)) {
        assertEquals(123456L, var.getValue()); 
      } else if (var.getValue().equals(555555L)) {
        assertEquals(555555L, var.getValue());  
      } else if (var.getValue().equals(987654L)) {
        assertEquals(987654L, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableLessThan_Long() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("longValue", 123456L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("longValue", 987654L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("longValue", 555555L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLessThan("longValue", 987654L);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("longValue", var.getName());
      if (var.getValue().equals(123456L)) {
        assertEquals(123456L, var.getValue());  
      } else if (var.getValue().equals(555555L)) {
        assertEquals(555555L, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableLessThanAndEqual_Long() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("longValue", 123456L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("longValue", 987654L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("longValue", 555555L);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLessThanOrEqual("longValue", 987654L);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("longValue", var.getName());
      if (var.getValue().equals(123456L)) {
        assertEquals(123456L, var.getValue()); 
      } else if (var.getValue().equals(555555L)) {
        assertEquals(555555L, var.getValue());  
      } else if (var.getValue().equals(987654L)) {
        assertEquals(987654L, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueEquals_Double() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("doubleValue", 123.456);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueEquals("doubleValue", 123.456);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("doubleValue", var.getName());
    assertEquals(123.456, var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueNotEquals_Double() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("doubleValue", 123.456);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("doubleValue", 654.321);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueNotEquals("doubleValue", 654.321);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("doubleValue", var.getName());
    assertEquals(123.456, var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableGreaterThan_Double() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("doubleValue", 123.456);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("doubleValue", 654.321);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("doubleValue", 999.999);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueGreaterThan("doubleValue", 123.456);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("doubleValue", var.getName());
      if (var.getValue().equals(654.321)) {
        assertEquals(654.321, var.getValue());  
      } else if (var.getValue().equals(999.999)) {
        assertEquals(999.999, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableGreaterThanAndEqual_Double() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("doubleValue", 123.456);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("doubleValue", 654.321);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("doubleValue", 999.999);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueGreaterThanOrEqual("doubleValue", 123.456);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("doubleValue", var.getName());
      if (var.getValue().equals(123.456)) {
        assertEquals(123.456, var.getValue()); 
      } else if (var.getValue().equals(654.321)) {
        assertEquals(654.321, var.getValue());  
      } else if (var.getValue().equals(999.999)) {
        assertEquals(999.999, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableLessThan_Double() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("doubleValue", 123.456);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("doubleValue", 654.321);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("doubleValue", 999.999);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLessThan("doubleValue", 999.999);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("doubleValue", var.getName());
      if (var.getValue().equals(123.456)) {
        assertEquals(123.456, var.getValue());  
      } else if (var.getValue().equals(654.321)) {
        assertEquals(654.321, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableLessThanAndEqual_Double() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("doubleValue", 123.456);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("doubleValue", 654.321);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("doubleValue", 999.999);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLessThanOrEqual("doubleValue", 999.999);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("doubleValue", var.getName());
      if (var.getValue().equals(123.456)) {
        assertEquals(123.456, var.getValue()); 
      } else if (var.getValue().equals(654.321)) {
        assertEquals(654.321, var.getValue());  
      } else if (var.getValue().equals(999.999)) {
        assertEquals(999.999, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueEquals_Short() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("shortValue", (short) 123);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueEquals("shortValue", (short) 123);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("shortValue", var.getName());
    assertEquals((short) 123, var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByVariableValueNotEquals_Short() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("shortValue", (short) 123);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("shortValue", (short) 999);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueNotEquals("shortValue", (short) 999);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("shortValue", var.getName());
    assertEquals((short) 123, var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableGreaterThan_Short() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("shortValue", (short) 123);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("shortValue", (short) 999);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("shortValue", (short) 555);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueGreaterThan("shortValue", (short) 123);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("shortValue", var.getName());
      if (var.getValue().equals((short) 555)) {
        assertEquals((short) 555, var.getValue());  
      } else if (var.getValue().equals((short) 999)) {
        assertEquals((short) 999, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableGreaterThanAndEqual_Short() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("shortValue", (short) 123);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("shortValue", (short) 999);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("shortValue", (short) 555);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueGreaterThanOrEqual("shortValue", (short) 123);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("shortValue", var.getName());
      if (var.getValue().equals((short) 123)) {
        assertEquals((short) 123, var.getValue()); 
      } else if (var.getValue().equals((short) 555)) {
        assertEquals((short) 555, var.getValue());  
      } else if (var.getValue().equals((short) 999)) {
        assertEquals((short) 999, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableLessThan_Short() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("shortValue", (short) 123);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("shortValue", (short) 999);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("shortValue", (short) 555);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLessThan("shortValue", (short) 999);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("shortValue", var.getName());
      if (var.getValue().equals((short) 123)) {
        assertEquals((short) 123, var.getValue());  
      } else if (var.getValue().equals((short) 555)) {
        assertEquals((short) 555, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableLessThanAndEqual_Short() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("shortValue", (short) 123);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("shortValue", (short) 999);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("shortValue", (short) 555);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueLessThanOrEqual("shortValue", (short) 999);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("shortValue", var.getName());
      if (var.getValue().equals((short) 123)) {
        assertEquals((short) 123, var.getValue()); 
      } else if (var.getValue().equals((short) 555)) {
        assertEquals((short) 555, var.getValue());  
      } else if (var.getValue().equals((short) 999)) {
        assertEquals((short) 999, var.getValue());  
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueEquals_Bytes() {
    // given
    byte[] bytes = "somebytes".getBytes();
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("bytesVar", bytes);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueEquals("bytesVar", bytes);
    
    // then
    try {
      query.list();
      fail("A ProcessEngineException was expected: Variables of type ByteArray cannot be used to query.");
    } catch (ProcessEngineException e) {
      // expected exception
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueEquals_Date() {
    // given
     Date now = new Date();
    
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("date", now);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueEquals("date", now);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("date", var.getName());
    assertEquals(now, var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueEqualsWihtoutAnyResult() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueEquals("stringVar", "notFoundValue");
    
    // then
    List<VariableInstance> result = query.list();
    assertTrue(result.isEmpty());
    
    assertEquals(0, query.count());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByNameAndVariableValueEquals_NullValue() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("nullValue", null);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueEquals("nullValue", null);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("nullValue", var.getName());
    assertEquals(null, var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByVariableValueNotEquals_NullValue() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("value", null);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);

    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("value", (short) 999);
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);

    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("value", "abc");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().variableValueNotEquals("value", null);
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      assertEquals("value", var.getName());
      if (var.getValue().equals((short) 999)) {
        assertEquals((short) 999, var.getValue());
      } else if (var.getValue().equals("abc")) {
        assertEquals("abc", var.getValue());
      } else {
        fail("A non expected value occured: " + var.getValue());
      }
      
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByProcessInstanceId() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    variables.put("myVar", "test123");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().processInstanceIdIn(processInstance.getId());
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      if (var.getName().equals("myVar")) {
        assertEquals("myVar", var.getName());
        assertEquals("test123", var.getValue());
      } else if (var.getName().equals("stringVar")) {
        assertEquals("stringVar", var.getName());
        assertEquals("test", var.getValue());
      } else {
        fail("An unexpected variable '" + var.getName() + "' was found with value " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByProcessInstanceIds() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    variables.put("myVar", "test123");
    ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("oneTaskProcess");
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().processInstanceIdIn(processInstance1.getId(), processInstance2.getId());
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      if (var.getName().equals("myVar")) {
        assertEquals("myVar", var.getName());
        assertEquals("test123", var.getValue());
      } else if (var.getName().equals("stringVar")) {
        assertEquals("stringVar", var.getName());
        assertEquals("test", var.getValue());
      } else {
        fail("An unexpected variable '" + var.getName() + "' was found with value " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByProcessInstanceIdWithoutAnyResult() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    runtimeService.startProcessInstanceByKey("oneTaskProcess");
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().processInstanceIdIn("aProcessInstanceId");
    
    // then
    List<VariableInstance> result = query.list();
    assertTrue(result.isEmpty());
    
    assertEquals(0, query.count());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByExecutionId() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    variables.put("myVar", "test123");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().executionIdIn(processInstance.getId());
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      if (var.getName().equals("myVar")) {
        assertEquals("myVar", var.getName());
        assertEquals("test123", var.getValue());
      } else if (var.getName().equals("stringVar")) {
        assertEquals("stringVar", var.getName());
        assertEquals("test", var.getValue());
      } else {
        fail("An unexpected variable '" + var.getName() + "' was found with value " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByExecutionIds() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("stringVar", "test");
    variables1.put("myVar", "test123");
    ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("myVar", "test123");
    ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().executionIdIn(processInstance1.getId(), processInstance2.getId());
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    
    assertEquals(3, query.count());
    
    for (VariableInstance var : result) {
      if (var.getName().equals("myVar")) {
        assertEquals("myVar", var.getName());
        assertEquals("test123", var.getValue());
      } else if (var.getName().equals("stringVar")) {
        assertEquals("stringVar", var.getName());
        assertEquals("test", var.getValue());
      } else {
        fail("An unexpected variable '" + var.getName() + "' was found with value " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByExecutionIdWithoutAnyResult() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    runtimeService.startProcessInstanceByKey("oneTaskProcess");
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().executionIdIn("anExecutionId");
    
    // then
    List<VariableInstance> result = query.list();
    assertTrue(result.isEmpty());
    
    assertEquals(0, query.count());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByTaskId() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
    
    taskService.setVariableLocal(task.getId(), "taskVariable", "aCustomValue");
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().taskIdIn(task.getId());
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    assertEquals(1, query.count());
    
    VariableInstance var = result.get(0);
    assertEquals("taskVariable", var.getName());
    assertEquals("aCustomValue", var.getValue());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByTaskIds() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("oneTaskProcess");
    ProcessInstance processInstance3 = runtimeService.startProcessInstanceByKey("oneTaskProcess");
    
    Task task1 = taskService.createTaskQuery().processInstanceId(processInstance1.getId()).singleResult();
    Task task2 = taskService.createTaskQuery().processInstanceId(processInstance2.getId()).singleResult();
    Task task3 = taskService.createTaskQuery().processInstanceId(processInstance3.getId()).singleResult();
    
    taskService.setVariableLocal(task1.getId(), "taskVariable", "aCustomValue");
    taskService.setVariableLocal(task2.getId(), "anotherTaskVariable", "aCustomValue");
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().taskIdIn(task1.getId(), task2.getId(), task3.getId());
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      if (var.getName().equals("taskVariable")) {
        assertEquals("taskVariable", var.getName());
        assertEquals("aCustomValue", var.getValue());
      } else if (var.getName().equals("anotherTaskVariable")) {
        assertEquals("anotherTaskVariable", var.getName());
        assertEquals("aCustomValue", var.getValue());
      } else {
        fail("An unexpected variable '" + var.getName() + "' was found with value " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByTaskIdWithoutAnyResult() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
    
    taskService.setVariableLocal(task.getId(), "taskVariable", "aCustomValue");
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().taskIdIn("aTaskId");
    
    // then
    List<VariableInstance> result = query.list();
    assertTrue(result.isEmpty());
    
    assertEquals(0, query.count());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByActivityInstanceId() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    ExecutionEntity execution = (ExecutionEntity) processInstance;
    
    Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
    taskService.setVariableLocal(task.getId(), "taskVariable", "aCustomValue");
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().activityInstanceIdIn(execution.getActivityInstanceId());
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    assertEquals(2, query.count());
    
    for (VariableInstance var : result) {
      if (var.getName().equals("stringVar")) {
        assertEquals("stringVar", var.getName());
        assertEquals("test", var.getValue());
      } else if (var.getName().equals("taskVariable")) {
        assertEquals("taskVariable", var.getName());
        assertEquals("aCustomValue", var.getValue());
      } else {
        fail("An unexpected variable '" + var.getName() + "' was found with value " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryByActivityInstanceIds() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("stringVar", "test");
    variables1.put("myVar", "test123");
    ExecutionEntity execution1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("myVar", "test123");
    ExecutionEntity execution2 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    Map<String, Object> variables3 = new HashMap<String, Object>();
    variables3.put("myVar", "test123");
    ExecutionEntity execution3 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("oneTaskProcess", variables3);
    
    Task task1 = taskService.createTaskQuery().processInstanceId(execution1.getProcessInstanceId()).singleResult();
    Task task2 = taskService.createTaskQuery().processInstanceId(execution2.getProcessInstanceId()).singleResult();
    
    taskService.setVariableLocal(task1.getId(), "taskVariable", "aCustomValue");
    taskService.setVariableLocal(task2.getId(), "anotherTaskVariable", "aCustomValue");
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().activityInstanceIdIn(execution1.getActivityInstanceId(), execution2.getActivityInstanceId(), execution3.getActivityInstanceId());
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(6, result.size());
    
    assertEquals(6, query.count());
    
    for (VariableInstance var : result) {
      if (var.getName().equals("myVar")) {
        assertEquals("myVar", var.getName());
        assertEquals("test123", var.getValue());
      } else if (var.getName().equals("stringVar")) {
        assertEquals("stringVar", var.getName());
        assertEquals("test", var.getValue());
      } else if (var.getName().equals("taskVariable")) {
        assertEquals("taskVariable", var.getName());
        assertEquals("aCustomValue", var.getValue());
      } else if (var.getName().equals("anotherTaskVariable")) {
        assertEquals("anotherTaskVariable", var.getName());
        assertEquals("aCustomValue", var.getValue());        
      } else {
        fail("An unexpected variable '" + var.getName() + "' was found with value " + var.getValue());
      }
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryOrderByName_Asc() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    variables.put("myVar", "test123");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().orderByVariableName().asc();
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    VariableInstance first = result.get(0);
    VariableInstance second = result.get(1);
    
    assertEquals("myVar", first.getName());
    assertEquals("stringVar", second.getName());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryOrderByName_Desc() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("stringVar", "test");
    variables.put("myVar", "test123");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().orderByVariableName().desc();
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    VariableInstance first = result.get(0);
    VariableInstance second = result.get(1);
    
    assertEquals("stringVar", first.getName());
    assertEquals("myVar", second.getName());
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryOrderByType_Asc() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("intVar", (int)123);
    variables.put("myVar", "test123");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().orderByVariableType().asc();
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    VariableInstance first = result.get(0);
    VariableInstance second = result.get(1);
    
    assertEquals("intVar", first.getName()); // integer
    assertEquals("myVar", second.getName()); // string
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryOrderByType_Desc() {
    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("intVar", (int)123);
    variables.put("myVar", "test123");
    runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().orderByVariableType().desc();
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    VariableInstance first = result.get(0);
    VariableInstance second = result.get(1);
    
    assertEquals("myVar", first.getName()); // string
    assertEquals("intVar", second.getName()); // integer
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryOrderByActivityInstanceId_Asc() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("intVar", (int)123);
    ExecutionEntity execution1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("stringVar", "test");
    ExecutionEntity execution2 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    int comparisonResult = execution1.getActivityInstanceId().compareTo(execution2.getActivityInstanceId());
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().orderByActivityInstanceId().asc();
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    VariableInstance first = result.get(0);
    VariableInstance second = result.get(1);

    if (comparisonResult < 0) {
      assertEquals("intVar", first.getName());
      assertEquals("stringVar", second.getName());
    } else if (comparisonResult > 0) {
      assertEquals("stringVar", first.getName());
      assertEquals("intVar", second.getName());      
    } else {
      fail("Something went wrong: both activity instances have the same id " + execution1.getActivityInstanceId() + " and " + execution2.getActivityInstanceId());
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testQueryOrderByActivityInstanceId_Desc() {
    // given
    Map<String, Object> variables1 = new HashMap<String, Object>();
    variables1.put("intVar", (int)123);
    ExecutionEntity execution1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("oneTaskProcess", variables1);
    
    Map<String, Object> variables2 = new HashMap<String, Object>();
    variables2.put("stringVar", "test");
    ExecutionEntity execution2 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("oneTaskProcess", variables2);
    
    int comparisonResult = execution1.getActivityInstanceId().compareTo(execution2.getActivityInstanceId());
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().orderByActivityInstanceId().desc();
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    
    VariableInstance first = result.get(0);
    VariableInstance second = result.get(1);

    if (comparisonResult < 0) {
      assertEquals("stringVar", first.getName());
      assertEquals("intVar", second.getName());   
    } else if (comparisonResult > 0) {
      assertEquals("intVar", first.getName());
      assertEquals("stringVar", second.getName());   
    } else {
      fail("Something went wrong: both activity instances have the same id " + execution1.getActivityInstanceId() + " and " + execution2.getActivityInstanceId());
    }
  }
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/oneTaskProcess.bpmn20.xml"})
  public void testGetValueOfserializableVar() {
    // given
    List<String> serializable = new ArrayList<String>();
    serializable.add("one");
    serializable.add("two");
    serializable.add("three");
    
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("serializableVar", serializable);
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
    
    // when
    VariableInstanceQuery query = runtimeService.createVariableInstanceQuery().processInstanceIdIn(processInstance.getId());
    
    // then
    List<VariableInstance> result = query.list();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    
    VariableInstance instance = result.get(0);
    
    assertEquals("serializableVar", instance.getName());
    assertNotNull(instance.getValue());
    assertEquals(serializable, instance.getValue()); 
    
  }
  
  
  @Test
  @Deployment(resources={"org/camunda/bpm/engine/test/api/runtime/VariableInstanceQueryTest.testSubProcessWithVariables.bpmn"})
  public void testSubProcessWithVariables() {
    // given
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("processWithSubProcess");
    
    ActivityInstance tree = runtimeService.getActivityInstance(processInstance.getId());
    assertNotNull(tree);
    assertEquals(5, tree.getChildActivityInstances().length);
    
    //when
    String activityInstanceId1 = tree.getChildActivityInstances()[0].getId();
    VariableInstanceQuery query1 = runtimeService.createVariableInstanceQuery().activityInstanceIdIn(activityInstanceId1);
        
    String activityInstanceId2 = tree.getChildActivityInstances()[1].getId();
    VariableInstanceQuery query2 = runtimeService.createVariableInstanceQuery().activityInstanceIdIn(activityInstanceId2);
    
    String activityInstanceId3 = tree.getChildActivityInstances()[2].getId();
    VariableInstanceQuery query3 = runtimeService.createVariableInstanceQuery().activityInstanceIdIn(activityInstanceId3);
    
    String activityInstanceId4 = tree.getChildActivityInstances()[3].getId();
    VariableInstanceQuery query4 = runtimeService.createVariableInstanceQuery().activityInstanceIdIn(activityInstanceId4);
    
    String activityInstanceId5 = tree.getChildActivityInstances()[4].getId();
    VariableInstanceQuery query5 = runtimeService.createVariableInstanceQuery().activityInstanceIdIn(activityInstanceId5);
    
    // then
    checkVariables(query1.list());
    checkVariables(query2.list());
    checkVariables(query3.list());
    checkVariables(query4.list());
    checkVariables(query5.list());
  }
  
  private void checkVariables(List<VariableInstance> variableInstances) {
    assertFalse(variableInstances.isEmpty());
    for (VariableInstance instance : variableInstances) {
      if (instance.getName().equals("nrOfInstances")) {
        assertEquals("nrOfInstances", instance.getName());
      } else if (instance.getName().equals("nrOfCompletedInstances")) {
        assertEquals("nrOfCompletedInstances", instance.getName());
      } else if (instance.getName().equals("nrOfActiveInstances")) {
        assertEquals("nrOfActiveInstances", instance.getName());
      } else if (instance.getName().equals("loopCounter")) {
        assertEquals("loopCounter", instance.getName());      
      } else if (instance.getName().equals("nullVar")) {
        assertEquals("nullVar", instance.getName());
      } else if (instance.getName().equals("integerVar")) {
        assertEquals("integerVar", instance.getName());        
      } else if (instance.getName().equals("dateVar")) {
        assertEquals("dateVar", instance.getName());            
      } else if (instance.getName().equals("stringVar")) {
        assertEquals("stringVar", instance.getName()); 
      } else if (instance.getName().equals("shortVar")) {
        assertEquals("shortVar", instance.getName());         
      } else if (instance.getName().equals("longVar")) {
        assertEquals("longVar", instance.getName());          
      } else if (instance.getName().equals("serializableVar")) {
        assertEquals("serializableVar", instance.getName());       
      } else {
        fail("An unexpected variable '" + instance.getName() + "' was found with value " + instance.getValue());
      }
    }    
  }
  
}
