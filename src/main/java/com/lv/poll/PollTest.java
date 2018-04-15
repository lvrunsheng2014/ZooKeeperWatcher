package com.lv.poll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import org.apache.log4j.Logger;

public class PollTest {

	  /**
	   * Logger for this class
	   * 
	   * 轮询算法的理解，算法
	   */
	   private static final Logger logger = Logger.getLogger(PollTest.class);
	   //定义常量
	   private static final String CONNECTSTRING = "192.168.40.130:2181"; //zookeeper的IP和端口
	   private static final int    SESSION_TIMEOUT = 50*1000; //session的超时时间
	   private static final String PATH = "/zht";
	   private static final String SUB_PREFIX = "sub";
	   //定义实例变量
	   private ZooKeeper zk = null;
	   private int subCount = 5; //银行有5个窗口
	   private List<String> serviceNodeLists = new ArrayList<String>();//服务节点列表
	   private int serviceNum = 0; //第几个服务
	   private int s = 0;
	   
	   //以下为业务方法
	   public ZooKeeper startZK() throws IOException
	   {
	       return new ZooKeeper(CONNECTSTRING, SESSION_TIMEOUT, new Watcher() {
	          @Override
	          public void process(WatchedEvent event)
	          {
	              try 
	              {
	                 serviceNodeLists = zk.getChildren(PATH, true);
	                 System.out.println(serviceNodeLists);
	              }catch (KeeperException | InterruptedException e) {
	                 e.printStackTrace();
	              }
	          }
	       });
	   }
	   
	   public String dealRequest() throws KeeperException, InterruptedException
	   {
	       serviceNum = serviceNum +1;
	       
	       //用if判断代替第二个for循环
//	       if (serviceNum>5) {
//	    	   serviceNum = serviceNum %subCount;
//	       }
	       
	       for (int i = serviceNum; i <=subCount; i++) 
	       {
	          if(serviceNodeLists.contains(SUB_PREFIX+serviceNum))
	          {
	              return new String(zk.getData(PATH+"/"+SUB_PREFIX+serviceNum, false, new Stat()));
	          }else{
	              serviceNum = serviceNum +1;
	          }
	       }
	     
	       for (int i = 1; i <=subCount; i++) 
	       {
	          if(serviceNodeLists.contains(SUB_PREFIX+i))
	          {
	              serviceNum = i;
	              return new String(zk.getData(PATH+"/"+SUB_PREFIX+serviceNum, false, new Stat()));             
	          }
	       }     
	       return "null node~~~~~";
	   }
	   
	   public static void main(String[] args) throws IOException, KeeperException, InterruptedException
	   {
	       PollTest test = new PollTest();
	       
	       test.setZk(test.startZK());
	       Thread.sleep(3000);
	       String result = null;
	       //以轮询的方式访问15次，共计5个节点来应付实现负载均衡
	       for (int i = 1; i <=15; i++) 
	       {
	          result = test.dealRequest();
	          System.out.println("****loop:"+i+"\t"+test.serviceNum+"\t"+result);
	          Thread.sleep(2000);
	       }
	   }
	 
	   //setter---getter
	   public ZooKeeper getZk()
	   {
	       return zk;
	   }
	   public void setZk(ZooKeeper zk)
	   {
	       this.zk = zk;
	   }  
}
