package descent.rps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Node;

public abstract class APeerSampling implements IDynamic, Linkable, CDProtocol, IPeerSampling {

	// #A the names of the parameters in the configuration file of peersim
	private static final String PAR_FAIL = "fail"; // proba of fail of each peer

	// #B the values from the configuration file of peersim
	protected static double fail;

	// #C local variables
	protected boolean isUp = false;
	public Node node = null;

	/**
	 * Constructor of the class
	 * 
	 * @param prefix
	 *            configuration of peersim
	 */
	public APeerSampling(String prefix) {
		APeerSampling.fail = Configuration.getDouble(prefix + "." + APeerSampling.PAR_FAIL, 0.0);
	}

	public APeerSampling() {
	}

	// must be implemented in the child class
	public abstract IPeerSampling clone();

	public abstract boolean addNeighbor(Node peer);

	/**
	 * Compute the probability that the connection establishment fails. A fail setup
	 * means that locally, the peer has the reference to the remote peer but the arc
	 * (or link (or connection)) associated to it does not work. It depends of the
	 * number of hops before reaching the peer to connect to. The inbetween arcs and
	 * peer must remains up for the round-trip, mandatory in three-way handshake
	 * connection context.
	 * 
	 * @param path
	 *            the path traveled by the connection
	 * @return true if the connection establishment fails, false otherwise
	 */
	protected abstract boolean pFail(List<Node> path);

	public void onKill() {
	}

	public void nextCycle(Node node, int pid) {
		// #1 lazy loading of the reference of the node
		this._setNode(node);
		// #2 call the periodic function of the node every Delta time
		if (isUp()) {
			this.periodicCall();
		}
	}

	public boolean contains(Node neighbor) {
		boolean found = false;
		Iterator<Node> iNeighbors = this.getPeers(Integer.MAX_VALUE).iterator();
		while (!found && iNeighbors.hasNext()) {
			if (iNeighbors.next().getID() == neighbor.getID()) {
				found = true;
			}
		}
		return found;
	}

	/**
	 * Getter of the list of alive neighbors
	 * 
	 * @return a list of nodes
	 */
	public Iterable<Node> getAliveNeighbors() {
		ArrayList<Node> result = new ArrayList<Node>();
		for (Node neighbor : this.getPeers()) {
			if (neighbor.isUp()) {
				result.add(neighbor);
			}
		}
		return result;
	}

	/**
	 * Getter of the list of dead neighbors
	 * 
	 * @return a list of nodes
	 */
	public Iterable<Node> getDeadNeighbors() {
		ArrayList<Node> result = new ArrayList<Node>();
		for (Node neighbor : this.getPeers()) {
			if (!neighbor.isUp()) {
				result.add(neighbor);
			}
		}
		return result;
	}

	public boolean isUp() {
		return this.isUp;
	}

	public boolean isDown() {
		return !this.isUp;
	}

	public int degree() {
		Iterator<Node> neighbors = this.getAliveNeighbors().iterator();
		Integer sum = 0;
		while (neighbors.hasNext()) {
			++sum;
			neighbors.next();
		}
		return sum;
	}

	public Node getNeighbor(int n) {
		Iterator<Node> neighbors = this.getPeers().iterator();
		Node result = neighbors.next();
		for (int i = 0; i < n; ++i) {
			result = neighbors.next();
		}
		return result;
	}

	public void pack() {
	}

	protected void _setNode(Node n) {
		if (this.node == null) {
			this.node = n;
		}

	}
}
