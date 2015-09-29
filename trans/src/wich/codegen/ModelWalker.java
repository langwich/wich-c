package wich.codegen;

import org.antlr.symtab.Utils;
import wich.codegen.model.ModelElement;
import wich.codegen.model.OutputModelObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// visitor methods:
// return null means delete. return same object means don't replace. return diff object means replace.

public class ModelWalker {
	public static final Object NOTFOUND = new Object();

	protected final Object listener;
	protected final Map<Class<?>, Object> visitorMethodCache = new HashMap<>();
	protected Object visitEveryModelObjectMethodCache = null;

	public ModelWalker(Object listener) {
		this.listener = listener;
	}

	public OutputModelObject walk(OutputModelObject omo) {
		if ( omo==null ) return null;

		final OutputModelObject result1 = visit(omo);
		final OutputModelObject result2 = visitEveryModelObject(omo);

		Class<? extends OutputModelObject> cl = omo.getClass();
		Field[] allAnnotatedFields = Utils.getAllAnnotatedFields(cl);
		List<Field> modelFields = new ArrayList<>();
		for (Field fi : allAnnotatedFields) {
			ModelElement annotation = fi.getAnnotation(ModelElement.class);
			if ( annotation != null ) {
				modelFields.add(fi);
			}
		}

		// WALK EACH NESTED MODEL OBJECT MARKED WITH @ModelElement
		for (Field fi : modelFields) {
			ModelElement annotation = fi.getAnnotation(ModelElement.class);
			if (annotation == null) {
				continue;
			}

			String fieldName = fi.getName();
			try {
				Object o = fi.get(omo);
				if ( o instanceof OutputModelObject ) {  // SINGLE MODEL OBJECT?
					OutputModelObject nestedOmo = (OutputModelObject)o;
					final OutputModelObject replacement = walk(nestedOmo);
					if ( replacement!=null ) {
						fi.set(omo, replacement);
					}
//					System.out.println("set ModelElement "+fieldName+"="+nestedST+" in "+templateName);
				}
				else if ( o instanceof OutputModelObject[] ) {
					OutputModelObject[] elems = (OutputModelObject[])o;
					for (int i = 0; i < elems.length; i++) {
						OutputModelObject nestedOmo = elems[i];
						if ( nestedOmo==null ) continue;
						final OutputModelObject replacement = walk((OutputModelObject) nestedOmo);
						if ( replacement!=null ) {
							fi.set(omo, replacement);
						}
//						System.out.println("set ModelElement "+fieldName+"="+nestedST+" in "+templateName);
					}
				}
				else if ( o instanceof Collection ) {
					Collection<?> nestedOmos = (Collection<?>)o;
					for (Object nestedOmo : nestedOmos) {
						if ( nestedOmo==null ) continue;
						walk((OutputModelObject)nestedOmo);
//						System.out.println("set ModelElement "+fieldName+"="+nestedST+" in "+templateName);
					}
				}
				else if ( o instanceof Map ) {
					Map<?, ?> nestedOmoMap = (Map<?, ?>)o;
					for (Map.Entry<?, ?> entry : nestedOmoMap.entrySet()) {
						walk((OutputModelObject)entry.getValue());
//						System.out.println("set ModelElement "+fieldName+"="+nestedST+" in "+templateName);
					}
				}
				else if ( o!=null ) {
					System.err.println("type of "+fieldName+"'s model element isn't recognized: "+o.getClass().getSimpleName());
				}
			}
			catch (IllegalAccessException iae) {
				System.err.printf("Can't access field: "+fieldName+" in "+cl.getSimpleName());
			}
		}

		return result1!=null ? result1 : result2;
	}

	/** Use reflection to find & invoke overloaded visit(modeltype) method */
	protected OutputModelObject visit(OutputModelObject omo) {
		final Method m = getVisitorMethodForType(omo.getClass());
		return execVisit(omo, m);
	}

	protected OutputModelObject visitEveryModelObject(OutputModelObject omo) {
		final Method m = getVisitEveryNodeMethod();
		return execVisit(omo, m);
	}

	protected OutputModelObject execVisit(OutputModelObject omo, Method m) {
		Object result = null;
		if ( m!=null ) {
			try {
				result = m.invoke(listener, omo);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return (OutputModelObject)result;
	}

	protected Method getVisitorMethodForType(Class cl) {
		Object m = visitorMethodCache.get(cl); // reflection is slow; cache.
		if ( m!=null ) {
			if ( m==NOTFOUND ) {
				return null;
			}
			return (Method)m;
		}
		try {
			m = listener.getClass().getMethod("visit", cl);
			visitorMethodCache.put(cl, m);
		}
		catch (NoSuchMethodException nsme) {
			m = null;
			visitorMethodCache.put(cl, NOTFOUND);
		}
		return (Method)m;
	}

	protected Method getVisitEveryNodeMethod() {
		if ( visitEveryModelObjectMethodCache!=null ) {
			if ( visitEveryModelObjectMethodCache==NOTFOUND ) {
				return null;
			}
			return (Method)visitEveryModelObjectMethodCache;
		}
		Method m;
		try {
			m = listener.getClass().getMethod("visitEveryModelObject", OutputModelObject.class);
			visitEveryModelObjectMethodCache = m;
		}
		catch (NoSuchMethodException nsme) {
			m = null;
			visitEveryModelObjectMethodCache = NOTFOUND;
		}
		return m;
	}
}
