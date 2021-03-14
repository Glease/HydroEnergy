package com.sinthoras.hydroenergy.network;

import com.sinthoras.hydroenergy.client.HEClient;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class HEPacketConfigUpdate implements IMessage {

    public int waterId;
    public int blockX;
    public int blockY;
    public int blockZ;
    public boolean debugState;
    public boolean drainState;
    public int limitWest;
    public int limitDown;
    public int limitNorth;
    public int limitEast;
    public int limitUp;
    public int limitSouth;

    public HEPacketConfigUpdate(int waterId, int blockX, int blockY, int blockZ, boolean debugState, boolean drainState, int limitWest, int limitDown, int limitNorth, int limitEast, int limitUp, int limitSouth) {
        this.waterId = waterId;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.debugState = debugState;
        this.drainState = drainState;
        this.limitWest = limitWest;
        this.limitDown = limitDown;
        this.limitNorth = limitNorth;
        this.limitEast = limitEast;
        this.limitUp = limitUp;
        this.limitSouth = limitSouth;
    }

    public HEPacketConfigUpdate() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        waterId = buf.readInt();
        blockX = buf.readInt();
        blockY = buf.readInt();
        blockZ = buf.readInt();
        debugState = buf.readBoolean();
        drainState = buf.readBoolean();
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
        buf.writeInt(blockX);
        buf.writeInt(blockY);
        buf.writeInt(blockZ);
        buf.writeBoolean(debugState);
        buf.writeBoolean(drainState);
        buf.writeInt(limitWest);
        buf.writeInt(limitDown);
        buf.writeInt(limitNorth);
        buf.writeInt(limitEast);
        buf.writeInt(limitUp);
        buf.writeInt(limitSouth);
    }

    public static class Handler implements IMessageHandler<HEPacketConfigUpdate, IMessage> {

        @Override
        public IMessage onMessage(HEPacketConfigUpdate message, MessageContext ctx) {
            HEClient.onConfigUpdate(message.waterId,
                    message.blockX,
                    message.blockY,
                    message.blockZ,
                    message.drainState,
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
