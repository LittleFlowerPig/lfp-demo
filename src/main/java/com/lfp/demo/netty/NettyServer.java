package com.lfp.demo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Project: lfp-demo
 * Title:
 * Description:
 * Date: 2019-02-13
 * Copyright: Copyright (c) 2019
 * Company:
 *
 * @author ZhuTao
 * @version 2.0
 */
public class NettyServer {

    public static void main(String[] args) {
        // 监听端口，创建连接
        NioEventLoopGroup boss = new NioEventLoopGroup();
        // 每个连接的具体任务处理
        NioEventLoopGroup worker = new NioEventLoopGroup();

        // 创建引导类
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            // 配置引导类
            serverBootstrap
                    // 配置（线程模型）
                    .group(boss, worker)
                    // 配置（IO模型）
                    // -BIO:OioServerSocketChannel.class
                    // -NIO:NioServerSocketChannel.class
                    // -EPOLL:
                    // -KQUEUE:
                    .channel(NioServerSocketChannel.class)
                    // 配置 NioServerSocketChannel（处理逻辑）
                    .handler(new ChannelInitializer<NioServerSocketChannel>() {
                        @Override
                        protected void initChannel(NioServerSocketChannel ch) {
                            // 入站 处理
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("channelActive");
                                    super.channelActive(ctx);
                                }

                                @Override
                                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("channelRegistered");
                                    super.channelRegistered(ctx);
                                }

                                @Override
                                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("handlerΩdded");
                                    super.handlerAdded(ctx);
                                }
                            });
                        }
                    })
                    // 配置 NioSocketChannel（处理逻辑）
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            // 入站 解码
                            ch.pipeline().addLast(new StringDecoder());
                            // 入站 处理
                            ch.pipeline().addLast(new ServerHandler());
                            // 出站 编码
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    });
            //serverBootstrap.attr();           // 配置 NioServerSocketChannel 属性
            //serverBootstrap.childHandler();   // 配置每一条连接 NioSocketChannel 属性
            //serverBootstrap.option();         // 配置 NioServerSocketChannel TCP底层相关
            //serverBootstrap.childOption();    // 配置每一条连接 NioSocketChannel TCP底层相关属性

            // 绑定端口并启动
            // -静态绑定 serverBootstrap.bind(8000)
            // -动态绑定 dynamicBind(serverBootstrap,8098)
            ChannelFuture f = serverBootstrap.bind(8000).sync();
            // 阻塞等待结果
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    private static ChannelFuture dynamicBind(final ServerBootstrap serverBootstrap, final int port){
        return serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
                dynamicBind(serverBootstrap, port+1);
            }
        });
    }


}
