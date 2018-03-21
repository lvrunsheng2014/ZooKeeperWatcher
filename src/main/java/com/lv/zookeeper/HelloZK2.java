package com.lv.zookeeper;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * eclipse此处为Client端，CentOS为ZooKeeper的server端
 * 1 通过Java程序，新建连接ZK,类似jdbc的connection，open session
 * 2 新建一个znode节点/atguigu并设置为helloWorld 等同是 create /atguigu helloWorld
 * 3 获取当前节点/atguigu的最新值
 * 4 关闭链接
 * @author lvrun
 * 
 *
 */
public class HelloZK2 extends ZooKeeperParent{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(HelloZK2.class);
	
	public ZooKeeper startZK() throws IOException {
		return new ZooKeeper(CONNECTSTRING, SESSIONTIMEOUT, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	/**
	 * 创建一个节点并赋值
	 * @param zk
	 * @param path
	 * @param data
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
			
	public void createZnode(ZooKeeper zk,String path,String data) throws KeeperException, InterruptedException {
		zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	/**
	 * 获取当前节点的最新值
	 * 
	 * @param zk
	 * @param path
	 * @return void
	 * @throws Exception
	 * @throws InterruptedException
	 */
	public String getZnode(ZooKeeper zk,String path) throws Exception, InterruptedException {
		String result="";
		byte[] data = zk.getData(path, false, new Stat());
		result=new String(data);
		return result;
		
	}
	
	
	public static void main(String[] args) throws Exception{
		HelloZK2 hz=new HelloZK2();
		ZooKeeper zk = hz.startZK();
		if(zk.exists(PATH, false)==null) {
			hz.createZnode(zk, PATH, "helloWorld");
			String result = hz.getZnode(zk, PATH);

			if (logger.isInfoEnabled()) {
				logger.info("main(String[]) - String result=" + result);
			}
		}else {
			logger.info("this is node exists");
		}
		hz.stopZK(zk);
	}
}
