package com.javaworker;

import improbable.worker.*;

public class JavaWorker {
	private static final int FRAMES_PER_SECOND = 60;

	public static void main(String[] args) {
		if(args.length > 0){
			for(String arg : args){
				System.out.println(arg);
			}
			Connection connection = getConnection(args[1], args[0], Integer.parseInt(args[1]));
		} else {
			System.out.println("No args passed. Exiting...");
			System.exit(1);
		}
	}

	private static Connection getConnection(String workerId, String hostname, int port) {
		ConnectionParameters parameters = new ConnectionParameters();
		parameters.workerType = "JavaWorker";
		parameters.network = new NetworkParameters();
		parameters.network.connectionType = NetworkConnectionType.Tcp;
		parameters.network.useExternalIp = false;

		return Connection.connectAsync(hostname, port, workerId, parameters).get();
	}

	private static void runEventLoop(Connection connection, Dispatcher dispatcher) {
		java.time.Duration maxWait = java.time.Duration.ofMillis(Math.round(1000.0 / FRAMES_PER_SECOND));
		while (true) {
			long startTime = System.nanoTime();
			OpList opList = connection.getOpList(0 /* non-blocking */);
			// Invoke callbacks.
			dispatcher.process(opList);
			// Do other work here...
			long stopTime = System.nanoTime();
			java.time.Duration waitFor = maxWait.minusNanos(stopTime - startTime);
			try {
				Thread.sleep(Math.max(waitFor.toMillis(), 0));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		}
	}

}
