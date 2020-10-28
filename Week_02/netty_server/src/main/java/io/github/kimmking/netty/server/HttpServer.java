package io.github.kimmking.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServer {

  private boolean ssl;
  private int port;

  public HttpServer(boolean ssl, int port) {
    this.port = port;
    this.ssl = ssl;
  }

  public void run() throws Exception {
    final SslContext sslCtx;
    if (ssl) {
      SelfSignedCertificate ssc = new SelfSignedCertificate();
      sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
    } else {
      sslCtx = null;
    }

    EventLoopGroup bossGroup = new NioEventLoopGroup(3);
    EventLoopGroup workerGroup = new NioEventLoopGroup(1000);

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.option(ChannelOption.SO_BACKLOG, 128)
          .childOption(ChannelOption.TCP_NODELAY, true)
          .option(ChannelOption.SO_KEEPALIVE, true)
          .option(ChannelOption.SO_REUSEADDR, true)
          .option(ChannelOption.SO_RCVBUF, 32 * 1024)
          .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
          //              .option(EpollChannelOption.SO_REUSEPORT, true) -- unix not windows
          .childOption(ChannelOption.SO_KEEPALIVE, true);
      // .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new HttpInitializer(sslCtx));

      Channel ch = b.bind(port).sync().channel();
      log.info(
          "开启netty http服务器，监听地址和端口为 " + (ssl ? "https" : "http") + "://127.0.0.1:" + port + '/');
      ch.closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
