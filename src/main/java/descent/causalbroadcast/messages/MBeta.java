package descent.causalbroadcast.messages;

import peersim.core.Node;

/**
 * When Process A adds a link to Process B, this is the second control message
 * that acknowledges the first receipt of alpha. On receipt, Process A starts
 * buffering.
 */
public class MBeta implements IMControlMessage {

	public final Node from;
	public final Node to;
	public final Node mediator;

	public MBeta(Node from, Node mediator, Node to) {
		this.from = from;
		this.to = to;
		this.mediator = mediator;
	}

	public Node getMediator() {
		return this.mediator;
	}

	public Node getFrom() {
		return this.from;
	}

	public Node getTo() {
		return this.to;
	}

	public Node getSender() {
		return this.to;
	}

	public Node getReceiver() {
		return this.from;
	}
}
