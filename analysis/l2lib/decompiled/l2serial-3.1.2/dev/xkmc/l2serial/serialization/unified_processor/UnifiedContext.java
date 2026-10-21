package dev.xkmc.l2serial.serialization.unified_processor;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2serial.serialization.generic_types.CodecReg;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.serialization.type_cache.ClassCache;
import dev.xkmc.l2serial.serialization.type_cache.FieldCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import java.util.Optional;
import javax.annotation.Nullable;

public abstract class UnifiedContext<E, O, A> {
   protected UnifiedContext() {
   }

   public abstract boolean hasSpecialHandling(Class<?> var1);

   public abstract Object deserializeSpecial(Class<?> var1, E var2);

   public abstract E serializeSpecial(Class<?> var1, Object var2);

   public abstract Object deserializeCodec(CodecReg<?> var1, E var2);

   public abstract E serializeCodec(CodecReg<?> var1, Object var2);

   public abstract Optional<Either<Optional<Object>, TypeInfo>> fetchRealClass(@Nullable E var1, TypeInfo var2) throws Exception;

   public abstract Optional<Pair<E, Optional<ClassCache>>> writeRealClass(TypeInfo var1, @Nullable Object var2) throws Exception;

   public abstract boolean shouldRead(O var1, FieldCache var2) throws Exception;

   public abstract E retrieve(O var1, String var2);

   public abstract A castAsList(E var1);

   public abstract int getSize(A var1);

   public abstract E getElement(A var1, int var2);

   public abstract boolean isListFormat(E var1);

   public abstract O castAsMap(E var1);

   public abstract E getKeyOfEntry(O var1);

   public abstract E getValueOfEntry(O var1);

   public abstract void setKeyOfEntry(O var1, E var2);

   public abstract void setValueOfEntry(O var1, E var2);

   public abstract Object deserializeEfficientMap(E var1, TypeInfo var2, TypeInfo var3, Object var4) throws Exception;

   public abstract String getAsString(E var1);

   public abstract void addField(O var1, String var2, E var3);

   public abstract void addOptionalClass(O var1, Class<?> var2, Class<?> var3);

   public abstract A createList(int var1);

   public abstract O createMap();

   public abstract void addListItem(A var1, E var2);

   public abstract boolean canBeString(E var1);

   public abstract E fromString(String var1);

   public abstract boolean shouldWrite(SerialField var1);
}
