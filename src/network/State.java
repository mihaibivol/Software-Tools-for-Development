package network;

public enum State {
	sendRequest,
	sendRequestRemaining,
	waitFileSize,
	acceptRequest,
	acceptRequestRemaining,
	downloading,
	uploadBegin,
	uploading
};