/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cluster;

import java.io.Serializable;

import java.net.InetAddress;
import java.net.NetworkInterface;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author Shuyang Zhou
 */
public interface ClusterExecutor {

	public FutureClusterResponses execute(ClusterRequest clusterRequest);

	public ClusterNodeResponse executeClusterRequest(
		ClusterRequest clusterRequest);

	public void fireClusterEvent(ClusterEvent clusterEvent);

	public InetAddress getBindInetAddress();

	public NetworkInterface getBindNetworkInterface();

	public ClusterChannel getClusterChannel();

	public List<ClusterEventListener> getClusterEventListeners();

	public String getClusterNodeId(Address address);

	public List<ClusterNode> getClusterNodes();

	public ExecutorService getExecutorService();

	public ClusterNode getLocalClusterNode();

	public void handleReceivedClusterNodeResponse(
		ClusterNodeResponse clusterNodeResponse);

	public Serializable handleReceivedClusterRequest(
		ClusterRequest clusterRequest);

	public boolean isClusterNodeAlive(String clusterNodeId);

	public boolean isEnabled();

	public void memberRemoved(List<Address> departAddresses);

	public void sendNotifyRequest();

}