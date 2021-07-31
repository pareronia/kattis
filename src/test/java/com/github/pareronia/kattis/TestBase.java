package com.github.pareronia.kattis;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class TestBase<T> {
	
	protected final Class<T> klass;

    protected TestBase(final Class<T> klass) {
		this.klass = klass;
	}
    
    protected InputStream setUpInput(final String string) {
        return new ByteArrayInputStream(string.getBytes());
    }
    
	protected List<String> run(final InputStream in)
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
    	
	    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	invokeSolveMethod(in, baos);
        return asList(baos.toString().split("\\r?\\n"));
    }

    private void invokeSolveMethod(
            final InputStream in,
            final ByteArrayOutputStream baos)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        
        getSolveMethod().invoke(constructInstance(in, baos));
    }

    private Method getSolveMethod() throws NoSuchMethodException {
        return this.klass.getDeclaredMethod("solve");
    }

    private T constructInstance(
            final InputStream in,
            final ByteArrayOutputStream baos)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        
        final Constructor<T> constructor
    			= this.klass.getDeclaredConstructor(
    			        Boolean.class, InputStream.class, PrintStream.class);
        final PrintStream out = new PrintStream(baos, true);
        return constructor.newInstance(FALSE, in, out);
    }
}
