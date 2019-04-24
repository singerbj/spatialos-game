package com.javaworker;

import java.util.ArrayList;
import java.util.List;

import demo.PingResponder;
import demo.Pong;
import improbable.worker.*;

public class JavaWorker {
	public static void main(String[] args) {
		if (args.length > 0) {
			Connection connection = getConnection(args[2], args[0], Integer.parseInt(args[1]));
			Dispatcher dispatcher = new Dispatcher();
			connection.sendLogMessage(LogLevel.INFO, "JavaWorker", "JavaWorker - Successfully connected using TCP and the Receptionist");
			registerCallbacks(connection, dispatcher);
			runEventLoop(connection, dispatcher);
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
//		parameters.network.useExternalIp = false;

		return Connection.connectAsync(hostname, port, workerId, parameters).get();
	}

	private static java.util.List<Long> registerCallbacks(Connection connection, Dispatcher dispatcher) {
		List<Long> callbackKeys = new ArrayList<Long>();

		long callbackKey;
		callbackKey = dispatcher.onDisconnect(request -> {
			System.out.println("Disconnected!");
		});
		callbackKeys.add(callbackKey);
		
		callbackKey = dispatcher.onCommandRequest(PingResponder.Commands.PING, request -> {
			connection.sendLogMessage(LogLevel.INFO, "JavaWorker", "Received GetWorkerType command");
			Pong pingResponse = new Pong("JavaWorker", "Hello from JavaWorker!");
			connection.sendCommandResponse(PingResponder.Commands.PING, request.requestId, pingResponse);
		});
		callbackKeys.add(callbackKey);
		
		return callbackKeys;
	}

	private static void runEventLoop(Connection connection, Dispatcher dispatcher) {
		while (true) {
			OpList opList = connection.getOpList(0 /* non-blocking */);
			dispatcher.process(opList);
		}
	}

}
