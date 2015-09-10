package wich.semantics.type;

/**
 * Created by Shuai on 9/9/15.
 */
public class WVector extends WPrimitiveType {

    protected static WVector instance = new WVector("vector");

    public static WVector instance() {
        return instance;
    }

    protected WVector(String name) {
        super(name);
    }
}