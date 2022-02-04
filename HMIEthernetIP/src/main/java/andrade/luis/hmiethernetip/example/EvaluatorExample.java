package andrade.luis.hmiethernetip.example;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import andrade.luis.hmiethernetip.models.Expression;
import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.views.SelectTagWindow;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;

public class EvaluatorExample {

    public static void
    main(String[] args) throws CompileException, InvocationTargetException {

        // Now here's where the story begins...
        ExpressionEvaluator ee = new ExpressionEvaluator();

        // The expression will have two "int" parameters: "a" and "b".

        ee.setParameters(new String[] { "a", "b" }, new Class[] { int.class, int.class });

        // And the expression (i.e. "result") type is also "int".
        ee.setExpressionType(boolean.class);

        // And now we "cook" (scan, parse, compile and load) the fabulous expression.
        ee.cook("a < b");

        // Eventually we evaluate the expression - and that goes super-fast.
        boolean result = (boolean) ee.evaluate(new Object[] { 19, 23 });
        System.out.println(result);

        SelectTagWindow selectTagWindow = new SelectTagWindow(false);
        selectTagWindow.showAndWait();
        Tag tag = selectTagWindow.getSelectedTag();
        ArrayList<Tag> test = new ArrayList<Tag>();
        test.add(tag);
        String expressionStr = "temperatura*2";
        Expression expression = new Expression(expressionStr,test);
        System.out.println(expression.evaluate());

    }
}