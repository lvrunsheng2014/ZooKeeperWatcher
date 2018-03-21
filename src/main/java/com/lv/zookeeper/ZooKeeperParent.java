package com.lv.zookeeper;

import org.apache.zookeeper.ZooKeeper;

public class ZooKeeperParent {
	protected static final String CONNECTSTRING="192.168.166.128:2181";
	protected static final String PATH="/atguigu";
	protected static final int SESSIONTIMEOUT=20*1000;
	
	public void stopZK(ZooKeeper zk) throws InterruptedException {
		if(null!=zk) zk.close();
	}
	
}
