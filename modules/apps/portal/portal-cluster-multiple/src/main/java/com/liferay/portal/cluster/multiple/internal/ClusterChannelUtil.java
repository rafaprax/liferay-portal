/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.net.InetAddress;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Renan Vasconcelos
 */
public class ClusterChannelUtil {

	public static void closeClusterChannel() {
		_clusterChannel.close();
	}

	public static InetAddress getBindInetAddress() {
		return _clusterChannel.getBindInetAddress();
	}

	public static ClusterChannel getClusterChannel() {
		return _clusterChannel;
	}

	public static String getClusterNodeId(Address address) {
		CompletableFuture<String> completableFuture =
			_clusterNodeIdCompletableFutures.computeIfAbsent(
				address, key -> new CompletableFuture<>());

		try {
			return completableFuture.get(
				clusterExecutorConfiguration.clusterNodeAddressTimeout(),
				TimeUnit.MILLISECONDS);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get cluster node with address " + address,
				exception);
		}

		return null;
	}

	public static CompletableFuture<String> getClusterNodeIdCompletableFutures(
		Address address) {

		return _clusterNodeIdCompletableFutures.computeIfAbsent(
			address, key -> new CompletableFuture<>());
	}

	public static Address getLocalAddress() {
		return _clusterChannel.getLocalAddress();
	}

	public static void removeClusterNodeIdCompletableFutures(Address address) {
		_clusterNodeIdCompletableFutures.remove(address);
	}

	public static void sendMulticastMessage(ClusterRequest clusterRequest) {
		_clusterChannel.sendMulticastMessage(clusterRequest);
	}

	public static void sendUnicastMessage(
		ClusterRequest clusterRequest, Address address) {

		_clusterChannel.sendUnicastMessage(clusterRequest, address);
	}

	public static void setClusterChannel(ClusterChannel clusterChannel) {
		_clusterChannel = clusterChannel;
	}

	protected static volatile ClusterExecutorConfiguration
		clusterExecutorConfiguration;

	private static final Log _log = LogFactoryUtil.getLog(
		ClusterChannelUtil.class);

	private static volatile ClusterChannel _clusterChannel;
	private static final Map<Address, CompletableFuture<String>>
		_clusterNodeIdCompletableFutures = new ConcurrentHashMap<>();

}