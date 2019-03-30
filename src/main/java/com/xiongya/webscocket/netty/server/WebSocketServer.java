package com.xiongya.webscocket.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * @Author xiongzhilong
 * @Email 2584496774@qq.com
 * @Date create by 2019-03-29 10:25
 */

@Component
public class WebSocketServer {

    private final static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    //初始化boss线程（boss线程）
    NioEventLoopGroup mainGrop = new NioEventLoopGroup();
    //初始化从线程（work线程）
    NioEventLoopGroup subGroup = new NioEventLoopGroup();

    @PostConstruct
    public void start(){
        try{
            //创建服务启动器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //指定使用主线程和从线程
            serverBootstrap.group(mainGrop, subGroup)
                    //指定使用NIO通道类型
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(8088))
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //指定通道初始化器用来加载当channel收到事件消息后，如何进行业务处理
                    .childHandler(new WsServerInitializer());

            //绑定端口启动服务器，并等待服务启动
            ChannelFuture future = serverBootstrap.bind().sync();

            //等待服务器关闭
            future.channel().closeFuture().sync();

        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destory(){
        mainGrop.shutdownGracefully().syncUninterruptibly();
        subGroup.shutdownGracefully().syncUninterruptibly();
        logger.info("关闭netty成功");
    }



}
