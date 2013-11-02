package org.djv.stockresearcher.util;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class ArrayAdapterFactory<T> implements TypeAdapterFactory {
	@SuppressWarnings({ "unchecked", "rawtypes" }) 
	@Override 
	public TypeAdapter create(final Gson gson, final TypeToken type) {

		TypeAdapter<T> typeAdapter = null;
		System.out.println("type.getRawType()"+type.getRawType());
		try {
			if (type.getRawType() == ArrayList.class)
			{

				typeAdapter = new ArrayAdapter(
						(Class) ((ParameterizedType) type.getType())
						.getActualTypeArguments()[0]);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}

		return typeAdapter;
	}
}
