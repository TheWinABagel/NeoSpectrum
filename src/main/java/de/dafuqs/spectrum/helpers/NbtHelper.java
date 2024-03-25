package de.dafuqs.spectrum.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.*;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Optional;

public class NbtHelper {
	public static Optional<CompoundTag> getNbtCompound(JsonElement json) {
		if (json == null || json.isJsonNull()) {
			return Optional.empty();
		}
		
		if (json.isJsonObject()) {
			return Optional.of(fromJsonObject(json.getAsJsonObject()));
		}
		
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			try {
				return Optional.of(TagParser.parseTag(json.getAsString()));
			} catch (CommandSyntaxException exception) {
				exception.printStackTrace();
			}
		}
		
		throw new UnsupportedOperationException("Nbt element is not an object or a string");
	}
	
	public static byte getJsonElementType(JsonElement element) {
		if (element == null)
			throw new UnsupportedOperationException("Null JSON NBT element");
		if (element.isJsonObject())
			return Tag.TAG_COMPOUND;
		if (element.isJsonArray())
			return getJsonArrayType(element.getAsJsonArray());
		if (element.isJsonPrimitive())
			return getJsonPrimitiveType(element.getAsJsonPrimitive());
		
		throw new UnsupportedOperationException("Unknown JSON NBT element type");
	}
	
	public static byte getJsonArrayType(JsonArray array) {
		if (array == null) {
			throw new UnsupportedOperationException("Null JSON NBT element");
		}
		
		if (array.size() > 0) {
			JsonElement first = array.get(0);
			byte type = getJsonElementType(first);
			
			if (type == Tag.TAG_STRING) {
				switch (first.getAsString()) {
					case "B;" -> {
						return Tag.TAG_BYTE_ARRAY;
					}
					case "I;" -> {
						return Tag.TAG_INT_ARRAY;
					}
					case "L;" -> {
						return Tag.TAG_LONG_ARRAY;
					}
				}
			}
		}
		
		return Tag.TAG_LIST;
	}
	
	public static byte getJsonPrimitiveType(JsonPrimitive primitive) {
		if (primitive == null) {
			throw new UnsupportedOperationException("Null JSON NBT element");
		}
		
		if (primitive.isBoolean()) {
			return Tag.TAG_BYTE;
		}
		
		if (primitive.isNumber()) {
			// The numbers will either be integers or doubles. Since JSON
			// doesn't differentiate, we'll get it as a decimal and check
			// if there's a fractional part. It can't tell 1.0 versus 1,
			// but you can always specify with suffixes.
			
			BigDecimal bigDecimal = primitive.getAsBigDecimal();
			try {
				bigDecimal.intValueExact();
				return Tag.TAG_INT;
			} catch (ArithmeticException exception) {
				return Tag.TAG_DOUBLE;
			}
		}
		
		if (primitive.isString()) {
			String string = primitive.getAsString();
			if (string.length() > 1) {
				String numStr = string.substring(0, string.length()-1);
				if (NumberUtils.isParsable(numStr)) {
					switch (string.charAt(string.length() - 1)) {
						case 'b', 'B' -> {
							return Tag.TAG_BYTE;
						}
						case 's', 'S' -> {
							return Tag.TAG_SHORT;
						}
						case 'i', 'I' -> {
							return Tag.TAG_INT;
						}
						case 'l', 'L' -> {
							return Tag.TAG_LONG;
						}
						case 'f', 'F' -> {
							return Tag.TAG_FLOAT;
						}
						case 'd', 'D' -> {
							return Tag.TAG_DOUBLE;
						}
					}
				}
			}
			
			return Tag.TAG_STRING;
		}
		
		throw new UnsupportedOperationException("Unknown JSON NBT primitive type");
	}
	
	public static Tag fromJson(JsonElement element) {
		if (element == null)
			throw new UnsupportedOperationException("Null JSON NBT element");
		if (element.isJsonObject())
			return fromJsonObject(element.getAsJsonObject());
		if (element.isJsonArray())
			return fromJsonArray(element.getAsJsonArray());
		if (element.isJsonPrimitive())
			return fromJsonPrimitive(element.getAsJsonPrimitive());
		
		throw new UnsupportedOperationException("Unknown JSON NBT element type");
	}
	
	public static CompoundTag fromJsonObject(JsonObject object) {
		if (object == null) {
			throw new UnsupportedOperationException("Null JSON NBT element");
		}
		
		CompoundTag result = new CompoundTag();
		
		object.entrySet().forEach(entry -> {
			String name = entry.getKey();
			JsonElement element = entry.getValue();
			if (element != null) {
				result.put(name, fromJson(element));
			}
		});
		
		return result;
	}
	
	public static CollectionTag<?> fromJsonArray(JsonArray array) {
		byte type = getJsonArrayType(array);
		
		if (type == Tag.TAG_LIST) {
			ListTag list = new ListTag();
			for (int i = 0; i < array.size(); i++) {
				list.add(i, fromJson(array.get(i)));
			}
			return list;
		}
		
		CollectionTag<?> nbtArray = switch (type) {
			case Tag.TAG_BYTE_ARRAY -> new ByteArrayTag(new byte[0]);
			case Tag.TAG_INT_ARRAY -> new IntArrayTag(new int[0]);
			case Tag.TAG_LONG_ARRAY -> new LongArrayTag(new long[0]);
			default -> throw new UnsupportedOperationException("Unknown JSON NBT list type");
		};
		
		for (int i = 1; i < array.size(); i++) {
			nbtArray.addTag(i - 1, fromJson(array.get(i)));
		}
		
		return nbtArray;
	}
	
	public static Tag fromJsonPrimitive(JsonPrimitive primitive) {
		byte type = getJsonPrimitiveType(primitive);
		
		if (primitive.isBoolean()) {
			return ByteTag.valueOf((byte)(primitive.getAsBoolean() ? 1 : 0));
		}
		
		if (primitive.isNumber()) {
			switch (type) {
				case Tag.TAG_INT -> {
					return IntTag.valueOf(primitive.getAsInt());
				}
				case Tag.TAG_DOUBLE -> {
					return DoubleTag.valueOf(primitive.getAsDouble());
				}
			}
		}
		
		if (primitive.isString()) {
			String string = primitive.getAsString();
			if (string.length() > 1) {
				String numStr = string.substring(0, string.length()-1);
				switch (type) {
					case Tag.TAG_BYTE -> {
						return ByteTag.valueOf(Byte.parseByte(numStr));
					}
					case Tag.TAG_SHORT -> {
						return ShortTag.valueOf(Short.parseShort(numStr));
					}
					case Tag.TAG_INT -> {
						return IntTag.valueOf(Integer.parseInt(numStr));
					}
					case Tag.TAG_LONG -> {
						return LongTag.valueOf(Long.parseLong(numStr));
					}
					case Tag.TAG_FLOAT -> {
						return FloatTag.valueOf(Float.parseFloat(numStr));
					}
					case Tag.TAG_DOUBLE -> {
						return DoubleTag.valueOf(Double.parseDouble(numStr));
					}
				}
			}
			
			return StringTag.valueOf(string);
		}
		
		throw new UnsupportedOperationException("Unknown JSON NBT primitive type");
	}
	
	/**
	 * Writes the delta into the original, maintaining the previous data unless
	 * overwritten.
	 * <p>
	 * If the provided elements are primitives, the delta will be returned.
	 * If the provided elements are arrays or lists, the delta will be returned.
	 * If the provided elements are compound, new keys will be added and existing
	 * keys will be merged.
	 * Otherwise, the original will be returned.
	 */
	public static void mergeNbt(Tag original, Tag delta) {
		if (original.getId() != delta.getId()) {
			return;
		}
		
		switch (original.getId()) {
			case Tag.TAG_BYTE, Tag.TAG_SHORT, Tag.TAG_ANY_NUMERIC, Tag.TAG_LONG, Tag.TAG_FLOAT, Tag.TAG_DOUBLE, Tag.TAG_STRING, Tag.TAG_END,
					Tag.TAG_BYTE_ARRAY, Tag.TAG_INT_ARRAY, Tag.TAG_LONG_ARRAY, Tag.TAG_LIST -> {
				
			}
			case Tag.TAG_COMPOUND -> {
				CompoundTag originalCompound = (CompoundTag) original;
				CompoundTag deltaCompound = (CompoundTag) delta;
				
				deltaCompound.getAllKeys().forEach(key -> {
					Tag value = deltaCompound.get(key);
					
					if (originalCompound.contains(key)) {
						mergeNbt(originalCompound.get(key), value);
					} else {
						originalCompound.put(key, value);
					}
				});
				
			}
			default -> {
			}
		}
	}
}
