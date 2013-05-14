package kyi.serialros;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

/**
 * A simple {@link Publisher} {@link NodeMain}.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 */
public class Talker extends AbstractNodeMain {

	InputStream _instrm;

	public Talker(InputStream inputstream){
		_instrm = inputstream;
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		// TODO Auto-generated method stub

		//Set Publisher
		final Publisher<std_msgs.String> publisher =
				connectedNode.newPublisher("chatter", std_msgs.String._TYPE);

		// This CancellableLoop will be canceled automatically when the node shuts
		// down.
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			@Override
			protected void loop() throws InterruptedException {
				// TODO Auto-generated method stub
				BufferedReader reader = new BufferedReader(new InputStreamReader(_instrm, Charset.forName("US-ASCII")));
				try {
					std_msgs.String str = publisher.newMessage();
					str.setData(reader.readLine());
					publisher.publish(str);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public GraphName getDefaultNodeName() {
		// TODO Auto-generated method stub
		return GraphName.of("PubsubTest/Talker");
	}

}