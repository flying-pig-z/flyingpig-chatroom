package com.flyingpig.chat.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.io.IOException;

public class WebSocketSessionSerializer extends JsonSerializer<StandardWebSocketSession> {

    @Override
    public void serialize(StandardWebSocketSession session, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", session.getId());
        gen.writeStringField("uri", session.getUri().toString());
        gen.writeObjectField("attributes", session.getAttributes());
        // 这里可以添加更多字段，比如创建时间等
        gen.writeEndObject();
    }
}
