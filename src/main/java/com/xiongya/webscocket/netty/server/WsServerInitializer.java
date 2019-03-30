package com.xiongya.webscocket.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Author xiongzhilong
 * @Email 2584496774@qq.com
 * @Date create by 2019-03-29 11:06
 *
 * 通道初始化器
 * 用来加载通道处理器
 */
public class WsServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();

        //用于支持http协议
        //websocket基于http协议，需要http的编码器
        pipeline.addLast(new HttpServerCodec())
                //对大数据的支持
                .addLast(new ChunkedWriteHandler())
                //添加对Http请求和响应的聚合器，只要Netty进行编码都需要使用
                //对HttpMessage进行聚合，聚合成FullHttpRequest获取FullHttpResponse
                .addLast(new HttpObjectAggregator(1024*64))
                //------------支持wesocket----------
                //需要指定接受请求的路由，处理握手动作
                //必须使用ws后缀结尾的url才能访问
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                //添加自定义的handle
                .addLast(new ChatHandler());

    }
}
