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

package com.atlassian.jira.rest.client;

/**
 * Main access point to REST com.atlassian.jira.rest.client.
 * As there are many types resources exposed by JIRA REST API, various resources are grouped into clusters
 * and then handled by different specialized *RestClient classes.
 *
 * @since v0.1
 */
public interface JiraRestClient {
	/**
	 * @return com.atlassian.jira.rest.client for performing operations on selected issue
	 */
	IssueRestClient getIssueClient();

	/**
	 * @return the com.atlassian.jira.rest.client handling session information
	 */
	SessionRestClient getSessionClient();

	/**
	 * @return the com.atlassian.jira.rest.client handling full user information
	 */
	UserRestClient getUserClient();

	/**
	 * @return the com.atlassian.jira.rest.client handling project metadata
	 */
	ProjectRestClient getProjectClient();

	/**
	 * @return the com.atlassian.jira.rest.client handling components
	 */
	ComponentRestClient getComponentClient();

	/**
	 * @return the com.atlassian.jira.rest.client handling basic meta-data (data dictionaries defined in JIRA - like resolutions, statuses,
	 *         priorities)
	 */
	MetadataRestClient getMetadataClient();

	/**
	 * @return the com.atlassian.jira.rest.client handling search (e.g. JQL)
	 */
	SearchRestClient getSearchClient();

	/**
	 * @return the com.atlassian.jira.rest.client handling project versions.
	 */
	VersionRestClient getVersionRestClient();

	/**
	 * @return the com.atlassian.jira.rest.client for project roles.
	 */
	ProjectRolesRestClient getProjectRolesRestClient();

    /**
     * Destroys this instance of JIRA Rest Client.
	 *
     * @throws Exception
     */
    void destroy() throws Exception;
}