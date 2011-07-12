/*
 * Copyright (C) 2010 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it;

import com.atlassian.jira.rest.client.IntegrationTestUtil;
import com.atlassian.jira.rest.client.IterableMatcher;
import com.atlassian.jira.rest.client.TestUtil;
import com.atlassian.jira.rest.client.domain.AssigneeType;
import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.Component;
import com.atlassian.jira.rest.client.domain.input.ComponentInput;
import com.atlassian.jira.rest.client.internal.json.TestConstants;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertThat;

public class JerseyComponentRestClientTest extends AbstractRestoringJiraStateJerseyRestClientTest {

	@Test
	public void testGetComponent() throws Exception {
		final BasicComponent basicComponent = Iterables.find(client.getProjectClient().getProject("TST", pm).getComponents(),
				new Predicate<BasicComponent>() {
					@Override
					public boolean apply(BasicComponent input) {
						return "Component A".equals(input.getName());
					}
				});
		final Component component = client.getComponentClient().getComponent(basicComponent.getSelf(), pm);
		assertEquals("Component A", component.getName());
		assertEquals("this is some description of component A", component.getDescription());
		assertEquals(IntegrationTestUtil.USER_ADMIN, component.getLead());
	}

	@Test
	public void testGetInvalidComponent() throws Exception {
		final BasicComponent basicComponent = Iterables.getOnlyElement(client.getProjectClient().getProject("TST", pm).getComponents());
		final String uriForUnexistingComponent = basicComponent.getSelf().toString() + "1234";
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND,  "The component with id "
				+ TestUtil.getLastPathSegment(basicComponent.getSelf()) + "1234 does not exist.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().getComponent(TestUtil.toUri(uriForUnexistingComponent), pm);
			}
		});
	}

	@Test
	public void testGetComponentFromRestrictedProject() throws Exception {
		final BasicComponent basicComponent = Iterables.getOnlyElement(client.getProjectClient().getProject("RST", pm).getComponents());
		assertEquals("One Great Component", client.getComponentClient().getComponent(basicComponent.getSelf(), pm).getName());

		// now as unauthorized user
		setClient(TestConstants.USER2_USERNAME, TestConstants.USER2_PASSWORD);
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, "The user user does not have permission to complete this operation.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().getComponent(basicComponent.getSelf(), pm).getName();
			}
		});

		setAnonymousMode();
		TestUtil.assertErrorCode(Response.Status.NOT_FOUND, "This user does not have permission to complete this operation.", new Runnable() {
			@Override
			public void run() {
				client.getComponentClient().getComponent(basicComponent.getSelf(), pm).getName();
			}
		});
	}

	@Test
	public void testCreateAndRemoveComponent() {
		if (!isJira4x4OrNewer()) {
			return;
		}
		final Iterable<BasicComponent> components = client.getProjectClient().getProject("TST", pm).getComponents();
		assertEquals(2, Iterables.size(components));
		final BasicComponent basicComponent = Iterables.get(components, 0);
		final BasicComponent basicComponent2 = Iterables.get(components, 1);
		final String componentName = "my component";
		final ComponentInput componentInput = new ComponentInput(componentName, "a description", null, null);
		final Component component = client.getComponentClient().createComponent("TST", componentInput, pm);
		assertEquals(componentInput.getName(), component.getName());
		assertEquals(componentInput.getDescription(), component.getDescription());
		assertNull(component.getLead());
		assertProjectHasComponents(basicComponent.getName(), basicComponent2.getName(), componentName);

		client.getComponentClient().removeComponent(basicComponent.getSelf(), null, pm);
		assertProjectHasComponents(basicComponent2.getName(), componentName);
		client.getComponentClient().removeComponent(basicComponent2.getSelf(), null, pm);
		assertProjectHasComponents(componentName);
		client.getComponentClient().removeComponent(component.getSelf(), null, pm);
		assertProjectHasComponents();

		setUser1();

	}

	@SuppressWarnings({"ConstantConditions"})
	@Test
	public void testCreateComponentWithLead() {
		if (!isJira4x4OrNewer()) {
			return;
		}
		final ComponentInput componentInput = new ComponentInput("my component name", "a description", "admin", AssigneeType.COMPONENT_LEAD);
		final Component component = client.getComponentClient().createComponent("TST", componentInput, pm);
		assertNotNull(component.getAssigneeInfo());
		assertEquals(IntegrationTestUtil.USER_ADMIN, component.getAssigneeInfo().getAssignee());
		assertEquals(AssigneeType.COMPONENT_LEAD, component.getAssigneeInfo().getAssigneeType());
		assertTrue(component.getAssigneeInfo().isAssigneeTypeValid());
		assertEquals(IntegrationTestUtil.USER_ADMIN, component.getAssigneeInfo().getRealAssignee());
		assertEquals(AssigneeType.COMPONENT_LEAD, component.getAssigneeInfo().getRealAssigneeType());

		final ComponentInput componentInput2 = new ComponentInput("my component name2", "a description", IntegrationTestUtil.USER1.getName(), AssigneeType.UNASSIGNED);
		final Component component2 = client.getComponentClient().createComponent("TST", componentInput2, pm);
		assertNotNull(component2.getAssigneeInfo());
		assertNull(component2.getAssigneeInfo().getAssignee());
		assertEquals(AssigneeType.UNASSIGNED, component2.getAssigneeInfo().getAssigneeType());
		assertFalse(component2.getAssigneeInfo().isAssigneeTypeValid());
		assertEquals(IntegrationTestUtil.USER_ADMIN, component2.getAssigneeInfo().getRealAssignee());
		assertEquals(AssigneeType.PROJECT_DEFAULT, component2.getAssigneeInfo().getRealAssigneeType());
	}


	private void assertProjectHasComponents(String ...names) {
		assertThat(Iterables.transform(client.getProjectClient().getProject("TST", pm).getComponents(), new Function<BasicComponent, String>() {
			@Override
			public String apply(BasicComponent from) {
				return from.getName();
			}
		}), IterableMatcher.hasOnlyElements(names));
	}
}
