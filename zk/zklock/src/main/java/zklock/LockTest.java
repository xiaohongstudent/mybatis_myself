package zklock;

public class LockTest {
	public static void main(String[] args) {
		//模拟多个10个客户端
		for (int i=0;i<10;i++) {
			Thread thread = new Thread(new LockRunnable());
			thread.start();
		}
		
	}
	
	static class LockRunnable implements Runnable{

		@Override
		public void run() {
			AbstractLock simpleLock = new HighPerformanceZkLock();
			simpleLock.getLock();
			//模拟业务操作
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			simpleLock.releaseLock();
		}
		
	}
}
