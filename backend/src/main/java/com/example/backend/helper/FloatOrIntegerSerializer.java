package com.example.backend.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class FloatOrIntegerSerializer extends JsonSerializer<Float> {
    @Override
    public void serialize(Float value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // Check if the float has no fractional part (e.g., 123.0)
            if (value % 1 == 0) {
                // Write it as an integer
                gen.writeRawValue(String.valueOf(value.intValue()));
            } else {
                // Write it as a float
                gen.writeRawValue(value.toString());
            }
        }
    }
}
