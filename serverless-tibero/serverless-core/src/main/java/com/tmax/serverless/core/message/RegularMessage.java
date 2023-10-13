package com.tmax.serverless.core.message;

import com.tmax.serverless.core.annotation.GsonExclude;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/* |                   header                    |      body       |
 * | magic number | flag | type | payload length | ... payload ... |
 * 0              4      8     12               16
 */
@Slf4j
@GsonExclude
@Data
public class RegularMessage {

  public static final int SIZE = 16;
  private static final int DEFAULT_MAGIC_NUMBER = 0x000016B5;

  private int msgMagicNumber = DEFAULT_MAGIC_NUMBER;
  private int msgFlag = 0;
  private int msgType = 0;
  private int msgLength = 0;
  private byte[] bytes = null;

  public RegularMessage(int type) {
    this.msgType = type;
  }

  public RegularMessage(int type, int length) {
    this.msgType = type;
    this.msgLength = length;
  }

  public RegularMessage(ByteBuf in) {
    msgMagicNumber = in.readIntLE();
    msgFlag = in.readIntLE();
    msgType = in.readIntLE();
    msgLength = in.readIntLE();
    bytes = new byte[getTotalLength()];

    if (log.isDebugEnabled()) {
      log.info("msgMagicNumber: {}", msgMagicNumber);
      log.info("msgFlag: {}", msgFlag);
      log.info("msgType: {}", msgType);
      log.info("msgLength: {}", msgLength);
    }
  }

  public RegularMessage(RegularMessage header) {
    msgMagicNumber = header.getMsgMagicNumber();
    msgFlag = header.getMsgFlag();
    msgType = header.getMsgType();
    msgLength = header.getMsgLength();
    bytes = header.bytes;
  }

  public int getTotalLength() {
    return SIZE + msgLength;
  }

  public void setBytes(ByteBuf in) {
    in.resetReaderIndex();
    in.readBytes(bytes, 0, getTotalLength());
  }

  public byte[] getPayload() {
    return Arrays.copyOfRange(bytes, SIZE, bytes.length);
  }
}
