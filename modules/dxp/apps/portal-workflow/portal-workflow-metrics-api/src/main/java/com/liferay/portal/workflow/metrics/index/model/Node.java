/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.workflow.metrics.index.model;

import java.util.Objects;

/**
 * @author Rafael Praxedes
 */
public class Node {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if ((object == null) || (getClass() != object.getClass())) {
			return false;
		}

		Node node = (Node)object;

		if ((_initial == node._initial) && Objects.equals(_name, node._name) &&
			(_nodeId == node._nodeId) && (_processId == node._processId) &&
			Objects.equals(_processVersion, node._processVersion) &&
			(_terminal == node._terminal) &&
			Objects.equals(_type, node._type)) {

			return true;
		}

		return false;
	}

	public boolean getInitial() {
		return _initial;
	}

	public String getName() {
		return _name;
	}

	public long getNodeId() {
		return _nodeId;
	}

	public long getProcessId() {
		return _processId;
	}

	public String getProcessVersion() {
		return _processVersion;
	}

	public boolean getTerminal() {
		return _terminal;
	}

	public String getType() {
		return _type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			_initial, _name, _nodeId, _processId, _processVersion, _terminal,
			_type);
	}

	public void setInitial(boolean initial) {
		_initial = initial;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setNodeId(long nodeId) {
		_nodeId = nodeId;
	}

	public void setProcessId(long processId) {
		_processId = processId;
	}

	public void setProcessVersion(String processVersion) {
		_processVersion = processVersion;
	}

	public void setTerminal(boolean terminal) {
		_terminal = terminal;
	}

	public void setType(String type) {
		_type = type;
	}

	private boolean _initial;
	private String _name;
	private long _nodeId;
	private long _processId;
	private String _processVersion;
	private boolean _terminal;
	private String _type;

}