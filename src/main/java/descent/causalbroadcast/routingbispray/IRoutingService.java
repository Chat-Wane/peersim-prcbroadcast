package descent.causalbroadcast.routingbispray;

import java.util.ArrayList;
import java.util.HashSet;

import descent.causalbroadcast.messages.MReliableBroadcast;
import peersim.core.Node;

/**
 * Service provided by peer-sampling service to route control messages from a
 * process to another process using safe links.
 */
public interface IRoutingService {

	public void sendAlpha(Node from, Node to);

	public void sendBeta(Node from, Node to);

	public void sendPi(Node from, Node to);

	public void sendRho(Node from, Node to);

	public void sendBuffer(Node dest, Node from, Node to, ArrayList<MReliableBroadcast> buffer);

	public void sendToOutview(MReliableBroadcast m);

	public HashSet<Node> getOutview();

	public void removeRouteAsMediator(Node from, Node to);

	public void removeRouteAsEndProcess(Node from, Node to);

	public void addToInView(Node neighbor);

	public boolean addToOutView(Node neighbor);

	public boolean isSafe(Node neighbor);
}