package com.tom.common.localcache.config.jasckson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.cache.support.NullValue;

import java.io.IOException;

/**
 * 初始化ObjectMapper的时候加上这个
 */
public class NullValueSerializer extends StdSerializer<NullValue> {

    public NullValueSerializer() {
        super(NullValue.class);
    }

    @Override
    public void serialize(NullValue nullValue, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeString("null");
    }
}
