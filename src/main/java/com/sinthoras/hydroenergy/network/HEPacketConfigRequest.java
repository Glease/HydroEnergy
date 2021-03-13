package com.sinthoras.hydroenergy.network;

import com.sinthoras.hydroenergy.server.HEServer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class HEPacketConfigRequest implements IMessage {

    public int waterId;
    public boolean debugState;
    public int limitWest;
    public int limitDown;
    public int limitNorth;
    public int limitEast;
    public int limitUp;
    public int limitSouth;

    public HEPacketConfigRequest(int waterId, boolean debugState, int limitWest, int limitDown, int limitNorth, int limitEast, int limitUp, int limitSouth) {
        this.waterId = waterId;
        this.debugState = debugState;
        this.limitWest = limitWest;
        this.limitDown = limitDown;
        this.limitNorth = limitNorth;
        this.limitEast = limitEast;
        this.limitUp = limitUp;
        this.limitSouth = limitSouth;
    }

    public HEPacketConfigRequest() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        waterId = buf.readInt();
        debugState = buf.readBoolean();
        limitWest = buf.readInt();
        limitDown = buf.readInt();
        limitNorth = buf.readInt();
        limitEast = buf.readInt();
        limitUp = buf.readInt();
        limitSouth = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(waterId);
        buf.writeBoolean(debugState);
        buf.writeInt(limitWest);
        buf.writeInt(limitDown);
        buf.writeInt(limitNorth);
        buf.writeInt(limitEast);
        buf.writeInt(limitUp);
        buf.writeInt(limitSouth);
    }

    public static class Handler implements IMessageHandler<HEPacketConfigRequest, IMessage> {

        @Override
        public IMessage onMessage(HEPacketConfigRequest message, MessageContext ctx) {
            HEServer.instance.onConfigRequest(message.waterId,
                    message.debugState,
                    message.limitWest,
                    message.limitDown,
                    message.limitNorth,
                    message.limitEast,
                    message.limitUp,
                    message.limitSouth);
            return null;
        }
    }
}
