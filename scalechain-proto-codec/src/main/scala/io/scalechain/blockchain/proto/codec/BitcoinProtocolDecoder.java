/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.scalechain.blockchain.proto.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.scalechain.blockchain.proto.ProtocolMessage;
import scodec.bits.ByteVector;
import scodec.bits.ByteVector$;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Decodes a received {@link ByteBuf} into a case class that represents Bitcoin protocol message.
 */
@Sharable
public class BitcoinProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {
    /**
     * Creates a new instance with the current system character set.
     */
    public BitcoinProtocolDecoder() {
    }

    private BitcoinProtocolCodec codec = new BitcoinProtocolCodec( new BitcoinProtocol() );

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        System.out.println("[Debug] BitcoinProtocolDecoder Called ");

        ProtocolMessage message = codec.decode(msg.array());

        System.out.println("[Debug] BitcoinProtocolDecoder : " + message);

        out.add(message);
    }
}
