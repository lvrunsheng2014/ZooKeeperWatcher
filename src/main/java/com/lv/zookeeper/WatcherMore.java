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
public class WatcherMore {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WatchOne.class);
	protected static final String CONNECTSTRING="192.168.166.128:2181";
	protected static final String PATH="/atguigu";
	protected static final int SESSIONTIMEOUT=20*1000;
	//实例变量
	private @Setter @Getter  ZooKeeper zk=null;
	private @Setter @Getter String newValue="";
	private @Setter @Getter String oldValue="";
	
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
			
	public void createZnode(String path,String data) throws KeeperException, InterruptedException {
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
	public String getZnode(String path) throws Exception, InterruptedException {
		String result="";
		byte[] data = zk.getData(path, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// 业务逻辑
				try {
					triggerValue(path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, new Stat());
		result=new String(data);
		oldValue=result;
		return result;
		
	}
	
	
	public boolean triggerValue(String path) throws Exception  {
		String result="";
		
		byte[] data1=zk.getData(path,  new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// 业务逻辑
				try {
					triggerValue(path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, new Stat());
		result=new String(data1);
		newValue=result;
		if(newValue.equals(oldValue)) {
			logger.info("--------------the value is not change-------------------f" );
			return false;
		}else {
			logger.info("the oldValue"+"\t"+oldValue+"\t the newValue \t"+newValue );
			oldValue=newValue;
			return true;
		}
	
		
	}

	public static void main(String[] args) throws Exception {
		WatcherMore one=new WatcherMore();
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
	}
}

