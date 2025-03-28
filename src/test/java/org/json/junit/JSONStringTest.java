package org.json.junit;

/*
Public Domain.
*/

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.*;

import org.json.*;
import org.junit.Test;

/**
 * Tests for JSONString implementations, and the difference between
 * {@link JSONStringer#valueToString} and {@link JSONStringer#writeValue}.
 */
public class JSONStringTest {

    /**
     * This tests the JSONObject.writeValue() method. We can't test directly
     * due to it being a package-protected method. Instead, we can call
     * JSONArray.write(), which delegates the writing of each entry to
     * writeValue().
     */
    @Test
    public void writeValues() throws Exception {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put((Object)null);

        StringWriter writer = new StringWriter();
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[null]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put(JSONObject.NULL);
        } finally {
            writer.close();
        }

        writer = new StringWriter();
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[null]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put(new JSONObject());
        } finally {
            writer.close();
        }
        
        writer = new StringWriter();
        try  {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[{}]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put(new JSONArray());
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[[]]".equals(output));
    
            jsonArray = new JSONArray();
            Map<?,?> singleMap = Collections.singletonMap("key1", "value1");
            jsonArray.put((Object)singleMap);
        } finally {
            writer.close();
        }
        
        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[{\"key1\":\"value1\"}]".equals(output));
    
            jsonArray = new JSONArray();
            List<?> singleList = Collections.singletonList("entry1");
            jsonArray.put((Object)singleList);
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[[\"entry1\"]]".equals(output));
    
            jsonArray = new JSONArray();
            int[] intArray = new int[] { 1, 2, 3 };
            jsonArray.put(intArray);
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[[1,2,3]]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put(24);
        } finally {
            writer.close();
        }
        
        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[24]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put("string value");
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[\"string value\"]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put(true);
        } finally {
            writer.close();
        }

        writer = new StringWriter();
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[true]".equals(output));
        } finally {
            writer.close();
        }

    }

    /**
     * This tests the JSONWriter.valueToString() method. These should be
     * identical to the values above, except for the enclosing [ and ].
     */
    @SuppressWarnings("boxing")
    @Test
    public void valuesToString() throws Exception {

        String output = JSONWriter.valueToString(null);
        assertTrue("String values should be equal", "null".equals(output));

        output = JSONWriter.valueToString(JSONObject.NULL);
        assertTrue("String values should be equal", "null".equals(output));

        output = JSONWriter.valueToString(new JSONObject());
        assertTrue("String values should be equal", "{}".equals(output));

        output = JSONWriter.valueToString(new JSONArray());
        assertTrue("String values should be equal", "[]".equals(output));

        Map<?,?> singleMap = Collections.singletonMap("key1", "value1");
        output = JSONWriter.valueToString(singleMap);
        assertTrue("String values should be equal", "{\"key1\":\"value1\"}".equals(output));

        List<?> singleList = Collections.singletonList("entry1");
        output = JSONWriter.valueToString(singleList);
        assertTrue("String values should be equal", "[\"entry1\"]".equals(output));

        int[] intArray = new int[] { 1, 2, 3 };
        output = JSONWriter.valueToString(intArray);
        assertTrue("String values should be equal", "[1,2,3]".equals(output));

        output = JSONWriter.valueToString(24);
        assertTrue("String values should be equal", "24".equals(output));

        output = JSONWriter.valueToString("string value");
        assertTrue("String values should be equal", "\"string value\"".equals(output));

        output = JSONWriter.valueToString(true);
        assertTrue("String values should be equal", "true".equals(output));

    }

    /**
     * Test what happens when toJSONString() returns a well-formed JSON value.
     * This is the usual case.
     */
    @Test
    public void testJSONStringValue() throws Exception {
        JSONStringValue jsonString = new JSONStringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(jsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[\"the JSON string value\"]".equals(output));
    
            output = JSONWriter.valueToString(jsonString);
            assertTrue("String values should be equal", "\"the JSON string value\"".equals(output));
        } finally {
            writer.close();
        }
    }

    /**
     * Test what happens when toJSONString() returns null. In one case,
     * use the object's toString() method. In the other, throw a JSONException.
     */
    @Test
    public void testJSONNullStringValue() throws Exception {
        JSONNullStringValue jsonString = new JSONNullStringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(jsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[\"the toString value\"]".equals(output));
    
            // The only different between writeValue() and valueToString():
            // in this case, valueToString throws a JSONException
            try {
                output = JSONWriter.valueToString(jsonString);
                fail("Expected an exception, got a String value");
            } catch (Exception e) {
                assertTrue("Expected JSONException", e instanceof JSONException);
                assertTrue("Exception message does not match", "Bad value from toJSONString: null".equals(e.getMessage()));
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Test what happens when toJSONString() returns an exception. In both
     * cases, a JSONException is thrown, with the cause and message set from
     * the original exception.
     */
    @Test
    public void testJSONStringExceptionValue() {
        JSONStringExceptionValue jsonString = new JSONStringExceptionValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(jsonString);

        StringWriter writer = new StringWriter(); 
        try {
            jsonArray.write(writer).toString();
            fail("Expected an exception, got a String value");
        } catch (JSONException e) {
            assertEquals("Unable to write JSONArray value at index: 0", e.getMessage());
        } catch(Exception e) {
            fail("Expected JSONException");
        } finally {
            try {
                writer.close();
            } catch (Exception e){}
        }

        try {
            JSONWriter.valueToString(jsonString);
            fail("Expected an exception, got a String value");
        } catch (JSONException e) {
            assertTrue("Exception message does not match", "the exception value".equals(e.getMessage()));
        } catch(Exception e) {
            fail("Expected JSONException");
        }
    }

    /**
     * Test what happens when a Java object's toString() returns a String value.
     * This is the usual case.
     */
    @Test
    public void testStringValue() throws Exception {
        StringValue nonJsonString = new StringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(nonJsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[\"the toString value for StringValue\"]".equals(output));
    
            output = JSONWriter.valueToString(nonJsonString);
            assertTrue("String values should be equal", "\"the toString value for StringValue\"".equals(output));
        } finally {
            writer.close();
        }
    }

    /**
     * Test what happens when a Java object's toString() returns null.
     * Defaults to empty string.
     */
    @Test
    public void testNullStringValue() throws Exception {
        NullStringValue nonJsonString = new NullStringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(nonJsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            assertTrue("String values should be equal", "[\"\"]".equals(output));
    
            output = JSONWriter.valueToString(nonJsonString);
            assertTrue("String values should be equal", "\"\"".equals(output));
        } finally {
            writer.close();
        }
    }

    @Test
    public void testEnumJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", MyEnum.MY_ENUM);
        assertEquals("{\"key\":\"myJsonString\"}", jsonObject.toString());
    }

    private enum MyEnum implements JSONString {
        MY_ENUM;

        @Override
        public String toJSONString() {
            return "\"myJsonString\"";
        }
    }

    /**
     * A JSONString that returns a valid JSON string value.
     */
    private static final class JSONStringValue implements JSONString {

        @Override
        public String toJSONString() {
            return "\"the JSON string value\"";
        }

        @Override
        public String toString() {
            return "the toString value for JSONStringValue";
        }
    }

    /**
     * A JSONString that returns null when calling toJSONString().
     */
    private static final class JSONNullStringValue implements JSONString {

        @Override
        public String toJSONString() {
            return null;
        }

        @Override
        public String toString() {
            return "the toString value";
        }
    }

    /**
     * A JSONString that throw an exception when calling toJSONString().
     */
    private static final class JSONStringExceptionValue implements JSONString {

        @Override
        public String toJSONString() {
            throw new IllegalStateException("the exception value");
        }

        @Override
        public String toString() {
            return "the toString value for JSONStringExceptionValue";
        }
    }

    public static final class StringValue {

        @Override
        public String toString() {
            return "the toString value for StringValue";
        }
    }

    public static final class NullStringValue {

        @Override
        public String toString() {
            return null;
        }
    }
}
