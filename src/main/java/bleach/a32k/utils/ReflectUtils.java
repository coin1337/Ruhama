package bleach.a32k.utils;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.lang3.reflect.*;

public class ReflectUtils
{
    public static Field getField(final Class<?> c, final String... names) {
        final int length = names.length;
        int i = 0;
        while (i < length) {
            final String s = names[i];
            try {
                final Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                final Field f = c.getDeclaredField(s);
                f.setAccessible(true);
                modifiersField.setInt(f, f.getModifiers() & 0xFFFFFFEF);
                return f;
            }
            catch (Exception ex) {
                ++i;
            }
        }
        System.out.println("Invalid Fields: " + Arrays.asList(names) + " For Class: " + c.getName());
        return null;
    }
    
    public static Object callMethod(final Object target, final Object[] params, final String... names) {
        final int length = names.length;
        int i = 0;
        while (i < length) {
            final String s = names[i];
            try {
                return MethodUtils.invokeMethod(target, true, s, params);
            }
            catch (Exception ex) {
                ++i;
            }
        }
        System.out.println("Invalid Method: " + Arrays.asList(names));
        return null;
    }
}
