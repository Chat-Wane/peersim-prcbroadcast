package descent.bidirectionnal;

import java.util.HashSet;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import descent.rps.IMessage;
import descent.rps.IPeerSampling;
import descent.spray.Spray;
import peersim.core.Node;

/**
 * Spray with bidirectional communication links.
 */
public class BiSpray extends Spray {

	public Bag<Node> inview;
	public Bag<Node> outview;

	public BiSpray(String prefix) {
		super(prefix);
		this.inview = new HashBag<Node>();
		this.outview = new HashBag<Node>();
	}

	public BiSpray() {
		super();
		this.inview = new HashBag<Node>();
		this.outview = new HashBag<Node>();
	}

	@Override
	public void periodicCall() {
		HashSet<Node> before = new HashSet<Node>();
		before.addAll(inview.uniqueSet());
		before.addAll(outview.uniqueSet());
		System.out.println("before " + before.size());
		super.periodicCall();
		HashSet<Node> after = new HashSet<Node>();
		after.addAll(inview.uniqueSet());
		after.addAll(outview.uniqueSet());
		HashSet<Node> newPeers = new HashSet<Node>(after);
		newPeers.removeAll(before);
	}

	@Override
	public IMessage onPeriodicCall(Node origin, IMessage message) {
		HashSet<Node> before = new HashSet<Node>();
		before.addAll(inview.uniqueSet());
		before.addAll(outview.uniqueSet());
		IMessage m = super.onPeriodicCall(origin, message);
		HashSet<Node> after = new HashSet<Node>();
		after.addAll(inview.uniqueSet());
		after.addAll(outview.uniqueSet());
		HashSet<Node> newPeers = new HashSet<Node>(after);
		newPeers.removeAll(before);
		return m;
	}

	@Override
	public boolean addNeighbor(Node peer) {
		this.outview.add(peer);
		BiSpray bs = (BiSpray) peer.getProtocol(this.pid);
		bs.inview.add(peer);
		return super.addNeighbor(peer);
	}

	@Override
	public boolean removeNeighbor(Node peer) {
		this.outview.remove(peer, 1);
		BiSpray bs = (BiSpray) peer.getProtocol(this.pid);
		bs.inview.remove(peer, 1);
		return super.removeNeighbor(peer);
	}

	@Override
	public IPeerSampling clone() {
		return new BiSpray();
	}

}
