package kyi.serialros;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

import org.ros.address.InetAddressFactory;
import org.ros.android.android_acm_serial.AcmDevice;
import org.ros.android.android_acm_serial.AcmDeviceActivity;
import org.ros.exception.RosRuntimeException;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AcmDeviceActivity {

	private final CountDownLatch nodeRunnerServiceLatch;

	private NodeMainExecutor nodeMainExecutor;
	
	private Talker talker;

	public MainActivity() {
		super("Serial Node", "Serial Node");
		// TODO Auto-generated constructor stub
		nodeRunnerServiceLatch = new CountDownLatch(1);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void init(NodeMainExecutor nodeMainExecutor) {
		// TODO Auto-generated method stub
		nodeRunnerServiceLatch.countDown();
		this.nodeMainExecutor = nodeMainExecutor;
		Log.d("KYI", "On Init");
	}

	@Override
	public void onPermissionGranted(final AcmDevice acmDevice) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				Log.d("KYI", "Pm Granted");
				
				InputStream in = acmDevice.getInputStream();
				
				
				acmSerialPublisher(acmDevice, in);
				Log.d("KYI", "Publisher Start.");
			};
		}.start();
	}

	protected void acmSerialPublisher(AcmDevice acmDevice, InputStream in) {
		// TODO Auto-generated method stub
		try {
			nodeRunnerServiceLatch.await();
		} catch (InterruptedException e) {
			throw new RosRuntimeException(e);
		}

		talker = new Talker(in);
		
		NodeConfiguration nodeConfiguration =
				NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
		nodeConfiguration.setMasterUri(getMasterUri());
		
		nodeMainExecutor.execute(talker, nodeConfiguration);
		Log.i("KYI", "Talker Excuted");
	}

	@Override
	public void onPermissionDenied() {
		// TODO Auto-generated method stub

	}
}
