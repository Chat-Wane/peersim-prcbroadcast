package descent.broadcast.causal.prcbroadcast.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections4.bag.HashBag;

import descent.broadcast.causal.prcbroadcast.MBuffer;
import descent.broadcast.causal.prcbroadcast.PreventiveReliableCausalBroadcast;
import descent.broadcast.reliable.MReliableBroadcast;
import descent.rps.APeerSampling;
import descent.rps.IMessage;
import descent.rps.IPeerSampling;
import descent.spray.SprayPartialView;
import peersim.config.FastConfig;
import peersim.core.Node;
import peersim.transport.Transport;

/**
 * Spray with a light form of routing capabilities : the mediator knows both
 * peers and routes control messages back and forth until new links become safe.
 */
public class SprayWithRouting extends APeerSampling implements IRoutingService {

	public static int pid; // (TODO) rework that

	public PreventiveReliableCausalBroadcast parent; // (TODO) Interface CB + PS

	public Routes routes;

	public SprayPartialView outview;
	public HashBag<Node> inview;

	public HashSet<Node> unsafe;

	public SprayWithRouting(PreventiveReliableCausalBroadcast parent) {
		this.parent = parent;

		this.routes = new Routes();

		this.outview = new SprayPartialView();
		this.inview = new HashBag<Node>();

		this.unsafe = new HashSet<Node>();
	}

	// PEER-SAMPLING:

	public void periodicCall() {
		// TODO Auto-generated method stub

	}

	public IMessage onPeriodicCall(Node origin, IMessage message) {
		// TODO Auto-generated method stub
		return null;
	}

	public void join(Node joiner, Node contact) {
		this._clear();
		this._setNode(joiner);
		if (contact != null) {
			// #1 the very first connection is safe
			this.outview.addNeighbor(contact);
			// #2 subsequent ones might not be
			SprayWithRouting swr = (SprayWithRouting) contact.getProtocol(SprayWithRouting.pid);
			swr.onSubscription(joiner);
		}
		this.isUp = true;
	}

	public void onSubscription(Node origin) {
		this.inview.add(origin);
		for (Node neighbor : this.outview.getPeers()) {
			this._send(neighbor, new MConnectTo(neighbor, origin, this.node));
		}
	}

	public void leave() {
		// (TODO)
		// #0 Goes down.
		this.isUp = false;
		// #1 Immediately remove in the in-views.
		for (Node neighbor : this.outview.getPeers()) {
			SprayWithRouting swr = (SprayWithRouting) neighbor.getProtocol(SprayWithRouting.pid);
			swr._closeI(this.node);
		}
	}

	/**
	 * A neighbor in our in-view just left. Remove the occurrences in our local
	 * structure.
	 * 
	 * @param leaver
	 *            The leaver identity.
	 */
	private void _closeI(Node leaver) {
		this.inview.remove(leaver);
		this.parent.closeI(leaver);
		this.unsafe.remove(leaver);
		this._removeAllRoutes(leaver); // (TODO)
	}

	@Override
	public boolean addNeighbor(Node peer) {
		// TODO Auto-generated method stub
		return false;
	}

	// ROUTING:

	/**
	 * Remove all routes coming from a node
	 * 
	 * @param toRemove
	 *            The node to remove from routing tables.
	 */
	private void _removeAllRoutes(Node toRemove) {
		// (TODO)
	}

	public void receiveMConnectTo(Node from, Node to, Node mediator) {
		// (TODO)
	}

	public void sendAlpha(Node from, Node to) {
		// TODO Auto-generated method stub

	}

	public void sendBeta(Node from, Node to) {
		// TODO Auto-generated method stub

	}

	public void sendPi(Node from, Node to) {
		// TODO Auto-generated method stub

	}

	public void sendRho(Node from, Node to) {
		Node receiver = to;
		if (this.node == to) {
			receiver = from;
		}
	}

	public void sendBuffer(Node from, Node to, ArrayList<MReliableBroadcast> buffer) {
		// #0 bidirectional, Process "to" also sends a buffer
		Node receiver = this._getReceiver(from, to);
		// #1 check if there is an issue with algo
		if (!this.unsafe.contains(receiver)) {
			System.out.println("Send buffer but seems safe already");
			return;
		}
		// #2 send the buffer
		MBuffer m = new MBuffer(from, to, buffer);
		this._sendUnsafe(receiver, m);
	}

	/**
	 * Send a message using safe channels and processed routes.
	 * 
	 * @param to
	 *            The target ultimately.
	 */
	private void _send(Node to, Object m) {
		if ((this.outview.contains(to) || this.inview.contains(to) && !this.unsafe.contains(to))) {
			((Transport) this.node.getProtocol(FastConfig.getTransport(SprayWithRouting.pid))).send(this.node, to, m,
					SprayWithRouting.pid);
		}
	}

	/**
	 * Send a message using possibly unsafe channels and processed routes.
	 * 
	 * @param to
	 *            The target ultimately.
	 */
	private void _sendUnsafe(Node to, Object m) {
		if ((this.outview.contains(to) || this.inview.contains(to))) {
			((Transport) this.node.getProtocol(FastConfig.getTransport(SprayWithRouting.pid))).send(this.node, to, m,
					SprayWithRouting.pid);
		}
	}

	public void sendToOutview(MReliableBroadcast m) {
		for (Node n : this.getOutview()) {
			this._send(n, m);
		}
	}

	public Node _getReceiver(Node from, Node to) {
		if (this.node == to) {
			return from;
		} else {
			return to;
		}
	}

	public HashSet<Node> getOutview() {
		// since bidirectionnal, outview includes inview
		HashSet<Node> result = new HashSet<Node>();
		for (Node n : this.outview.getPeers()) {
			if (!this.unsafe.contains(n))
				result.add(n);
		}
		for (Node n : this.inview) {
			if (!this.unsafe.contains(n))
				result.add(n);
		}
		return result;
	}

	public void setNeighborSafe(Node n) {
		this.unsafe.remove(n);
	}

	public void setNeighborUnsafe(Node n) {
		this.unsafe.add(n);
	}

	// PEER-SAMPLING BASICS:

	public Iterable<Node> getPeers(int k) {
		// (TODO) inview ?
		return this.outview.getPeers(k);
	}

	public Iterable<Node> getPeers() {
		// (TODO) inview ?
		return this.outview.getPeers();
	}

	@Override
	public IPeerSampling clone() {
		return new SprayWithRouting(this.parent);
	}

	@Override
	protected boolean pFail(List<Node> path) {
		return false;
	}

	/**
	 * Reset the internal structures. Not very clean but garbage collecting will
	 * do the job.
	 */
	private void _clear() {
		this.routes = new Routes();
		this.outview = new SprayPartialView();
		this.inview = new HashBag<Node>();
		this.unsafe = new HashSet<Node>();
	}

}
