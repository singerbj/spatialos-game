package com.javaworker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import improbable.worker.*;
import improbable.common.*;
import improbable.collections.*;

public class JavaWorker {
	private static final int FRAMES_PER_SECOND = 60;

	public static void main(String[] args) {
		if(args.length > 0){
			Connection connection = getConnection(args[0], args[1], Integer.parseInt(args[2]));
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
