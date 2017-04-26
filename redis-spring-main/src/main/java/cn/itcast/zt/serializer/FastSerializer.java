package cn.itcast.zt.serializer;

import cn.itcast.zt.exception.JRedisCacheException;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by zhangtian on 2017/4/26.
 */
public class FastSerializer implements RedisSerializer {

    @Override
    public byte[] serialize(Object obj) throws JRedisCacheException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FSTObjectOutput out = null;
        try {
            // stream closed in the finally
            byteArrayOutputStream = new ByteArrayOutputStream(512);
            out = new FSTObjectOutput(byteArrayOutputStream);  //32000  buffer size
            out.writeObject(obj);
            out.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            throw new JRedisCacheException(ex);
        } finally {
            try {
                obj = null;
                if (out != null) {
                    out.close();    //call flush byte buffer
                    out = null;
                }
                if (byteArrayOutputStream != null) {

                    byteArrayOutputStream.close();
                    byteArrayOutputStream = null;
                }
            } catch (IOException ex) {
                // ignore close exception
                ex.printStackTrace();
            }
        }
    }

    @Override
    public Object deserialize(byte[] objectData) throws JRedisCacheException {
        ByteArrayInputStream byteArrayInputStream = null;
        FSTObjectInput in = null;
        try {
            // stream closed in the finally
            byteArrayInputStream = new ByteArrayInputStream(objectData);
            in = new FSTObjectInput(byteArrayInputStream);
            return in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new JRedisCacheException(ex);
        } catch (IOException ex) {
            throw new JRedisCacheException(ex);
        } finally {
            try {
                objectData = null;
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                    byteArrayInputStream = null;
                }
            } catch (IOException ex) {
                // ignore close exception
                ex.printStackTrace();
            }
        }
    }
}
