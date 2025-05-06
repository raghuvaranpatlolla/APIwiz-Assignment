import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.python.util.PythonInterpreter;
import org.python.core.PyObject;
import org.python.core.PyInteger;
import org.python.core.PyFloat;
import org.python.core.PyString;
import org.python.core.PyList;
import org.python.core.PyDictionary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ScriptingEngine {

    public Object runScript(String language, String script) {
        if ("JavaScript".equalsIgnoreCase(language)) {
            return executeJavaScript(script);
        } else if ("python".equalsIgnoreCase(language)) {
            return executePython(script);
        } else {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private Object executeJavaScript(String script) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
        try {
            return jsEngine.eval(script);
        } catch (ScriptException e) {
            throw new RuntimeException("JavaScript execution error", e);
        } catch (NullPointerException e) {
            throw new RuntimeException("JavaScript engine not found", e);
        }
    }

    private Object executePython(String script) {
        PythonInterpreter pyInterpreter = new PythonInterpreter();
        try {
            pyInterpreter.exec(script);
            PyObject pyResult = pyInterpreter.get("result");
            return convertPyObjectToJava(pyResult); // Use your conversion method
        } catch (Exception e) {
            throw new RuntimeException("Python execution error", e);
        } finally {
            pyInterpreter.close();
        }
    }

    private Object convertPyObjectToJava(PyObject pyObj) {
        if (pyObj == null) {
            return null;
        } else if (pyObj instanceof PyInteger) {
            return ((PyInteger) pyObj).getValue();
        } else if (pyObj instanceof PyFloat) {
            return ((PyFloat) pyObj).getValue();
        } else if (pyObj instanceof PyString) {
            return ((PyString) pyObj).getString();
        } else if (pyObj instanceof PyList) {
            return convertPyListToJava((PyList) pyObj);
        } else if (pyObj instanceof PyDictionary) {
            return convertPyDictToJava((PyDictionary) pyObj);
        } else {
            return pyObj.__tojava__(Object.class); // Generic conversion (may not always work perfectly)
        }
    }

    private List<Object> convertPyListToJava(PyList pyList) {
        List<Object> javaList = new ArrayList<>();
        for (int i = 0; i < pyList.size(); i++) {
            javaList.add(convertPyObjectToJava(pyList.get(i)));
        }
        return javaList;
    }

    private Map<Object, Object> convertPyDictToJava(PyDictionary pyDict) {
        Map<Object, Object> javaMap = new HashMap<>();
        for (Object key : pyDict.keys()) {
            PyObject pyKey = (PyObject) key;
            PyObject pyValue = pyDict.__getitem__(pyKey);
            javaMap.put(convertPyObjectToJava(pyKey), convertPyObjectToJava(pyValue));
        }
        return javaMap;
    }

    public static void main(String[] args) {
        ScriptingEngine engine = new ScriptingEngine();

        // JavaScript Example (should hopefully work if no classpath issues)
        String jsScript = "2 + 2;";
        Object jsResult = engine.runScript("JavaScript", jsScript);
        System.out.println("JavaScript Result: " + jsResult);

        // Python Example - Make sure your Python script assigns to 'result'
        String pyIntScript = "result = 5";
        Object pyIntResult = engine.runScript("python", pyIntScript);
        System.out.println("Python Integer Result: " + pyIntResult);

        String pyStringScript = "result = 'hello from python'";
        Object pyStringResult = engine.runScript("python", pyStringScript);
        System.out.println("Python String Result: " + pyStringResult);

        String pyListScript = "result = [1, 2, 'three']";
        Object pyListResult = engine.runScript("python", pyListScript);
        System.out.println("Python List Result: " + pyListResult);

        String pyDictScript = "result = {'key': 'value', 'number': 10}";
        Object pyDictResult = engine.runScript("python", pyDictResult);
        System.out.println("Python Dictionary Result: " + pyDictResult);
    }
}