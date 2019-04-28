package com.snapshotter;

import improbable.worker.SnapshotOutputStream;

public class Empty {

	public static void main(String[] args) {
		SnapshotOutputStream emptySnapshot = new SnapshotOutputStream("empty.snapshot");
		emptySnapshot.close();
	}

}
