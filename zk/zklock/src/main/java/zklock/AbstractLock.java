package zklock;

import org.I0Itec.zkclient.ZkClient;

public abstract class AbstractLock {

	//zk地址和端口
	public static final String ZK_ADDR = "192.168.0.230:2181";
	//超时时间
	public static final int SESSION_TIMEOUT = 10000;
	//创建zk
	protected ZkClient zkClient = new ZkClient(ZK_ADDR, SESSION_TIMEOUT);
	
	
	/**
	 * 可以认为是模板模式，两个子类分别实现它的抽象方法
	 * 1，简单的分布式锁
	 * 2，高性能分布式锁
	 */
	public void getLock() {
		String threadName = Thread.currentThread().getName();
		if (tryLock()) {
			System.out.println(threadName+"-获取锁成功");
		}else {
			System.out.println(threadName+"-获取锁失败，进行等待...");
			waitLock();
			//递归重新获取锁
			getLock();
		}
	}
	
	public abstract void releaseLock();
	
	public abstract boolean tryLock();
	
	public abstract void waitLock();
}
