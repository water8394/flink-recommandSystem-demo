package com.demo.scheduler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wangxc
 * @date: 2019/7/30 下午11:23
 *
 */
public class TestMain {
	public static void main(String[] args) {
		Timer qTimer = new Timer();
		qTimer.scheduleAtFixedRate(new DemoTask(), 0, 2000);// 定时每15分钟

	}

	private static class DemoTask extends TimerTask {
		@Override
		public void run() {
			System.out.println("每刻任务已执行");
		}
	}
}
