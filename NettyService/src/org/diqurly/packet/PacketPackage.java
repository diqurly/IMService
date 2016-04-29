package org.diqurly.packet;

import io.netty.channel.Channel;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ÏûÏ¢·â×°
 * 
 * @author diqurly
 *
 */
public class PacketPackage {
	private static final Logger log = LoggerFactory
			.getLogger(PacketPackage.class);

	public static <E extends Channel> Packet packageing(String msg, E connect) {
		try {
			JSONObject jsonObject = new JSONObject(msg);
			String type = jsonObject.getString("type");
			if ("chat".equals(type)) {
				Message message = new Message(jsonObject);
				return message;
			} else if ("groupchat".equals(type)) {
				Message message = new Message(jsonObject);
				return message;
			} else if ("error".equals(type)) {
				// Message message=(Message) packet;
				return null;
			} else if ("pressence".equals(type)) {
				return null;
			} else if ("iq".equals(type)) {
				return null;
			} else if ("ping".equals(type)) {
				Ping ping = new Ping(jsonObject);
				ping.setType("reply");
				connect.writeAndFlush(ping.toJson());
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
			connect.close();
		}
		return null;
	}
}
