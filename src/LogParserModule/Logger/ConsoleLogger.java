package LogParserModule.Logger;

public class ConsoleLogger implements Logger
{

    @Override
    public void output(String str) {
        System.out.println(str);
    }

    @Override
    public void output(int i) {
        System.out.println(Integer.toString(i));
    }

    @Override
    public void output(long l) {
        System.out.println(Long.toString(l));
    }

    @Override
    public void output(float f) {
        System.out.println(Float.toString(f));
    }

    @Override
    public void output(double d) {
        System.out.println(Double.toString(d));
    }
}
