package com.tmax.serverless.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.jetbrains.annotations.NotNull;

public class ConnectionObservable extends ChannelInboundHandlerAdapter {

  public final static String PROPERTY = "connected";
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  public ConnectionObservable(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  @Override
  public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
    support.firePropertyChange(PROPERTY, null, true);
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
    support.firePropertyChange(PROPERTY, null, false);
    super.channelInactive(ctx);
  }
}
