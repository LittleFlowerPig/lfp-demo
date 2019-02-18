package com.lfp.demo.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Date;

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
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        // 每个连接的具体任务处理
        NioEventLoopGroup group = new NioEventLoopGroup();

        // 创建引导类
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 配置引导类
            bootstrap
                    // 配置（线程模型）
                    .group(group)
                    // 配置（IO模型）
                    .channel(NioSocketChannel.class)
                    // 配置 NioServerSocketChannel（处理逻辑）
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            // 出站 编码
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    });

            Channel channel = bootstrap.connect("127.0.0.1", 8000).channel();

            while (true) {
                channel.writeAndFlush(new Date() + ": hello world!");
                Thread.sleep(2000);
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
