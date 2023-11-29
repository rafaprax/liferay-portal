/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cluster;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public interface ClusterExecutor {

	public FutureClusterResponses execute(ClusterRequest clusterRequest);

	public InetAddress getBindInetAddress();

	public NetworkInterface getBindNetworkInterface();

	public List<ClusterEventListener> getClusterEventListeners();

	public List<ClusterNode> getClusterNodes();

	public ClusterNode getLocalClusterNode();

	public boolean isClusterNodeAlive(String clusterNodeId);

	public boolean isEnabled();

	void handleReceivedClusterNodeResponse(ClusterNodeResponse messagePayload);

	void sendNotifyRequest();

	void memberRemoved(List<Address> removedAddresses);

	ClusterChannel getClusterChannel();

	Serializable handleReceivedClusterRequest(ClusterRequest clusterRequest);

	void fireClusterEvent(ClusterEvent clusterEvent);

	String getClusterNodeId(Address coordinatorAddress);
}