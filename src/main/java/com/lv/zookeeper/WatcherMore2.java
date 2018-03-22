package com.lv.zookeeper;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.mysql.fabric.xmlrpc.base.Data;

import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @Description: 
 *1	初始化ZK的多个操作
 * 		1.1	建立ZK的链接
 * 		1.2	创建/atguigu节点并赋值
 * 		1.3	获得该节点的值
 * 
 * 2	watchmore
 * 		2.1	获得值之后设置一个观察者watcher，如果/atguigu该节点的值发生了变化，要求通知Client端，一次性通知
 * 
 * 3	watchMore
 * 		3.1	获得值之后设置一个观察者watcher，如果/atguigu该节点的值发生了变化，要求通知Client端,继续观察
 * 		3.2	又再次获得新的值的同时再新设置一个观察者，继续观察并获得值
 * 		3.3	又再次获得新的值的同时再新设置一个观察者，继续观察并获得值.。。。。。重复上述过程
 * @author zzyy
 * @date 2018年3月21日
 */
public class WatcherMore2 {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WatcherMore2.class);
	private static final String CONNECTSTRING="192.168.166.128:2181";
	private static final int SESSIONTIMEOUT=20*1000;
	private @Setter @Getter ZooKeeper zk=null;
	private @Setter @Getter String oldValue="";
	private @Setter @Getter String newValue="";
	private static final String PATH="/atguigu";
	public ZooKeeper startZK() throws Exception {
		return new ZooKeeper(CONNECTSTRING, SESSIONTIMEOUT, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void createZK(String path,String data) throws KeeperException, InterruptedException {
		zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	public String getNode(String path) throws KeeperException, InterruptedException {
		
		String result="";
		byte[] data = zk.getData(path, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				try {
					triggerValue(path);
				} catch (KeeperException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Stat());
		result=new String(data);
		oldValue=result;
		return result;
	}

	protected boolean triggerValue(String path) throws KeeperException, InterruptedException {
		// TODO Auto-generated method stub
		String result="";
		byte[] data = zk.getData(path, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				try {
					triggerValue(path);
				} catch (KeeperException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Stat());
		result=new String(data);
		newValue=result;
		if(oldValue.equals(newValue)) {
			logger.info("--------------the value is not change-------------------f" );
			return false;
		}else {
			logger.info("the oldValue"+"\t"+oldValue+"\t the newValue \t"+newValue );
			oldValue=newValue;
		}
		
		return false;
	}
	public static void main(String[] args) throws Exception {
		WatcherMore2 more=new WatcherMore2();
		more.setZk(more.startZK());
		if(more.getZk().exists(PATH, false)==null) {
			more.createZK(PATH, "AAAA");
			String result = more.getNode(PATH);
			if (logger.isInfoEnabled()) {
				logger.info("main(String[]) - String result=" + result);
			}
		}else {
			logger.info("this is node exists");
		}
		Thread.sleep(Long.MAX_VALUE);
	}

	/*public static void main(String[] args) throws Exception {
		WatcherMore2 one=new WatcherMore2();
		one.setZk(one.startZK());
		if(one.getZk().exists(PATH, false)==null) {
			one.createZnode(PATH, "AAAAA");
			String result = one.getZnode(PATH);
			if (logger.isInfoEnabled()) {
				logger.info("main(String[]) - String result=" + result);
			}
		}else {
			logger.info("this is node exists");
		}
		Thread.sleep(Long.MAX_VALUE);
	}*/
}

