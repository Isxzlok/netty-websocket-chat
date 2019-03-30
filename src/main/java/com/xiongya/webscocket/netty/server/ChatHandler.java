package com.xiongya.webscocket.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author xiongzhilong
 * @Email 2584496774@qq.com
 * @Date create by 2019-03-29 11:15
 */
//TextWebSocketFrame在netty中是用于webSocket专门处理文本对象，frame是消息的载体
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final static Logger logger = LoggerFactory.getLogger(ChatHandler.class);

    //用于记录和管理所有客户端的Channel
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm");

    //当接收到客户端发送过来的信息就会触发此回调
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {

        String content = textWebSocketFrame.text();
        System.out.println("接收到的数据："+content);
        //将消息发送给其他客户端排除自己
        Channel channel = channelHandlerContext.channel();

        //将接收到的消息发送给其他客户端
        for (Channel ch : channels){
            //排除当前通道
            if(channel != ch){
                ch.writeAndFlush(new TextWebSocketFrame(sdf.format(new Date()) + ":" + content));
            }
        }

    }

    //当有新的客户端连接服务器之后，就会自动调用这个方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
    }
}
