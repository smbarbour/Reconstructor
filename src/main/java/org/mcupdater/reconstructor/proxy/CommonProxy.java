package org.mcupdater.reconstructor.proxy;

public class CommonProxy
{
	protected boolean client;

	public CommonProxy() {
		client = false;
	}

	public boolean isClient() {
		return client;
	}

	public void doClientRegistrations() {

	}
}
