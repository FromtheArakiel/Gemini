package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonElement;

public interface JsonClassHandler<T> {
   JsonElement toJson(Object var1);

   T fromJson(JsonElement var1);
}
